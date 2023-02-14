package com.yupi.yupao.model.dto;

import com.yupi.yupao.common.PageRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author dongdong
 * @Date 2023/2/6 20:56
 * 更新队伍数据传输对象
 */
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 返回个人创建的所有队伍id
     */
    private List<Long> idList;
    /**
     * 搜索关键词
     */
    private String searchText;
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



}
