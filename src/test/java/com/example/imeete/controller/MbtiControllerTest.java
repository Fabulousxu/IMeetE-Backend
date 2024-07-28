package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.MbtiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

public class MbtiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MbtiService mbtiService;

    @InjectMocks
    private MbtiController mbtiController;

    private JSONObject mockTestResponse;
    private JSONObject mockIntroResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mbtiController).build();

        mockTestResponse = new JSONObject();
        mockTestResponse.put("question", "你觉得向其他人介绍自己很困难");

        mockIntroResponse = new JSONObject();
        mockIntroResponse.put("intro", "ENFJ是理想主义的组织者，他们驱使着将自己对人类最好的愿景付诸实施。由于他们能够看到他人潜力并且具有说服他人接受自己想法的魅力，他们经常充当人类成长的催化剂。");
    }

    @Test
    public void testGetMbtiTest() throws Exception {
        when(mbtiService.getMbtiTest()).thenReturn(mockTestResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/mbti/test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("你觉得向其他人介绍自己很困难"));
    }

    @Test
    public void testGetMbtiIntro() throws Exception {
        when(mbtiService.getMbtiIntro(anyString())).thenReturn(mockIntroResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/mbti/intro")
                        .param("mbtiType", "ENTJ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intro").value("ENFJ是理想主义的组织者，他们驱使着将自己对人类最好的愿景付诸实施。由于他们能够看到他人潜力并且具有说服他人接受自己想法的魅力，他们经常充当人类成长的催化剂。"));
    }
}
