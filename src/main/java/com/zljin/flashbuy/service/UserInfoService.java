package com.zljin.flashbuy.service;

import com.zljin.flashbuy.model.dto.RegisterDTO;
import com.zljin.flashbuy.model.vo.UserVO;

/**
* @author zoulingjin
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2025-10-06 11:19:14
*/
public interface UserInfoService {

    void register(RegisterDTO registerDTO);

    UserVO login(String email, String password);

    void getOtp(String email);

    UserVO getUserById(String userId);
}
