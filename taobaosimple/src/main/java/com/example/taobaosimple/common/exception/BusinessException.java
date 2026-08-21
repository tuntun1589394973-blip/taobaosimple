package com.example.taobaosimple.common.exception;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 业务异常
 */
@EqualsAndHashCode(callSuper = true)
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 对应错误码枚举
     */
    private final ErrorCodeEnum errorCodeEnum;

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.errorCodeEnum = errorCodeEnum;
    }
}
