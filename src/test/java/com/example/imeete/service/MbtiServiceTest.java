package com.example.imeete.service;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.MbtiIntroRepository;
import com.example.imeete.dao.MbtiTestRepository;
import com.example.imeete.entity.MbtiIntro;
import com.example.imeete.entity.MbtiTest;
import com.example.imeete.service.impl.MbtiServiceImpl;
import com.example.imeete.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MbtiServiceTest {

    @Mock private MbtiTestRepository mbtiTestRepository;
    @Mock private MbtiIntroRepository mbtiIntroRepository;

    @InjectMocks private MbtiServiceImpl mbtiService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetMbtiTest() {
        // Mocking the data returned by the mbtiTestRepository
        List<MbtiTest> mockTests = new ArrayList<>();
        MbtiTest mockTest = new MbtiTest();
        mockTest.setTestId(0); // Initialize with appropriate test data
        mockTests.add(mockTest);
        when(mbtiTestRepository.findAll()).thenReturn(mockTests);

        JSONObject response = mbtiService.getMbtiTest();
        JSONObject expectedResponse = Util.successResponse("获取MBTI测试题成功");
        expectedResponse.put("data", mockTests);
        assertEquals(expectedResponse.toString(), response.toString());
    }

    @Test
    public void testGetMbtiIntro_MbtiExists() {
        MbtiIntro mockIntro = new MbtiIntro(/* initialize with appropriate data */);
        when(mbtiIntroRepository.findById(anyString())).thenReturn(Optional.of(mockIntro));

        JSONObject response = mbtiService.getMbtiIntro("INTJ");
        JSONObject expectedResponse = Util.successResponse("获取MBTI介绍成功");
        expectedResponse.put("data", mockIntro);
        assertEquals(expectedResponse.toString(), response.toString());
    }

    @Test
    public void testGetMbtiIntro_MbtiNotExists() {
        when(mbtiIntroRepository.findById(anyString())).thenReturn(Optional.empty());

        JSONObject response = mbtiService.getMbtiIntro("INTJ");
        assertEquals(Util.errorResponse("MBTI类型不存在").toString(), response.toString());
    }
}
