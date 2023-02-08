package com.yupi.yupao.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dongdong
 * @Date 2023/1/24 22:54
 * 星球用户信息表
 */
@Data
@EqualsAndHashCode
public class XingQiuTabUserInfo {
    /**
     * id
     */
    @ExcelProperty("星球编号")
    private String planetCode;
    /**
     * username
     */
    @ExcelProperty("用户昵称")
    private String username;
}
