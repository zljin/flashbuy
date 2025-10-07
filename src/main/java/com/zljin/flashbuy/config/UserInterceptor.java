package com.zljin.flashbuy.config;

import com.zljin.flashbuy.domain.UserInfo;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.model.vo.UserVO;
import com.zljin.flashbuy.service.JwtService;
import com.zljin.flashbuy.service.RedisService;
import com.zljin.flashbuy.util.AppConstants;
import com.zljin.flashbuy.util.UserInfoHolder;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@Component
public class UserInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;
    private final RedisService redisService;

    public UserInterceptor(JwtService jwtService, RedisService redisService) {
        this.jwtService = jwtService;
        this.redisService = redisService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String token = request.getHeader("Authorization");

        if (!jwtService.validateToken(token)) {
            throw new JwtException("JWT Token验证失败");
        }

        String userId = jwtService.getUserIdFromToken(token);

        Object o = redisService.get(AppConstants.USER_CACHE_KEY + userId);
        if (o == null) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_LOGIN.getErrorCode(), BusinessExceptionEnum.USER_NOT_LOGIN.getErrorMessage());
        }
        UserVO userVO = (UserVO) o;
        //token不一致，说明已经重新登录，之前的token失效
        if (!token.equals(userVO.getToken())) {
            throw new BusinessException(BusinessExceptionEnum.USER_NOT_LOGIN.getErrorCode(), BusinessExceptionEnum.USER_NOT_LOGIN.getErrorMessage());
        }
        UserInfoHolder.saveUser(userVO);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserInfoHolder.removeUser();
    }
}
