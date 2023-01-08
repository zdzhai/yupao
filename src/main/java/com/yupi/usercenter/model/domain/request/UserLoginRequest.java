package com.yupi.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhai Zhidong
 * @version 1.0
 * @Date 2023/1/1 22:41
 * 用户注册请求体
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

}
