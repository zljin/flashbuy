package com.zljin.flashbuy.service.impl;

import com.zljin.flashbuy.model.dto.ItemDTO;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.PageResult;
import com.zljin.flashbuy.domain.Item;
import com.zljin.flashbuy.domain.ItemStock;
import com.zljin.flashbuy.domain.Promo;
import com.zljin.flashbuy.exception.BusinessException;
import com.zljin.flashbuy.exception.BusinessExceptionEnum;
import com.zljin.flashbuy.repository.ItemRepository;
import com.zljin.flashbuy.repository.ItemStockRepository;
import com.zljin.flashbuy.repository.PromoRepository;
import com.zljin.flashbuy.service.ItemService;
import com.zljin.flashbuy.util.AppConstants;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zoulingjin
 * @description 针对表【item(商品表)】的数据库操作Service实现
 * @createDate 2025-10-06 11:16:56
 */
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final ItemStockRepository itemStockRepository;

    private final PromoRepository promoRepository;

    public ItemServiceImpl(ItemRepository itemRepository, ItemStockRepository itemStockRepository, PromoRepository promoRepository) {
        this.itemRepository = itemRepository;
        this.itemStockRepository = itemStockRepository;
        this.promoRepository = promoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public ItemVO createItem(ItemDTO itemDTO) {
        try {
            Item item = new Item();
            BeanUtils.copyProperties(itemDTO, item);
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            item.setIsDeleted(0);
            itemRepository.save(item);

            ItemStock itemStock = new ItemStock();
            itemStock.setItemId(item.getId());
            itemStock.setStock(itemDTO.getStock());
            itemStock.setCreatedAt(LocalDateTime.now());
            itemStock.setUpdatedAt(LocalDateTime.now());
            itemStock.setIsDeleted(0);
            itemStockRepository.save(itemStock);

            ItemDTO.Promo promo = itemDTO.getPromo();
            if (promo.getStatus() != 0
                    && !StringUtils.isBlank(promo.getPromoName())
                    && promo.getPromoItemPrice() != null
                    && promo.getStartDate() != null
                    && promo.getEndDate() != null) {
                Promo promoDb = new Promo();
                promoDb.setPromoName(promo.getPromoName());
                promoDb.setPromoItemPrice(promo.getPromoItemPrice());
                // 转换Date为LocalDateTime
                promoDb.setStartDate(promo.getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                promoDb.setEndDate(promo.getEndDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
                promoDb.setItemId(item.getId());
                promoDb.setCreatedAt(LocalDateTime.now());
                promoDb.setUpdatedAt(LocalDateTime.now());
                promoDb.setIsDeleted(0);
                promoRepository.save(promoDb);
            }
            return getItemById(item.getId());
        } catch (Exception e) {
            throw new BusinessException(BusinessExceptionEnum.ADD_ITEM_ERROR, BusinessExceptionEnum.ADD_ITEM_ERROR.getErrorMessage());
        }
    }

    @Override
    public ItemVO getItemById(String id) {
        Item itemEntity = itemRepository.findByIdAndIsDeleted(id);
        ItemStock itemStockEntity = itemStockRepository.findByItemIdAndIsDeleted(id);
        Promo promoEntity = promoRepository.findByItemIdAndIsDeleted(id);
        return convert2ItemVO(itemEntity, itemStockEntity, promoEntity);
    }

    @Override
    public PageResult<ItemVO> listItem(String title, Integer pageCurrent, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageCurrent - 1, pageSize);
        Page<Item> itemPage;
        
        if (!StringUtils.isBlank(title)) {
            itemPage = itemRepository.findByTitleContainingAndIsDeletedOrderByCreatedAtDesc(title, pageable);
        } else {
            itemPage = itemRepository.findByIsDeletedOrderByCreatedAtDesc(pageable);
        }
        
        if (itemPage == null || itemPage.getContent().isEmpty()) {
            return PageResult.error(404, "no data");
        }
        
        List<ItemVO> list = itemPage.getContent().stream()
                .map(item -> getItemById(item.getId()))
                .toList();
        return PageResult.success(itemPage.getNumber() + 1, itemPage.getTotalPages(), itemPage.getTotalElements(), list);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void decreaseStock(String itemId, Integer amount) {
        //todo 后面用分布式锁
        int affectedRow = itemStockRepository.decreaseStock(itemId, amount);
        if (affectedRow <= 0) {
            throw new BusinessException(BusinessExceptionEnum.STOCK_NOT_ENOUGH, BusinessExceptionEnum.STOCK_NOT_ENOUGH.getErrorMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void increaseSales(String itemId, Integer amount) {
        //todo 后面用分布式锁
        itemRepository.increaseSales(itemId, amount);
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
            // 转换LocalDateTime为Date
            promoVO.setStartDate(java.sql.Timestamp.valueOf(promoEntity.getStartDate()));
            promoVO.setEndDate(java.sql.Timestamp.valueOf(promoEntity.getEndDate()));
            promoVO.setItemId(itemVO.getId());
            itemVO.setPromo(promoVO);
        }
        return itemVO;
    }

    private int getPromoStatus(Promo promoEntity) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = promoEntity.getStartDate();
        LocalDateTime end = promoEntity.getEndDate();
        if (now.isBefore(start)) {
            return AppConstants.PROMOTE_WAIT;
        } else if (now.isAfter(end)) {
            return AppConstants.PROMOTE_END;
        }
        return AppConstants.PROMOTE_PROCESS;
    }
}




