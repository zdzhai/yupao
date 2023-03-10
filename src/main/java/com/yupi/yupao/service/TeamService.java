package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.request.TeamJoinRequest;
import com.yupi.yupao.model.domain.request.TeamQuitRequest;
import com.yupi.yupao.model.domain.request.TeamUpdateRequest;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.vo.TeamVO;

import java.util.List;

/**
* @author dongdong
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-02-06 20:40:07
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 展示队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍信息
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeams(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);
}
