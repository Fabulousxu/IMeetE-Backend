package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.UserService;
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

import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;

public class UserControllerTest {

  private MockMvc mockMvc;

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
  }

  @Test
  public void testGetUserInfo() throws Exception {
    JSONObject userInfo = new JSONObject();
    userInfo.put("id", "u1");
    userInfo.put("nickname", "user1");

    when(userService.getUserInfo(anyString())).thenReturn(userInfo);

    mockMvc.perform(MockMvcRequestBuilders.get("/user")
                    .param("id", "u1")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.nickname").value("user1"));
  }

  @Test
  public void testGetSelfInfo() throws Exception {
    JSONObject selfInfo = new JSONObject();
    selfInfo.put("id", "u1");
    selfInfo.put("nickname", "selfUser");

    when(userService.getSelfInfo(anyString())).thenReturn(selfInfo);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/self")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.nickname").value("selfUser"));
  }

  @Test
  public void testGetUserPosts() throws Exception {
    JSONObject userPosts = new JSONObject();
    userPosts.put("id", "u1");
    userPosts.put("posts", new JSONArray());

    when(userService.getUserPosts(anyString(), anyString())).thenReturn(userPosts);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/post")
                    .param("id", "u1")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.posts").isArray());
  }

  @Test
  public void testGetSelfPosts() throws Exception {
    JSONArray selfPosts = new JSONArray();

    when(userService.getSelfPosts(anyString())).thenReturn(selfPosts);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/self/post")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testGetUserCollect() throws Exception {
    JSONObject userCollect = new JSONObject();
    userCollect.put("id", "u1");
    userCollect.put("collects", new JSONArray());

    when(userService.getUserCollects(anyString(), anyString())).thenReturn(userCollect);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/collect")
                    .param("id", "u1")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("u1"))
            .andExpect(jsonPath("$.collects").isArray());
  }

  @Test
  public void testGetSelfCollect() throws Exception {
    JSONArray selfCollects = new JSONArray();

    when(userService.getSelfCollects(anyString())).thenReturn(selfCollects);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/self/collect")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testGetFriends() throws Exception {
    JSONArray friends = new JSONArray();

    when(userService.getFriends(anyString())).thenReturn(friends);

    mockMvc.perform(MockMvcRequestBuilders.get("/user/self/friends")
                    .cookie(new MockCookie("userId", "u1"))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testFollow() throws Exception {
    JSONObject response = new JSONObject();
    response.put("code", 200);
    response.put("message", "关注成功");

    when(userService.follow(anyString(), anyString())).thenReturn(response);

    JSONObject requestBody = new JSONObject();
    requestBody.put("id", "u2");

    mockMvc.perform(MockMvcRequestBuilders.post("/user/follow")
                    .cookie(new MockCookie("userId", "u1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody.toJSONString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("关注成功"));
  }

  @Test
  public void testUnfollow() throws Exception {
    JSONObject response = new JSONObject();
    response.put("code", 200);
    response.put("message", "取消关注成功");

    when(userService.unfollow(anyString(), anyString())).thenReturn(response);

    JSONObject requestBody = new JSONObject();
    requestBody.put("id", "u2");

    mockMvc.perform(MockMvcRequestBuilders.post("/user/unfollow")
                    .cookie(new MockCookie("userId", "u1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody.toJSONString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("取消关注成功"));
  }

  @Test
  public void testUpdateUserInfo() throws Exception {
    JSONObject response = new JSONObject();
    response.put("code", 200);
    response.put("message", "更新成功");

    when(userService.updateUserInfo(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(response);

    JSONObject requestBody = new JSONObject();
    requestBody.put("nickname", "newUser");
    requestBody.put("avatar", "newAvatar");
    requestBody.put("mbti", "INTP");
    requestBody.put("sex", 1);
    requestBody.put("age", 25);
    requestBody.put("area", "北京");
    requestBody.put("intro", "新的介绍");

    mockMvc.perform(MockMvcRequestBuilders.post("/user/setting")
                    .cookie(new MockCookie("userId", "u1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody.toJSONString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("更新成功"));
  }
}
