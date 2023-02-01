package com.yupi.yupao.mapper;

import com.yupi.yupao.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 62618
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2023-01-26 15:13:30
* @Entity com.yupi.yupao.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




