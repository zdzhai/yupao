package com.yupi.yupao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.yupao.model.domain.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 62618
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2022-12-30 11:10:59
* @Entity com.yupi.usercenter.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




