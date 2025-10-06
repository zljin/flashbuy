package com.zljin.flashbuy.exception;

/**
 * 业务异常基类
 */
public class BusinessException extends RuntimeException {

    private final int code;
    private final String message;

    public BusinessException(BusinessExceptionEnum exceptionEnum) {
        super(exceptionEnum.getErrorMessage());
        this.code = exceptionEnum.getErrorCode();
        this.message = exceptionEnum.getErrorMessage();
    }

    public BusinessException(BusinessExceptionEnum exceptionEnum, String customMessage) {
        super(customMessage);
        this.code = exceptionEnum.getErrorCode();
        this.message = customMessage;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    // Getters
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
