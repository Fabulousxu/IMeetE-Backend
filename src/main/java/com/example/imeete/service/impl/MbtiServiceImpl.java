package com.example.imeete.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.MbtiIntroRepository;
import com.example.imeete.dao.MbtiTestRepository;
import com.example.imeete.entity.MbtiIntro;
import com.example.imeete.service.MbtiService;
import com.example.imeete.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MbtiServiceImpl implements MbtiService {
  @Autowired private MbtiTestRepository mbtiTestRepository;
  @Autowired private MbtiIntroRepository mbtiIntroRepository;

  @Override
  public JSONObject getMbtiTest() {
    JSONObject res = Util.successResponse("获取MBTI测试题成功");
    res.put("data", mbtiTestRepository.findAll());
    return res;
  }

  @Override
  public JSONObject getMbtiIntro(String mbti) {
    MbtiIntro mbtiIntro = mbtiIntroRepository.findById(mbti).orElse(null);
    if (mbtiIntro == null) return Util.errorResponse("MBTI类型不存在");
    JSONObject res = Util.successResponse("获取MBTI介绍成功");
    res.put("data", mbtiIntroRepository.findById(mbti).orElse(null));
    return res;
  }
}
