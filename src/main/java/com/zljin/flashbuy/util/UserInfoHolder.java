package com.zljin.flashbuy.util;

import com.zljin.flashbuy.model.vo.UserVO;

public class UserInfoHolder {

    private UserInfoHolder() {
    }

    private static final ThreadLocal<UserVO> userInfoThreadLocal = new ThreadLocal<>();

    public static void saveUser(UserVO user) {
        userInfoThreadLocal.set(user);
    }

    public static UserVO getUser() {
        return userInfoThreadLocal.get();
    }

    public static void removeUser() {
        userInfoThreadLocal.remove();
    }
}
