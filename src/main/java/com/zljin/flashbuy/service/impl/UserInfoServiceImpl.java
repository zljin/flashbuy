package com.zljin.flashbuy.service.impl;

import com.zljin.flashbuy.domain.UserInfo;
import com.zljin.flashbuy.domain.UserPassword;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.repository.UserInfoRepository;
import com.zljin.flashbuy.repository.UserPasswordRepository;
import com.zljin.flashbuy.service.JwtService;
import com.zljin.flashbuy.service.RedisService;
import com.zljin.flashbuy.service.UserInfoService;
import com.zljin.flashbuy.util.AppConstants;
import com.zljin.flashbuy.util.CommonUtil;
import com.zljin.flashbuy.model.dto.RegisterDTO;
import com.zljin.flashbuy.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author zoulingjin
 * @description 针对表【user_info(用户信息表)】的数据库操作Service实现
 * @createDate 2025-10-06 11:19:14
 */
@Slf4j
@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final RedisService redisService;
    private final JwtService jwtService;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, UserPasswordRepository userPasswordRepository, RedisService redisService, JwtService jwtService) {
        this.userInfoRepository = userInfoRepository;
        this.userPasswordRepository = userPasswordRepository;
        this.redisService = redisService;
        this.jwtService = jwtService;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void register(RegisterDTO registerDTO) {

        Object o = redisService.get(AppConstants.OTP_KEY + registerDTO.getEmail());
        if (null == o || !registerDTO.getOtpCode().equals(o.toString())) {
            throw new BusinessException(BusinessExceptionEnum.USER_OTP_FAIL, BusinessExceptionEnum.USER_OTP_FAIL.getErrorMessage());
        }

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(registerDTO, userInfo);
        userInfo.setCreatedAt(LocalDateTime.now());
        userInfo.setUpdatedAt(LocalDateTime.now());
        userInfo.setIsDeleted(0);

        try {
            userInfoRepository.save(userInfo);
        } catch (Exception e) {
            log.error("register error: ", e);
            throw new BusinessException(BusinessExceptionEnum.REGISTER_FAIL, BusinessExceptionEnum.REGISTER_FAIL.getErrorMessage());
        }

        UserPassword userPasswordEntity = new UserPassword();
        userPasswordEntity.setUserId(userInfo.getId());
        userPasswordEntity.setEncrptPassword(CommonUtil.passwordEncrypt(registerDTO.getPassword()));

        try {
            userPasswordRepository.save(userPasswordEntity);
        } catch (Exception e) {
            log.error("register error: ", e);
            throw new BusinessException(BusinessExceptionEnum.REGISTER_FAIL, BusinessExceptionEnum.REGISTER_FAIL.getErrorMessage());
        }
    }

    @Override
    public UserVO login(String email, String password) {
        UserInfo userInfo = userInfoRepository.findByEmailAndIsDeleted(email);
        if (null == userInfo) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }
        UserPassword userPassword = userPasswordRepository.findByUserId(userInfo.getId());

        if (null == userPassword) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }

        if (!CommonUtil.passwordMatches(password, userPassword.getEncrptPassword())) {
            throw new BusinessException(BusinessExceptionEnum.USER_LOGIN_FAIL, BusinessExceptionEnum.USER_LOGIN_FAIL.getErrorMessage());
        }

        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userInfo, userVo);

        //登陆成功则，生成jwt token,30分钟过期
        String token = jwtService.generateToken(email, userInfo.getId(), null);
        userVo.setToken(token);

        //用户信息放入redis，30分钟过期
        redisService.set(AppConstants.USER_CACHE_KEY + userInfo.getId(), userVo, 1800);
        return userVo;
    }

    @Override
    public void getOtp(String email) {
        String otp = CommonUtil.generateValidateCode();
        //2分钟过期
        redisService.set(AppConstants.OTP_KEY + email, otp, 120);
        //模拟发送短信
        log.info("email: {}, otp: {}", email, otp);
    }

    @Override
    public UserVO getUserById(String userId) {
        UserInfo userInfo = userInfoRepository.findById(userId).orElse(null);
        if (null == userInfo) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_EXIST, BusinessExceptionEnum.USER_NOT_EXIST.getErrorMessage());
        }
        UserVO userVo = new UserVO();
        BeanUtils.copyProperties(userInfo, userVo);
        return userVo;
    }
}




