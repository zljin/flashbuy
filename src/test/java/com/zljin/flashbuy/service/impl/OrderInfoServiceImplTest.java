package com.zljin.flashbuy.service.impl;

import com.zljin.flashbuy.domain.OrderInfo;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.mapper.OrderInfoMapper;
import com.zljin.flashbuy.model.dto.ItemDTO;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.OrderVO;
import com.zljin.flashbuy.model.vo.UserVO;
import com.zljin.flashbuy.service.ItemService;
import com.zljin.flashbuy.util.AppConstants;
import com.zljin.flashbuy.util.UserInfoHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInfoServiceImplTest {

    @Mock
    private OrderInfoMapper orderInfoMapper;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderInfoServiceImpl orderInfoService;

    private static final String USER_ID = "user123";
    private static final String ITEM_ID = "item123";
    private static final String PROMO_ID = "promo123";
    private static final Integer AMOUNT = 2;

    @BeforeEach
    void setUp() {
        // 设置静态方法mock
        UserVO userVO = new UserVO();
        userVO.setId(USER_ID);
        UserInfoHolder.saveUser(userVO);
    }

    @Test
    void createOrder_Success_WithPromo() {
        String orderId = "order123";
        ItemVO itemVO = createMockItemVO(true, AppConstants.PROMOTE_PROCESS);
        when(itemService.getItemById(ITEM_ID)).thenReturn(itemVO);

        when(orderInfoMapper.insert(any(OrderInfo.class))).thenReturn(1);

        // When
        OrderVO result = orderInfoService.createOrder(ITEM_ID, PROMO_ID, AMOUNT);

        // Then
        assertNotNull(result);
        assertNotNull(orderId);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(ITEM_ID, result.getItemId());
        assertEquals(PROMO_ID, result.getPromoId());
        assertEquals(AMOUNT, result.getAmount());
        assertEquals(itemVO.getPromo().getPromoItemPrice(), result.getItemPrice());

        //验证方法是否被调用，默认check 1次以上
        verify(itemService).getItemById(ITEM_ID);
        verify(itemService).decreaseStock(ITEM_ID, AMOUNT);
        verify(itemService).increaseSales(ITEM_ID, AMOUNT);
        verify(orderInfoMapper).insert(any(OrderInfo.class));

    }

    @Test
    void createOrder_UnexpectedException() {

        when(itemService.getItemById(ITEM_ID)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderInfoService.createOrder(ITEM_ID, null, AMOUNT));

        assertEquals(BusinessExceptionEnum.ORDER_ERROR.getErrorCode(), exception.getCode());

        verify(itemService, never()).decreaseStock(anyString(), anyInt());
        verify(itemService, never()).increaseSales(anyString(), anyInt());
        verify(orderInfoMapper, never()).insert(any(OrderInfo.class));

    }

    private ItemVO createMockItemVO(boolean hasPromo, Integer promoStatus) {
        ItemVO itemVO = new ItemVO();
        itemVO.setId(ITEM_ID);
        itemVO.setPrice(new BigDecimal("100.00"));

        if (hasPromo) {
            ItemDTO.Promo promo = new ItemDTO.Promo();
            promo.setId(PROMO_ID);
            promo.setStatus(promoStatus);
            promo.setPromoItemPrice(new BigDecimal("80.00"));
            itemVO.setPromo(promo);
        }

        return itemVO;
    }
}