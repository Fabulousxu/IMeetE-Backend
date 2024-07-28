package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class LoginController {
  @Autowired private UserService userService;

  @PostMapping("/login")
  public JSONObject login(@RequestBody JSONObject body) {
    return userService.login(body.getString("id"), body.getString("password"));
  }

  @PostMapping("/register")
  public JSONObject register(@RequestBody JSONObject body) {
    return userService.register(
        body.getString("id"), body.getString("nickname"), body.getString("password"));
  }
}
