package com.example.imeete.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
  JSONObject login(String userId, String password);

  JSONObject register(String userId, String nickname, String password);

  JSONObject getUserInfo(String userId);

  JSONObject getSelfInfo(String selfId) throws IOException;

  JSONObject getUserPosts(String userId, String selfId);

  JSONArray getSelfPosts(String selfId) throws IOException;

  JSONObject getUserCollects(String userId, String selfId);

  JSONArray getSelfCollects(String selfId) throws IOException;

  JSONArray getFriends(String userId) throws IOException;

  JSONObject follow(String userId, String selfId);

  JSONObject unfollow(String userId, String selfId);

  JSONObject updateUserInfo(
      String userId,
      String nickname,
      String avatar,
      String mbti,
      int sex,
      int age,
      String area,
      String intro);
}
