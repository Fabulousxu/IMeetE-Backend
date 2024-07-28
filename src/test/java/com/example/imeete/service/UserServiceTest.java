package com.example.imeete.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.UserAuthRepository;
import com.example.imeete.dao.UserRepository;
import com.example.imeete.entity.User;
import com.example.imeete.entity.UserAuth;
import com.example.imeete.service.impl.UserServiceImpl;
import com.example.imeete.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserAuthRepository userAuthRepository;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    private User user2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserId("u1");
        user.setPosts(new HashSet<>());
        user.setCollects(new HashSet<>());
        user.setLikes(new HashSet<>());
        user.setFollowers(new HashSet<>());
        user.setFollowings(new HashSet<>());
        user.setLikeComments(new HashSet<>());

        user2 = new User();
        user2.setUserId("u2");
        user2.setPosts(new HashSet<>());
        user2.setCollects(new HashSet<>());
        user2.setLikes(new HashSet<>());
        user2.setFollowers(new HashSet<>());
        user2.setFollowings(new HashSet<>());
        user2.setLikeComments(new HashSet<>());

        Set<User> u1F = new HashSet<>();

        u1F.add(user2);

        user.setFollowers(u1F);

        user.setFollowings(u1F);
    }

    @Test
    public void testLogin_Success() {
        String userId = "u1";
        String password = "password";

        when(userAuthRepository.existsById(userId)).thenReturn(true);
        when(userAuthRepository.existsByUserIdAndPassword(userId, password)).thenReturn(true);

        JSONObject result = userService.login(userId, password);

        assertEquals("登录成功", result.getString("message"));
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    public void testLogin_UserNotFound() {
        String userId = "u1";
        String password = "password";

        when(userAuthRepository.existsById(userId)).thenReturn(false);

        JSONObject result = userService.login(userId, password);

        assertEquals("账号不存在", result.getString("message"));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    public void testLogin_WrongPassword() {
        String userId = "u1";
        String password = "password";

        when(userAuthRepository.existsById(userId)).thenReturn(true);
        when(userAuthRepository.existsByUserIdAndPassword(userId, password)).thenReturn(false);

        JSONObject result = userService.login(userId, password);

        assertEquals("密码错误", result.getString("message"));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    public void testRegister_UserAlreadyExists() {
        String userId = "u1";
        String nickname = "nickname";
        String password = "password";

        when(userAuthRepository.existsById(userId)).thenReturn(true);

        JSONObject result = userService.register(userId, nickname, password);

        assertEquals("账号已存在", result.getString("message"));
        verify(userRepository, never()).save(any(User.class));
        verify(userAuthRepository, never()).save(any(UserAuth.class));
    }

    @Test
    public void testRegister_Success() {
        String userId = "u1";
        String nickname = "nickname";
        String password = "password";

        when(userAuthRepository.existsById(userId)).thenReturn(false);

        JSONObject result = userService.register(userId, nickname, password);

        assertEquals("注册成功", result.getString("message"));
        verify(userRepository).save(any(User.class));
        verify(userAuthRepository).save(any(UserAuth.class));
    }

    @Test
    public void testGetUserInfo_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        // 调用方法并获取结果
        JSONObject result = userService.getUserInfo("u1");

        // 验证返回的 JSON 对象
        assertEquals("获取用户信息成功", result.getString("message"));
        assertEquals(user.toJson("u1"), result.getJSONObject("data"));
    }

    @Test
    public void testGetUserInfo_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.getUserInfo("u1");

        // 验证返回的 JSON 对象
        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    public void testGetSelfInfo_UserExists() throws IOException, IOException {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        // 调用方法并获取结果
        JSONObject result = userService.getSelfInfo("u1");

        // 验证返回的 JSON 对象
        assertEquals(user.toJson("u1"), result);
    }

    @Test
    public void testGetSelfInfo_UserNotExists() throws IOException {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.getSelfInfo("u1");

        // 验证返回的 JSON 对象应为 null
        assertNull(result);
    }

    @Test
    public void testGetUserPosts_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONObject result = userService.getUserPosts("u1", "u1");

        // 验证返回的 JSON 对象是否正确
        assertEquals("获取用户动态成功", result.getString("message"));
    }

    @Test
    public void testGetUserPosts_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.getUserPosts("non_existing_user", "u1");

        // 验证返回的 JSON 对象是否正确
        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    public void testGetSelfPosts_UserExists() throws IOException {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONArray result = userService.getSelfPosts("u1");

        assertEquals(0, result.size()); // 假设用户有一条动态
    }

    @Test
    public void testGetSelfPosts_UserNotExists() throws IOException {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONArray result = userService.getSelfPosts("non_existing_user");

        assertNull(result);
    }

    @Test
    public void testGetUserCollects_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONObject result = userService.getUserCollects("u1", "u1");

        // 验证返回的 JSONObject 中包含 "data" 字段，并且不为 null
        assertEquals("获取用户收藏成功", result.getString("message"));
        assertEquals(0, result.getJSONArray("data").size()); // 假设用户的收藏为空
    }

    @Test
    public void testGetUserCollects_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.getUserCollects("non_existing_user", "u1");

        // 验证返回的 JSONObject 中包含错误信息
        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    public void testGetSelfCollects_UserExists() throws IOException {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONArray result = userService.getSelfCollects("u1");

        // 验证返回的 JSONArray 不为 null，具体内容根据具体实现而定
        assertEquals(0, result.size()); // 假设用户的收藏为空
    }

    @Test
    public void testGetSelfCollects_UserNotExists() throws IOException {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONArray result = userService.getSelfCollects("non_existing_user");

        // 验证返回的 JSONArray 应为 null，表示用户不存在
        assertEquals(null, result);
    }

    @Test
    public void testGetFriends_UserExists() throws IOException {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONArray result = userService.getFriends("u1");

        // 验证返回的 JSONArray 不为 null，具体内容根据具体实现而定
        assertEquals(1, result.size()); // 假设用户的好友列表为空
    }

    @Test
    public void testGetFriends_UserNotExists() throws IOException {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONArray result = userService.getFriends("non_existing_user");

        // 验证返回的 JSONArray 应为 null，表示用户不存在
        assertEquals(null, result);
    }

    @Test
    public void testFollow_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById("u1")).thenReturn(java.util.Optional.ofNullable(user));
        when(userRepository.findById("u2")).thenReturn(java.util.Optional.ofNullable(user2));

        // 调用方法并获取结果
        JSONObject result = userService.follow("u1", "u2");

        // 验证返回的 JSONObject 中包含成功信息
        assertEquals("关注成功", result.getString("message"));
    }

    @Test
    public void testFollow_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.follow("non_existing_user", "u2");

        // 验证返回的 JSONObject 中包含错误信息
        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    public void testFollow_AlreadyFollowing() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById("u1")).thenReturn(java.util.Optional.ofNullable(user));
        when(userRepository.findById("u2")).thenReturn(java.util.Optional.ofNullable(user2));

        // 调用方法并获取结果（u2 已经关注过 u1）
        JSONObject result = userService.follow("u2", "u1");

        // 验证返回的 JSONObject 中包含已关注错误信息
        assertEquals("已关注该用户", result.getString("message"));
    }

    @Test
    public void testUnfollow_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById("u1")).thenReturn(java.util.Optional.ofNullable(user));
        when(userRepository.findById("u2")).thenReturn(java.util.Optional.ofNullable(user2));

        // 调用方法并获取结果
        JSONObject result = userService.unfollow("u2", "u1");

        // 验证返回的 JSONObject 中包含成功信息
        assertEquals("取消关注成功", result.getString("message"));
    }

    @Test
    public void testUnfollow_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.unfollow("non_existing_user", "u2");

        // 验证返回的 JSONObject 中包含错误信息
        assertEquals("用户不存在", result.getString("message"));
    }

    @Test
    public void testUnfollow_NotFollowing() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById("u1")).thenReturn(java.util.Optional.ofNullable(user));
        when(userRepository.findById("u2")).thenReturn(java.util.Optional.ofNullable(user2));

        // 调用方法并获取结果（u2 没有关注过 u1）
        JSONObject result = userService.unfollow("u1", "u2");

        // 验证返回的 JSONObject 中包含未关注错误信息
        assertEquals("未关注该用户", result.getString("message"));
    }

    @Test
    public void testUpdateUserInfo_UserExists() {
        // 模拟 userRepository 返回的用户
        when(userRepository.findById("u1")).thenReturn(java.util.Optional.ofNullable(user));

        // 调用方法并获取结果
        JSONObject result = userService.updateUserInfo("u1", "newNickname", "newAvatar", "newMbti", 1, 30, "newArea", "newIntro");

        // 验证返回的 JSONObject 中包含成功信息
        assertEquals("更新用户信息成功", result.getString("message"));
        assertEquals("newNickname", user.getNickname()); // 验证用户信息已更新
    }

    @Test
    public void testUpdateUserInfo_UserNotExists() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(java.util.Optional.empty());

        // 调用方法并获取结果
        JSONObject result = userService.updateUserInfo("non_existing_user", "newNickname", "newAvatar", "newMbti", 1, 30, "newArea", "newIntro");

        // 验证返回的 JSONObject 中包含错误信息
        assertEquals("用户不存在", result.getString("message"));
    }
}
