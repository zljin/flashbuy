package com.zljin.flashbuy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zljin.flashbuy.domain.UserPassword;
import com.zljin.flashbuy.service.UserPasswordService;
import com.zljin.flashbuy.mapper.UserPasswordMapper;
import org.springframework.stereotype.Service;

/**
* @author zoulingjin
* @description 针对表【user_password(用户密码表)】的数据库操作Service实现
* @createDate 2025-10-06 11:19:21
*/
@Service
public class UserPasswordServiceImpl extends ServiceImpl<UserPasswordMapper, UserPassword>
    implements UserPasswordService{

}




