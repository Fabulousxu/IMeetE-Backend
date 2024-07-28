package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.MbtiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mbti")
@CrossOrigin
public class MbtiController {
  @Autowired private MbtiService mbtiService;

  @GetMapping("/test")
  public JSONObject getMbtiTest() {
    return mbtiService.getMbtiTest();
  }

  @GetMapping("/intro")
  public JSONObject getMbtiIntro(String mbtiType) {
    return mbtiService.getMbtiIntro(mbtiType);
  }
}
