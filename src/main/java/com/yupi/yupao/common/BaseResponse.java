package com.yupi.yupao.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dongdong
 * @Date 2023/1/4 17:03
 *
 * 通用返回类
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 4312006810014849357L;

    private int code;

    //todo 这里把泛型换成Object有啥区别
    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code,T data){
        this(code,data,"");
    }

    /**
     * 只要有错，就没有返回的data对象值，所以为null
     * @param errorCode
     */
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
