package com.yupi.yupao.model.domain.request;

import lombok.Data;

/**
 * @author dongdong
 * @Date 2023/2/12 10:23
 */
@Data
public class TeamJoinRequest {

    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 密码
     */
    private String password;
}
