package com.yupi.yupao.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 队伍信息返回类
 * @author dongdong
 * @Date 2023/2/10 22:11
 */
@Data
public class TeamVO {
    /**
     * id
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private UserVO userVO;
    /**
     * 已加入用户数
     */
    private Integer hasJoinNum;
    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;
}
