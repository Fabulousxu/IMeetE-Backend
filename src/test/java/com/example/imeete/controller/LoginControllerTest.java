package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.UserService;
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

public class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        JSONObject loginResponse = new JSONObject();
        loginResponse.put("code", 200);
        loginResponse.put("message", "登录成功");

        when(userService.login(anyString(), anyString())).thenReturn(loginResponse);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "u1");
        requestBody.put("password", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"));
    }

    @Test
    public void testLoginFailure() throws Exception {
        JSONObject loginResponse = new JSONObject();
        loginResponse.put("code", 400);
        loginResponse.put("message", "登录失败");

        when(userService.login(anyString(), anyString())).thenReturn(loginResponse);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "u1");
        requestBody.put("password", "wrongPassword");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("登录失败"));
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        JSONObject registerResponse = new JSONObject();
        registerResponse.put("code", 200);
        registerResponse.put("message", "注册成功");

        when(userService.register(anyString(), anyString(), anyString())).thenReturn(registerResponse);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "u1");
        requestBody.put("nickname", "user1");
        requestBody.put("password", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    public void testRegisterFailure() throws Exception {
        JSONObject registerResponse = new JSONObject();
        registerResponse.put("code", 400);
        registerResponse.put("message", "注册失败");

        when(userService.register(anyString(), anyString(), anyString())).thenReturn(registerResponse);

        JSONObject requestBody = new JSONObject();
        requestBody.put("id", "u1");
        requestBody.put("nickname", "user1");
        requestBody.put("password", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("注册失败"));
    }
}
