package com.zljin.flashbuy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.OrderVO;
import com.zljin.flashbuy.domain.OrderInfo;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.mapper.OrderInfoMapper;
import com.zljin.flashbuy.service.ItemService;
import com.zljin.flashbuy.service.OrderInfoService;
import com.zljin.flashbuy.util.AppConstants;
import com.zljin.flashbuy.util.CommonUtil;
import com.zljin.flashbuy.util.UserInfoHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author zoulingjin
 * @description 针对表【order_info(订单表)】的数据库操作Service实现
 * @createDate 2025-10-06 11:19:04
 */
@Slf4j
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
        implements OrderInfoService {

    private final OrderInfoMapper orderInfoMapper;
    private final ItemService itemService;

    public OrderInfoServiceImpl(OrderInfoMapper orderInfoMapper, ItemService itemService) {
        this.orderInfoMapper = orderInfoMapper;
        this.itemService = itemService;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public OrderVO createOrder(String itemId, String promoId, Integer amount) {
        String userId = UserInfoHolder.getUser().getId();
        try {
            ItemVO itemVO = itemService.getItemById(itemId);
            if (itemVO == null) {
                throw new BusinessException(BusinessExceptionEnum.PARAMETER_VALIDATION_ERROR, "商品信息不存在");
            }

            if (promoId != null) {
                if (!promoId.equals(itemVO.getPromo().getId())) {
                    throw new BusinessException(BusinessExceptionEnum.PARAMETER_VALIDATION_ERROR, "活动信息不正确");
                } else if (itemVO.getPromo().getStatus() != AppConstants.PROMOTE_PROCESS) {
                    throw new BusinessException(BusinessExceptionEnum.PARAMETER_VALIDATION_ERROR, "活动信息未开始");
                }
            }

            //扣件库存
            itemService.decreaseStock(itemId, amount);

            OrderInfo orderInfoEntity = new OrderInfo();
            orderInfoEntity.setUserId(userId);
            orderInfoEntity.setItemId(itemId);
            orderInfoEntity.setAmount(amount);
            orderInfoEntity.setPromoId(promoId);
            orderInfoEntity.setItemPrice((promoId != null ? itemVO.getPromo().getPromoItemPrice() : itemVO.getPrice()));
            orderInfoEntity.setOrderPrice(orderInfoEntity.getItemPrice().multiply(new BigDecimal(amount)));
            orderInfoEntity.setId(CommonUtil.generateOrderId());
            orderInfoMapper.insert(orderInfoEntity);

            //记录商品销量
            itemService.increaseSales(itemId, amount);

            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orderInfoEntity, orderVO);
            return orderVO;
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw new BusinessException(BusinessExceptionEnum.ORDER_ERROR);
        }
    }
}




