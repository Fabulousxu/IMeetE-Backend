package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.entity.User;
import com.example.imeete.service.MatchService;
import com.example.imeete.util.Util;
import org.hibernate.mapping.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;

public class MatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MatchService matchService;

    @InjectMocks
    private MatchController matchController;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(matchController).build();

        mockUser = new User();
        mockUser.setUserId("u2");
        mockUser.setFollowings(new HashSet<>());
        mockUser.setCollects(new HashSet<>());
        mockUser.setFollowers(new HashSet<>());
        mockUser.setLikeComments(new HashSet<>());
        mockUser.setLikes(new HashSet<>());
        mockUser.setPosts(new HashSet<>());
    }

    @Test
    public void testMatchSuccess() throws Exception {
        when(matchService.matchUser(anyString(), anyString(), anyInt())).thenReturn(CompletableFuture.completedFuture(mockUser));

        mockMvc.perform(MockMvcRequestBuilders.post("/match")
                        .cookie(new MockCookie("userId", "u1"))
                        .param("mbti", "INTP")
                        .param("sex", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("匹配成功"))
                .andExpect(jsonPath("$.matchedUser.userId").value("u2"));
    }

    @Test
    public void testMatchFailure() throws Exception {
        when(matchService.matchUser(anyString(), anyString(), anyInt())).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(MockMvcRequestBuilders.post("/match")
                        .cookie(new MockCookie("userId", "u1"))
                        .param("mbti", "INTJ")
                        .param("sex", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("匹配失败"));
    }
}
