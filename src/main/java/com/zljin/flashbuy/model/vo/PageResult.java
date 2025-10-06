package com.zljin.flashbuy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<E> {

    private int code;

    private String message;

    /**
     * @param pageNo 当前页码
     * @param pages 总页数
     * @param total 总条数
     * @param list 当前页的集合数量
     */
    private long pageCurrent;
    private long pages;
    private long total;
    private List<E> data;

    public static <E> PageResult<E> success(long pageCurrent, long pages, long total, List<E> data) {
        return new PageResult<>(200, "success", pageCurrent, pages, total, data);
    }

    public static <E> PageResult<E> error(int code, String message) {
        return new PageResult<>(code, message, 0, 0, 0, null);
    }
}
