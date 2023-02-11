package com.yupi.yupao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.request.TeamUpdateRequest;
import com.yupi.yupao.model.dto.TeamQuery;
import com.yupi.yupao.model.vo.TeamUserVO;

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
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 修改队伍信息
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeams(TeamUpdateRequest teamUpdateRequest, User loginUser);
}
