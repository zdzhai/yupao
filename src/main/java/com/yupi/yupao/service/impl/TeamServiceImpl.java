package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.mapper.TeamMapper;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.domain.request.TeamUpdateRequest;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.enums.TeamStatusEnum;
import com.yupi.yupao.model.vo.TeamUserVO;
import com.yupi.yupao.model.vo.UserVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Long id = teamQuery.getId();
        if (id != null && id > 0){
            queryWrapper.eq("id",id);
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
        if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
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
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
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
            TeamUserVO teamUserVO = new TeamUserVO();
            //脱敏用户信息
            if (user != null){
                BeanUtils.copyProperties(team,teamUserVO);
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setUserVO(userVO);
                teamUserVOList.add(teamUserVO);
            }
        }
        return teamUserVOList;
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
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间要有密码");
            }
        }
        Team newTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        return this.updateById(newTeam);
    }
}




