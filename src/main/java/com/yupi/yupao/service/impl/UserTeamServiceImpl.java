package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupao.model.domain.UserTeam;
import com.yupi.yupao.service.UserTeamService;
import com.yupi.yupao.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 62618
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-02-06 20:40:46
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




