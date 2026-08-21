package com.example.taobaosimple.common.resp;

/**
 * 统一响应结构
 */
public class RestResp <T>{

    /**
     * 响应码，0 代表成功
     */
    private final String code;

    /**
     * 响应提示信息
     */
    private final String message;

    /**
     * 响应业务数据
     */
    private final T data;

    private RestResp(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public T getData() {
        return data;
    }

    /**
     * 操作成功，无业务数据返回
     */
    public static RestResp<Void> success() {
        return new RestResp<>("0", "操作成功", null);
    }

    /**
     * 操作成功，带业务数据返回
     */
    public static <T> RestResp<T> success(T data){
        return new RestResp<>("0","操作成功",data);
    }

    /**
     * 操作失败，指定错误码和提示
     */
    public static <T> RestResp<T> fail(
            String code,
            String message) {

        return new RestResp<>(code, message, null);
    }
}
