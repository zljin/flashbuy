package com.zljin.flashbuy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<E> {

    private int code;

    private String message;

    private E data;

    private String path;

    public R(int code, String message, E data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <E> R<E> success(E data) {
        return new R<>(200, "success", data);
    }

    public static <E> R<E> error(int code, String message) {
        return new R<>(code, message, null);
    }
}
