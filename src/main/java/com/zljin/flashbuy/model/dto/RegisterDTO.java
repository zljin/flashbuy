package com.zljin.flashbuy.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "验证码不能为空")
    private String otpCode;
    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    @Pattern(regexp = "(^Man$|^Woman$|^UGM$)", message = "sex 值不在可选范围")
    private String gender;

    private Integer age;

    @NotBlank(message = "手机号不能为空")
    private String telephone;

    private String registerMode;
    private String thirdPartyId;

    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮件不能为空")
    @Email(message = "email 格式不正确")
    private String email;
}
