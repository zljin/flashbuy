package com.zljin.flashbuy.model.vo;

import lombok.Data;

@Data
public class UserVO {
    private String id;
    private String name;

    private String gender;

    private int age;

    private String telephone;
    private String registerMode;
    private String thirdPartyId;

    private String email;

    private String token;

}
