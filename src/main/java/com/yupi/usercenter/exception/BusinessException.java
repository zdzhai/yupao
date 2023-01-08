package com.yupi.usercenter.exception;

import com.yupi.usercenter.common.ErrorCode;

/**
 * @author Zhai Zhidong
 * @version 1.0
 * @Date 2023/1/4 17:40
 * 自定义业务处理异常类
 * 在还没有定义全局异常处理器之前，throw出去我们自定义的异常
 * 是java内部自己处理的，只用到了RuntimeException中的message字段
 * 因此我们需要自定义全局异常处理器来处理抛出的异常！！！
 */
public class BusinessException extends RuntimeException{
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
