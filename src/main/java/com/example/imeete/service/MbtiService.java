package com.example.imeete.service;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface MbtiService {
  JSONObject getMbtiTest();

  JSONObject getMbtiIntro(String mbti);
}
