package com.zljin.flashbuy.controller;

import com.zljin.flashbuy.model.dto.LoginDTO;
import com.zljin.flashbuy.model.dto.RegisterDTO;
import com.zljin.flashbuy.model.vo.R;
import com.zljin.flashbuy.service.UserInfoService;
import com.zljin.flashbuy.model.vo.UserVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserInfoService userInfoService;

    public UserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping("/register")
    public ResponseEntity<R<String>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        String password = registerDTO.getPassword();
        registerDTO.setPassword(password);
        userInfoService.register(registerDTO);
        return ResponseEntity.ok(R.success("ok"));
    }

    @GetMapping("/get-otp")
    public ResponseEntity<R<String>> getOtp(@RequestParam(name = "email") String email) {
        userInfoService.getOtp(email);
        return ResponseEntity.ok(R.success("ok"));
    }

    @PostMapping("/login")
    public ResponseEntity<R<UserVO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return ResponseEntity.ok(R.success(userInfoService.login(loginDTO.getAccount(), loginDTO.getPassword())));
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<R<UserVO>> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(R.success(userInfoService.getUserById(userId)));
    }
}
