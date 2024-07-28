package com.example.imeete.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.UserAuthRepository;
import com.example.imeete.dao.UserRepository;
import com.example.imeete.entity.User;
import com.example.imeete.entity.UserAuth;
import com.example.imeete.service.UserService;
import com.example.imeete.util.Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired private UserRepository userRepository;
  @Autowired private UserAuthRepository userAuthRepository;
  @Autowired private HttpServletResponse response;

  @Override
  public JSONObject login(String userId, String password) {
    if (!userAuthRepository.existsById(userId)) return Util.errorResponse("账号不存在");
    if (!userAuthRepository.existsByUserIdAndPassword(userId, password))
      return Util.errorResponse("密码错误");
    response.addCookie(new Cookie("userId", userId));
    return Util.successResponse("登录成功");
  }

  @Override
  public JSONObject register(String userId, String nickname, String password) {
    if (userAuthRepository.existsById(userId)) return Util.errorResponse("账号已存在");
    User user = new User(userId, nickname);
    userRepository.save(user);
    userAuthRepository.save(new UserAuth(userId, password));
    return Util.successResponse("注册成功");
  }

  @Override
  public JSONObject getUserInfo(String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    JSONObject res = Util.successResponse("获取用户信息成功");
    res.put("data", user.toJson(selfId));
    return res;
  }

  @Override
  public JSONObject getSelfInfo(String userId) throws IOException {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) response.sendError(401);
    return user == null ? null : user.toJson(userId);
  }

  @Override
  public JSONObject getUserPosts(String userId, String selfId) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    JSONObject res = Util.successResponse("获取用户动态成功");
    res.put("data", user.getPostsJson(selfId));
    return res;
  }

  @Override
  public JSONArray getSelfPosts(String selfId) throws IOException {
    User user = userRepository.findById(selfId).orElse(null);
    if (user == null) response.sendError(401);
    return user == null ? null : user.getPostsJson(selfId);
  }

  @Override
  public JSONObject getUserCollects(String userId, String selfId) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    JSONObject res = Util.successResponse("获取用户收藏成功");
    res.put("data", user.getCollectsJson(selfId));
    return res;
  }

  @Override
  public JSONArray getSelfCollects(String selfId) throws IOException {
    User user = userRepository.findById(selfId).orElse(null);
    if (user == null) response.sendError(401);
    return user == null ? null : user.getCollectsJson(selfId);
  }

  @Override
  public JSONArray getFriends(String userId) throws IOException {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) response.sendError(401);
    return user == null ? null : user.getFriendsJson();
  }

  @Override
  public JSONObject follow(String userId, String selfId) {
    User user = userRepository.findById(userId).orElse(null);
    User self = userRepository.findById(selfId).orElse(null);
    if (user == null || self == null) return Util.errorResponse("用户不存在");
    if (self.getFollowings().contains(user)) return Util.errorResponse("已关注该用户");
    self.getFollowings().add(user);
    userRepository.save(self);
    return Util.successResponse("关注成功");
  }

  @Override
  public JSONObject unfollow(String userId, String selfId) {
    User user = userRepository.findById(userId).orElse(null);
    User self = userRepository.findById(selfId).orElse(null);
    if (user == null || self == null) return Util.errorResponse("用户不存在");
    if (!self.getFollowings().contains(user)) return Util.errorResponse("未关注该用户");
    self.getFollowings().remove(user);
    userRepository.save(self);
    return Util.successResponse("取消关注成功");
  }

  @Override
  public JSONObject updateUserInfo(
      String userId,
      String nickname,
      String avatar,
      String mbti,
      int sex,
      int age,
      String area,
      String intro) {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    user.setNickname(nickname);
    user.setAvatar(avatar);
    user.setMbti(mbti);
    user.setSex(sex);
    user.setAge(age);
    user.setArea(area);
    user.setIntro(intro);
    userRepository.save(user);
    return Util.successResponse("更新用户信息成功");
  }
}
