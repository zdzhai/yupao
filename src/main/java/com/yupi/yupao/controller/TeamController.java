package com.yupi.yupao.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.model.domain.request.*;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.vo.TeamVO;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.service.UserService;
import com.yupi.yupao.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author dongdong
 * @Date 2023/1/1 22:34
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = {"http://localhost:5173"},
            allowCredentials = "true")
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;


    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        User loginUser = userService.getLoginUser(request);
        long result = teamService.addTeam(team,loginUser);
        if (result < 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest){
        if (teamDeleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamDeleteRequest.getId();
        //todo service做校验
        boolean result = teamService.removeById(id);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);


    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeams(teamUpdateRequest, loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamVO>> listMyCreateTeam(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamVO> teamList = teamService.listTeams(teamQuery, true);
        teamList = this.calculateHasJoinNum(teamList,request);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamVO>> listMyJoinTeam(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //取出不重复的队伍id
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamVO> teamList = teamService.listTeams(teamQuery, true);
        teamList = this.calculateHasJoinNum(teamList,request);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamVO>> listTeam(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamVO> teamList = teamService.listTeams(teamQuery, isAdmin);
        teamList = this.calculateHasJoinNum(teamList,request);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPages(TeamQuery teamQuery ){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        int pageNum = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        Page<Team> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Page<Team> resultList = teamService.page(page,queryWrapper);
        return ResultUtils.success(resultList);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.NULL_ERROR,"加入用户失败");
        }
        return ResultUtils.success(result);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.NULL_ERROR,"加入用户失败");
        }
        return ResultUtils.success(result);
    }
    /**
     * 计算每个队伍现有的人数
     */
    public List<TeamVO> calculateHasJoinNum(List<TeamVO> teamList,HttpServletRequest request){
        //队伍Id列表
        List<Long> teamIdList = teamList.stream()
                .map(TeamVO::getId)
                .collect(Collectors.toList());
        //判断当前用户是否已加入队伍
        //这里要判断用户是否登录，如果未登录会抛异常，所以用try catch 捕获异常
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            User loginUser = userService.getLoginUser(request);
            userTeamQueryWrapper.eq("userId",loginUser.getId());
            userTeamQueryWrapper.in("teamId",teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //用户已加入队伍的teamId集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream()
                    .map(UserTeam::getTeamId)
                    .collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(hasJoin);
            });
        } catch (Exception e){ }
        //查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamHasNumQueryWrapper = new QueryWrapper<>();
        userTeamHasNumQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamHasNumQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamListMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            int hasJoinNum = teamIdUserTeamListMap.getOrDefault(team.getId(), new ArrayList<>()).size();
            team.setHasJoinNum(hasJoinNum);
        });
        return teamList;
    }
}
