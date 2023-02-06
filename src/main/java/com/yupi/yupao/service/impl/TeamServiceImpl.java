package com.yupi.yupao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yupao.model.domain.Team;
import com.yupi.yupao.service.TeamService;
import com.yupi.yupao.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 62618
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-02-06 20:40:07
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




