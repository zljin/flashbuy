package com.zljin.flashbuy.exception;

public enum BusinessExceptionEnum {
    PARAMETER_VALIDATION_ERROR(10001, "参数不合法"),
    UNKNOWN_ERROR(10002, "未知错误"),
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户账号或密码不正确"),
    USER_NOT_LOGIN(20003, "用户未登录"),
    USER_NOT_ADMIN(20006, "非管理员用户"),
    USER_OTP_FAIL(20004, "验证码不对"),
    REGISTER_FAIL(20005, "注册失败"),
    STOCK_NOT_ENOUGH(30001, "库存不足"),
    ORDER_ERROR(30002, "生成订单错误,请查看日志"),
    ADD_ITEM_ERROR(30004, "添加商品错误,请查看日志"),
    SQL_ERROR(40001, "SQL错误")
    ;
    private final int errorCode;
    private final String errorMessage;

    BusinessExceptionEnum(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
