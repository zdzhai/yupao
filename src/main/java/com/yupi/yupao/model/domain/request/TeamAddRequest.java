package com.yupi.yupao.model.domain.request;

import lombok.Data;

import java.util.Date;

/**
 * @author dongdong
 * @Date 2023/2/8 20:09
 * 添加队伍请求类
 */
@Data
public class TeamAddRequest {

    /**
     * 队伍ID
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;


}
