package com.yupi.yupao.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongdong
 * @Date 2023/2/15 20:46
 * 删除队伍请求参数
 */
@Data
public class TeamDeleteRequest implements Serializable {

    private Long id;
}

