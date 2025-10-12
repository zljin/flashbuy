package com.zljin.flashbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zljin.flashbuy.model.dto.ItemDTO;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.PageResult;
import com.zljin.flashbuy.domain.Item;
import com.zljin.flashbuy.domain.ItemStock;
import com.zljin.flashbuy.domain.Promo;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.mapper.ItemMapper;
import com.zljin.flashbuy.mapper.ItemStockMapper;
import com.zljin.flashbuy.mapper.PromoMapper;
import com.zljin.flashbuy.service.ItemService;
import com.zljin.flashbuy.util.AppConstants;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author zoulingjin
 * @description 针对表【item(商品表)】的数据库操作Service实现
 * @createDate 2025-10-06 11:16:56
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item>
        implements ItemService {

    private final ItemMapper itemMapper;

    private final ItemStockMapper itemStockMapper;

    private final PromoMapper promoMapper;

    public ItemServiceImpl(ItemMapper itemMapper, ItemStockMapper itemStockMapper, PromoMapper promoMapper) {
        this.itemMapper = itemMapper;
        this.itemStockMapper = itemStockMapper;
        this.promoMapper = promoMapper;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public ItemVO createItem(ItemDTO itemDTO) {
        try {
            Item item = new Item();
            BeanUtils.copyProperties(itemDTO, item);
            itemMapper.insert(item);

            ItemStock itemStock = new ItemStock();
            itemStock.setItemId(item.getId());
            itemStock.setStock(itemDTO.getStock());
            itemStockMapper.insert(itemStock);

            ItemDTO.Promo promo = itemDTO.getPromo();
            if (promo.getStatus() != 0
                    && !StringUtils.isBlank(promo.getPromoName())
                    && promo.getPromoItemPrice() != null
                    && promo.getStartDate() != null
                    && promo.getEndDate() != null) {
                Promo promoDb = new Promo();
                BeanUtils.copyProperties(promo, promoDb);
                promoDb.setItemId(item.getId());
                promoMapper.insert(promoDb);
            }
            return getItemById(item.getId());
        } catch (Exception e) {
            log.error("createItem error: ", e);
            throw new BusinessException(BusinessExceptionEnum.ADD_ITEM_ERROR, BusinessExceptionEnum.ADD_ITEM_ERROR.getErrorMessage());
        }
    }

    @Override
    public ItemVO getItemById(String id) {
        Item itemEntity = itemMapper.selectById(id);
        QueryWrapper<ItemStock> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", id);
        ItemStock itemStockEntity = itemStockMapper.selectOne(queryWrapper);
        QueryWrapper<Promo> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("item_id", id);
        Promo promoEntity = promoMapper.selectOne(queryWrapper2);
        return convert2ItemVO(itemEntity, itemStockEntity, promoEntity);
    }

    @Override
    public PageResult<ItemVO> listItem(String title, Integer pageCurrent, Integer pageSize) {
        Page<Item> page = new Page<>(pageCurrent, pageSize);
        QueryWrapper<Item> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isBlank(title)) {
            queryWrapper.like("title", title);
        }
        queryWrapper.orderByDesc("created_at");
        Page<Item> itemPage = itemMapper.selectPage(page, queryWrapper);
        if (itemPage == null || itemPage.getRecords() == null || itemPage.getRecords().isEmpty()) {
            return PageResult.error(404, "no data");
        }
        List<ItemVO> list = itemPage.getRecords().stream()
                .map(item -> getItemById(item.getId()))
                .toList();
        return PageResult.success(itemPage.getCurrent(), itemPage.getPages(), itemPage.getTotal(), list);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void decreaseStock(String itemId, Integer amount) {
        //todo 后面用分布式锁
        int affectedRow = itemStockMapper.decreaseStock(itemId, amount);
        if (affectedRow <= 0) {
            throw new BusinessException(BusinessExceptionEnum.STOCK_NOT_ENOUGH, BusinessExceptionEnum.STOCK_NOT_ENOUGH.getErrorMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void increaseSales(String itemId, Integer amount) {
        //todo 后面用分布式锁
        itemStockMapper.increaseSales(itemId, amount);
    }

    private ItemVO convert2ItemVO(Item itemEntity, ItemStock itemStockEntity, Promo promoEntity) {
        ItemVO itemVO = new ItemVO();
        if (itemEntity != null) {
            BeanUtils.copyProperties(itemEntity, itemVO);
        }
        if (itemStockEntity != null) {
            itemVO.setStock(itemStockEntity.getStock());
        }
        if (promoEntity != null) {
            ItemDTO.Promo promoVO = new ItemDTO.Promo();
            promoVO.setId(promoEntity.getId());
            promoVO.setStatus(getPromoStatus(promoEntity));
            promoVO.setPromoItemPrice(promoEntity.getPromoItemPrice());
            promoVO.setPromoName(promoEntity.getPromoName());
            promoVO.setStartDate(promoEntity.getStartDate());
            promoVO.setEndDate(promoEntity.getEndDate());
            promoVO.setItemId(itemVO.getId());
            itemVO.setPromo(promoVO);
        }
        return itemVO;
    }

    private int getPromoStatus(Promo promoEntity) {
        long now = new Date().getTime() / 1000;
        long start = promoEntity.getStartDate().getTime() / 1000;
        long end = promoEntity.getEndDate().getTime() / 1000;
        if (now < start) {
            return AppConstants.PROMOTE_WAIT;
        } else if (now > end) {
            return AppConstants.PROMOTE_END;
        }
        return AppConstants.PROMOTE_PROCESS;
    }
}




