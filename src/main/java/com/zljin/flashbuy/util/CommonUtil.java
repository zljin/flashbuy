package com.zljin.flashbuy.util;

import com.google.gson.Gson;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通用工具类
 */
public class CommonUtil {

    private static final ThreadLocal<BCryptPasswordEncoder> encoderThreadLocal
            = ThreadLocal.withInitial(() -> new BCryptPasswordEncoder());

    private static final ThreadLocal<SimpleDateFormat> dateFormatThreadLocal
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));

    private static final ThreadLocal<SimpleDateFormat> dateFormatOrderIdThreadLocal
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmssSSS"));

    private static final ThreadLocal<Gson> gsonThreadLocal = ThreadLocal.withInitial(() -> new Gson());

    private CommonUtil() {

    }

    /**
     * 随机生成验证码
     */
    public static String generateValidateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    /**
     * 加密密码
     */
    public static String passwordEncrypt(String password) {
        return encoderThreadLocal.get().encode(password);
    }

    /**
     * 验证密码
     */
    public static boolean passwordMatches(String rawPassword, String encodedPassword) {
        return encoderThreadLocal.get().matches(rawPassword, encodedPassword);
    }

    /**
     * 格式化日期
     */
    public static String dateFormat(Date date) {
        return dateFormatThreadLocal.get().format(date);
    }

    /**
     * 生成订单号
     */
    public static String generateOrderId() {
        return dateFormatOrderIdThreadLocal.get().format(new Date());
    }

    /**
     * 获取Gson实例
     */
    public static Gson getGsonInstance() {
        return gsonThreadLocal.get();
    }

}
