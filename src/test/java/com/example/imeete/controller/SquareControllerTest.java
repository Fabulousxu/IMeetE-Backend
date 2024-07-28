package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.*;

@WebMvcTest(SquareController.class)
public class SquareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private JSONArray mockResponse;

    @BeforeEach
    public void setUp() {
        mockResponse = new JSONArray();
        JSONObject post = new JSONObject();
        post.put("id", 1);

        JSONObject user = new JSONObject();
        user.put("userId", "u1");

        post.put("user", user);

        mockResponse.add(post);
    }


    @Test
    public void testGetPosts() throws Exception {
        Mockito.when(postService.getPosts(anyString(), anyString(), anyInt(), anyString()))
                .thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/square")
                        .param("type", "news")
                        .param("category", "tech")
                        .param("lastPostId", "2")
                        .cookie(new MockCookie("userId", "u1"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.userId").value("u1"));
    }

    @Test
    public void testGetPostsCategoryNotFound() throws Exception {
        Mockito.when(postService.getPosts(eq("发现"), eq("不存在的分类"), anyInt(), anyString()))
                .thenReturn(new JSONArray());

        mockMvc.perform(MockMvcRequestBuilders.get("/square")
                        .param("type", "发现")
                        .param("category", "不存在的分类")
                        .param("lastPostId", "0")
                        .cookie(new MockCookie("userId", "u1"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void testSearchPosts() throws Exception {
        Mockito.when(postService.searchPosts(anyString(), anyInt(), anyString()))
                .thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("keyword", "帖子1")
                        .param("lastPostId", "10")
                        .cookie(new MockCookie("userId", "u1"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.userId").value("u1"));
    }
}
