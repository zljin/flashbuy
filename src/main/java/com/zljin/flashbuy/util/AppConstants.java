package com.zljin.flashbuy.util;

public class AppConstants {

    public static final Integer PROMOTE_ZERO = 0;
    public static final Integer PROMOTE_WAIT = 1;
    public static final Integer PROMOTE_PROCESS = 2;
    public static final Integer PROMOTE_END = 3;


    //redis key
    //key: OTP:email,value: otp code
    public static final String OTP_KEY = "OTP:";
    //key: USER_CACHE:userId, value: userVO(里面包含token信息)
    public static final String USER_CACHE_KEY = "USER_CACHE:";

}
