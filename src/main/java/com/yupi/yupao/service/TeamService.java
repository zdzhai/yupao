package com.yupi.yupao.service;

import com.yupi.yupao.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.domain.request.TeamAddRequest;

/**
* @author dongdong
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-02-06 20:40:07
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User loginUser);
}
