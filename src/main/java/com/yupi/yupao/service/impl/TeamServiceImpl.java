package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.mapper.TeamMapper;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.domain.request.TeamJoinRequest;
import com.yupi.yupao.model.domain.request.TeamQuitRequest;
import com.yupi.yupao.model.domain.request.TeamUpdateRequest;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.enums.TeamStatusEnum;
import com.yupi.yupao.model.vo.TeamVO;
import com.yupi.yupao.model.vo.UserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author dongdong
* @description 针对表【team(队伍)】的数据库操作Service实现
* @Date 2023-02-06 20:40:07
*/
@Service

public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {

        //1.请求参数是否为空？
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.是否登，未登录不允许创建
        if (loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        final long userId = loginUser.getId();
        //3.校验信息
//            a.队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不能超过20个");
        }
//            b.队伍标题<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍标题不满足要求");
        }
//            c.描述<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }
//            d.status是否公开(int)不传默认为0（公开）
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
//            e.如果status是加密状态，一定要有密码，且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) ){
            if (StringUtils.isNotBlank(password) && password.length() > 32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能超过32位");
            }
        }
//            f.超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"超时时间不能小于当前时间");
        }
//            g.校验用户最多创建5个队伍
        //todo 可能同时创建100个队伍 加锁
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"只能创建5个队伍");
        }
        //4.插入队伍信息到队伍表，这里要开启事务，防止两个数据库插入数据不同步
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        Long teamId = team.getId();
        if (!save || teamId == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        //5.插入用户=>队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean result = userTeamService.save(userTeam);
        if (!result){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamVO> listTeams(TeamQuery teamQuery, boolean isAdmin){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Long id = teamQuery.getId();
        if (id != null && id > 0){
            queryWrapper.eq("id",id);
        }
        List<Long> idList = teamQuery.getIdList();
        if (CollectionUtils.isNotEmpty(idList)){
            queryWrapper.in("id",idList);
        }
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)){
            queryWrapper.and(qw -> qw.like("name",searchText)
                    .or().like("description",searchText));
        }
        String name = teamQuery.getName();
        if (StringUtils.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        String description = teamQuery.getDescription();
        if (StringUtils.isNotBlank(description)){
            queryWrapper.like("description",description);
        }
        Integer maxNum = teamQuery.getMaxNum();
        //查询人数为maxNum的队伍
        if (maxNum != null && maxNum > 0){
            queryWrapper.eq("maxNum",maxNum);
        }
        Integer status = teamQuery.getStatus();
        //只有管理员可以查看私密房间
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            statusEnum  = TeamStatusEnum.PUBLIC;
        }
        //如果不是管理员并且访问私密房间
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        queryWrapper.eq("status",statusEnum.getValue());

        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0){
            queryWrapper.eq("userId",userId);
        }
        //不展示已过期的队伍,expireTime is null or expireTime > now
        queryWrapper.and(qw -> qw.gt("expireTime",new Date())
                .or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        List<TeamVO> teamVOList = new ArrayList<>();
        //关联查询用户信息
        //1.查询队伍和队伍创建人的信息
        // select * from team t left join user u on t.userId = u.id;
        //关联查询已加入队伍的用户信息
        //select * from team t join user_team ut on t.id = ut.teamId
        // left join user u on ut.userId = u.id;
        for (Team team: teamList){
            userId = team.getUserId();
            if (userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamVO teamVO = new TeamVO();
            //脱敏用户信息
            if (user != null){
                BeanUtils.copyProperties(team, teamVO);
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamVO.setUserVO(userVO);
                teamVOList.add(teamVO);
            }
        }
        return teamVOList;
    }

    @Override
    public boolean updateTeams(TeamUpdateRequest teamUpdateRequest, User loginUser){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null && id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Long createUserId = oldTeam.getUserId();
        if (!loginUser.getId().equals(createUserId) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Integer status = teamUpdateRequest.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间要有密码");
            }
        }
        Team newTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        return this.updateById(newTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        if (userId == null || userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        //todo 有可能创建退出后重新加入，退出后的房主怎么处理，顺延？不变？
//        if (userId.equals(team.getUserId())){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能加入自己创建的队伍")
//        }
        Date expireTime = team.getExpireTime();
        if (team.getExpireTime() != null && expireTime.before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }
        //如果队伍状态是加密的，则密码必须匹配
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !team.getPassword().equals(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不正确");
            }
        }
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能加入私密队伍");
        }

        RLock lock = redissonClient.getLock("yupao:join_team");
        try {
            // todo 这里和缓存预热不同，不是只有一个线程能获取到锁,并且最好的解决是两把锁
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //该用户已加入的队伍数量
                    QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId",userId);
                    long hasJoinNum = userTeamService.count(queryWrapper);
                    if (hasJoinNum >= 5){
                        throw new BusinessException(ErrorCode.PARAMS_ERROR,"最多创建和加入5个队伍");
                    }
                    //队伍已经加入人数
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("teamId",teamId);
                    long teamHasJoinNum = userTeamService.count(queryWrapper);
                    if (teamHasJoinNum >= team.getMaxNum()){
                        throw new BusinessException(ErrorCode.NULL_ERROR,"队伍人数已满");
                    }
                    //不能加入重复队伍
                    queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("userId",userId);
                    queryWrapper.eq("teamId",teamId);
                    long hasUserJoinTeamNum = userTeamService.count(queryWrapper);
                    if (hasUserJoinTeamNum > 0){
                        throw new BusinessException(ErrorCode.NULL_ERROR,"不能加入重复队伍");
                    }
                    //增加用户-队伍关联信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }
            }
        } catch (InterruptedException e){
            log.error("doCacheRecommendUser error", e);
            return false;
        } finally {
            //只能释放自己的锁
            if (lock.isHeldByCurrentThread()){
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //校验队伍是否存在
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        //校验我是否已加入队伍
        Long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        //查询队伍人数
        QueryWrapper<UserTeam> countUserTeamQueryWrapper = new QueryWrapper<>();
        countUserTeamQueryWrapper.eq("teamId",teamId);
        long teamHasJoinNum = userTeamService.count(countUserTeamQueryWrapper);
        //队伍只剩一人,解散
        if (teamHasJoinNum == 1){
            //删除队伍，删除用户-队伍关系表
            this.removeById(teamId);
            //在外层统一删除用户-队伍关系
//            userTeamService.remove(queryWrapper);
        } else {
            //队伍至少还有两人
            //队长退出队伍，权限转移到第二早加入队伍的用户（只要取id最小的两个用户）
            //todo 权限转移但是应该需要队伍创建人的字段来记录队伍的原始创建人
            if (userId.equals(team.getUserId())){
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId",teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextTeamLeader = userTeamList.get(1);
                Long newLeaderUserId = nextTeamLeader.getUserId();
                //更新新用户为队长
                Team updateTeam = new Team();
                updateTeam.setUserId(newLeaderUserId);
                updateTeam.setId(teamId);
                boolean result = this.updateById(updateTeam);
                if (!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队伍队长失败");
                }
                //移除原队长的用户队伍关系表
            }
            //非队长直接退出队伍,移除用户队伍关系表
        }
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 根据id获取队伍
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}




