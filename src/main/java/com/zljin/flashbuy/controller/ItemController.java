package com.zljin.flashbuy.controller;

import com.zljin.flashbuy.model.PermitAdmit;
import com.zljin.flashbuy.model.vo.R;
import com.zljin.flashbuy.service.ItemService;
import com.zljin.flashbuy.model.dto.ItemDTO;
import com.zljin.flashbuy.model.vo.ItemVO;
import com.zljin.flashbuy.model.vo.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 只有管理员才能创建商品
     * @PermitAdmit
     * @param itemDTO
     * @return
     */
    @PermitAdmit
    @PostMapping("/create")
    public ResponseEntity<R<ItemVO>> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        return ResponseEntity.ok(R.success(itemService.createItem(itemDTO)));
    }

    @GetMapping("/get/{itemId}")
    public ResponseEntity<R<ItemVO>> getItem(@PathVariable(value = "itemId") String itemId) {
        return ResponseEntity.ok(R.success(itemService.getItemById(Long.valueOf(itemId))));
    }

    @GetMapping("/getPage")
    public ResponseEntity<PageResult<ItemVO>> listItem(@RequestParam(value = "title", required = false) String title,
                                                       @RequestParam(value = "pageCurrent", required = false, defaultValue = "1") Integer pageCurrent,
                                                       @RequestParam(value = "pageSize", required = false, defaultValue = "100") Integer pageSize) {
        return ResponseEntity.ok(itemService.listItem(title, pageCurrent, pageSize));
    }
}
