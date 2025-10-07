package com.zljin.flashbuy.exception;

import com.zljin.flashbuy.model.vo.R;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private BindException e;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<R> handleBusinessException(HttpServletRequest request, BusinessException e) {
        log.warn("业务异常: {} - {} - {}", e.getCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(409).body(R.error(e.getCode(), e.getMessage()));
    }

    /**
     * 处理 @Validated 方法参数校验异常
     * 适用于 @RequestParam、@PathVariable 参数校验
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<R<Void>> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验异常: {} - {} - {}", message, request.getMethod(), request.getRequestURI());

        R<Void> result = R.error(400, "参数校验失败: " + message);
        result.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理 @Valid 对象参数绑定异常
     * 适用于 @RequestBody 对象校验
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<R<Void>> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定异常: {} - {} - {}", message, request.getMethod(), request.getRequestURI());

        R<Void> result = R.error(400, "参数绑定失败: " + message);
        result.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理请求体参数校验异常 (@RequestBody + @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<R<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("请求体参数校验异常: {} - {} - {}", message, request.getMethod(), request.getRequestURI());

        R<Void> result = R.error(400, "请求参数无效: " + message);
        result.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 处理JWT异常
     */
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleJwtException(JwtException e, HttpServletRequest request) {
        log.error("jwt: {} - {}", request.getRequestURI(), e.getMessage(), e);
        R<Void> result = R.error(500, e.getMessage());
        result.setPath(request.getRequestURI());
        return result;
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<R<Void>> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("接口不存在: {} {} - {}", e.getHttpMethod(), e.getRequestURL(), request.getRequestURI());

        R<Void> result = R.error(404, "接口不存在: " + e.getRequestURL());
        result.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常: {} - {}", request.getRequestURI(), e.getMessage(), e);
        R<Void> result = R.error(500, "系统内部错误");
        result.setPath(request.getRequestURI());
        return result;
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: {} - {}", request.getRequestURI(), e.getMessage(), e);

        // 生产环境可以返回更友好的提示
        String message = "系统繁忙，请稍后重试";
        // 开发环境可以返回详细错误
        // String message = e.getMessage();
        R<Void> result = R.error(500, message);
        result.setPath(request.getRequestURI());
        return result;
    }
}
