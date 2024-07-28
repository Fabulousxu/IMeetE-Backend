package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.entity.User;
import com.example.imeete.service.MatchService;
import com.example.imeete.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/match")
@CrossOrigin
public class MatchController {
  @Autowired private MatchService matchService;

  @PostMapping
  public JSONObject match(@CookieValue("userId") String userId, String mbti, int sex) {
    User matchedUser = matchService.matchUser(userId, mbti, sex).join();
    if (matchedUser == null) return Util.errorResponse("匹配失败");
    JSONObject res = Util.successResponse("匹配成功");
    res.put("matchedUser", matchedUser.toJson(userId));
    return res;
  }
}
