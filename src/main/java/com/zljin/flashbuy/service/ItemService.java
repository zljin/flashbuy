package com.zljin.flashbuy.service;

import com.zljin.flashbuy.model.dto.ItemDTO;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.PageResult;

/**
* @author zoulingjin
* @description 针对表【item(商品表)】的数据库操作Service
* @createDate 2025-10-06 11:16:56
*/
public interface ItemService {

    ItemVO createItem(ItemDTO itemDTO);

    ItemVO getItemById(String id);

    PageResult<ItemVO> listItem(String title, Integer pageCurrent, Integer pageSize);

    void decreaseStock(String itemId, Integer amount);

    void increaseSales(String itemId, Integer amount);
}
