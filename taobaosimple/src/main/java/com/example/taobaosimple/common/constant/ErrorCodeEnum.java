package com.example.taobaosimple.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    /**
     * 用户不存在
     */
    USER_NOT_FOUND("1002", "用户不存在"),

    /**
     * 无权限操作
     */
    NO_PERMISSION("1003", "无权操作"),

    /**
     * 地址不存在
     */
    ADDRESS_NOT_FOUND("1004", "地址不存在"),

    /**
     * 地址信息不完整
     */
    ADDRESS_INFO_INVALID("1005", "地址信息不完整"),

    /**
     * 地址更新失败
     */
    ADDRESS_UPDATE_FAILED("1006", "地址更新失败"),

    /**
     * 用户名或密码为空
     */
    AUTH_CREDENTIALS_EMPTY("1007", "用户名或密码不能为空"),

    /**
     * 用户名已存在
     */
    USERNAME_EXISTS("1008", "用户名已存在"),

    /**
     * 注册失败
     */
    REGISTER_FAILED("1009", "注册失败"),

    /**
     * 登录失败（账号或密码错误）
     */
    LOGIN_FAILED("1010", "用户名或密码错误"),

    /**
     * 账号已注销
     */
    ACCOUNT_DELETED("1011", "账号已注销或不存在"),

    /**
     * 头像上传失败
     */
    AVATAR_UPLOAD_FAILED("1012", "头像上传失败"),

    /**
     * 头像格式不支持
     */
    AVATAR_TYPE_NOT_SUPPORTED("1013", "头像格式仅支持 jpg/png"),

    /**
     * 头像大小超出限制
     */
    AVATAR_TOO_LARGE("1014", "头像大小不能超过 5MB"),

    /**
     * 商品不存在或已下架
     */
    GOODS_NOT_FOUND("1015", "商品不存在或已下架"),

    /**
     * 商品库存不足
     */
    GOODS_OUT_OF_STOCK("1016", "库存不足"),

    /**
     * 购物车项不存在
     */
    CART_ITEM_NOT_FOUND("1017", "购物车项不存在"),

    /**
     * 购物车操作失败
     */
    CART_OPERATION_FAILED("1018", "购物车操作失败"),

    /**
     * 参数校验失败
     */
    PARAM_INVALID("4000", "参数校验失败"),

    /**
     * 缺少必填参数
     */
    PARAM_MISSING("4001", "缺少必填参数"),

    /**
     * 请求体格式错误
     */
    REQUEST_BODY_INVALID("4002", "请求体格式错误"),

    /**
     * 未登录或登录已过期
     */
    UNAUTHORIZED("4010", "未登录或登录已过期"),

    /**
     * 无权访问
     */
    FORBIDDEN("4030", "无权访问"),

    /**
     * 服务器内部错误
     */
    SERVER_ERROR("5000", "服务器内部错误");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误提示
     */
    private final String message;

    /**
     * 根据错误码查找枚举
     */
    public static ErrorCodeEnum getErrorCodeByCode(String code) {
        for (ErrorCodeEnum errorCodeEnum : ErrorCodeEnum.values()) {
            if (errorCodeEnum.getCode().equals(code)) {
                return errorCodeEnum;
            }
        }
        return null;
    }
}
