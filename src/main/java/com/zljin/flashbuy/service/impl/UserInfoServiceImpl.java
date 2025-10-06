package com.zljin.flashbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zljin.flashbuy.domain.UserInfo;
import com.zljin.flashbuy.domain.UserPassword;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.mapper.UserInfoMapper;
import com.zljin.flashbuy.mapper.UserPasswordMapper;
import com.zljin.flashbuy.service.UserInfoService;
import com.zljin.flashbuy.util.CommonUtil;
import com.zljin.flashbuy.model.dto.RegisterDTO;
import com.zljin.flashbuy.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zoulingjin
 * @description 针对表【user_info(用户信息表)】的数据库操作Service实现
 * @createDate 2025-10-06 11:19:14
 */
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
        implements UserInfoService {

    private final UserInfoMapper userInfoMapper;
    private final UserPasswordMapper userPasswordMapper;

    public UserInfoServiceImpl(UserInfoMapper userInfoMapper, UserPasswordMapper userPasswordMapper) {
        this.userInfoMapper = userInfoMapper;
        this.userPasswordMapper = userPasswordMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void register(RegisterDTO registerDTO) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(registerDTO, userInfo);

        //todo 从redis中获取otp进行验证

        try {
            userInfoMapper.insert(userInfo);
        } catch (Exception e) {
            log.error("register error: ", e);
            throw new BusinessException(BusinessExceptionEnum.REGISTER_FAIL, BusinessExceptionEnum.REGISTER_FAIL.getErrorMessage());
        }

        UserPassword userPasswordEntity = new UserPassword();
        userPasswordEntity.setUserId(userInfo.getId());
        userPasswordEntity.setEncrptPassword(CommonUtil.passwordEncrypt(registerDTO.getPassword()));

        try {
            userPasswordMapper.insert(userPasswordEntity);
        } catch (Exception e) {
            log.error("register error: ", e);
            throw new BusinessException(BusinessExceptionEnum.REGISTER_FAIL, BusinessExceptionEnum.REGISTER_FAIL.getErrorMessage());
        }
    }

    @Override
    public UserVO login(String email, String password) {
        UserInfo userInfo = userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("email", email));
        if (null == userInfo) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }
        UserPassword userPassword = userPasswordMapper.selectOne(new QueryWrapper<UserPassword>().eq("user_id", userInfo.getId()));

        if (null == userPassword) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }

        if (!CommonUtil.passwordMatches(password, userPassword.getEncrptPassword())) {
            throw new BusinessException(BusinessExceptionEnum.USER_LOGIN_FAIL, BusinessExceptionEnum.USER_LOGIN_FAIL.getErrorMessage());
        }
        return getUserById(userInfo.getId());
    }

    @Override
    public void getOtp(String email) {
        String otp = CommonUtil.generateValidateCode();
        log.debug("email: {}, otp: {}", email, otp);
        //todo 将otp验证码存入redis
    }

    @Override
    public UserVO getUserById(String userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        if (null == userInfo) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userInfo, userVo);
        return userVo;
    }
}




