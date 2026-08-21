package com.example.taobaosimple.common.exception;

import com.example.taobaosimple.common.constant.ErrorCodeEnum;
import com.example.taobaosimple.common.resp.RestResp;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常统一返回
     */
    @ExceptionHandler(BusinessException.class)
    public RestResp<Void> handleBusinessException(BusinessException e) {
        // 直接使用枚举中的错误码和提示
        return RestResp.fail(
                e.getErrorCodeEnum().getCode(),
                e.getMessage()
        );
    }

    /**
     * DTO 字段校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResp<Void> handleNotValid(MethodArgumentNotValidException e) {
        // 拼接所有字段错误
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return RestResp.fail(
                ErrorCodeEnum.PARAM_INVALID.getCode(),
                msg
        );
    }

    /**
     * 路径参数 / 方法参数校验失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RestResp<Void> handleConstraintViolation(ConstraintViolationException e) {
        return RestResp.fail(
                ErrorCodeEnum.PARAM_INVALID.getCode(),
                e.getMessage()
        );
    }

    /**
     * 缺少请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public RestResp<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return RestResp.fail(
                ErrorCodeEnum.PARAM_MISSING.getCode(),
                "缺少必填参数: " + e.getParameterName()
        );
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public RestResp<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return RestResp.fail(
                ErrorCodeEnum.PARAM_INVALID.getCode(),
                "参数类型错误: " + e.getName()
        );
    }

    /**
     * 请求体无法解析（空 body / JSON 格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public RestResp<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return RestResp.fail(
                ErrorCodeEnum.REQUEST_BODY_INVALID.getCode(),
                ErrorCodeEnum.REQUEST_BODY_INVALID.getMessage()
        );
    }

    /**
     * 上传文件超出大小限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RestResp<Void> handleUploadTooLarge(MaxUploadSizeExceededException e) {
        return RestResp.fail(
                ErrorCodeEnum.AVATAR_TOO_LARGE.getCode(),
                ErrorCodeEnum.AVATAR_TOO_LARGE.getMessage()
        );
    }

    /**
     * 数据库完整性冲突（唯一索引/外键）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public RestResp<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        return RestResp.fail(
                ErrorCodeEnum.SERVER_ERROR.getCode(),
                "数据冲突，请检查提交内容"
        );
    }

    /**
     * 认证失败（401）
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResp<Void> handleAuth(AuthenticationException e) {
        return RestResp.fail(
                ErrorCodeEnum.UNAUTHORIZED.getCode(),
                ErrorCodeEnum.UNAUTHORIZED.getMessage()
        );
    }

    /**
     * 权限不足（403）
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public RestResp<Void> handleAccessDenied(AccessDeniedException e) {
        return RestResp.fail(
                ErrorCodeEnum.FORBIDDEN.getCode(),
                ErrorCodeEnum.FORBIDDEN.getMessage()
        );
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    public RestResp<Void> handleException(Exception e) {
        return RestResp.fail(
                ErrorCodeEnum.SERVER_ERROR.getCode(),
                ErrorCodeEnum.SERVER_ERROR.getMessage()
        );
    }
}
