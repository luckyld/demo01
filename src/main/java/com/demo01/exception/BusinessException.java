package com.demo01.exception;

import lombok.Data;

/**
 * created by wangzelong 2019/1/9 11:39
 */
@Data
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 3455708526465670030L;

    private Integer code;
    private String msg;
    private Exception stack;

    public BusinessException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public BusinessException(Integer code, String msg, Exception stack) {
        this.code = code;
        this.msg = msg;
        this.stack = stack;
    }
}
