package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONArray;
import com.example.imeete.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@CrossOrigin
public class SquareController {
  @Autowired private PostService postService;

  @GetMapping("/square")
  public JSONArray getPosts(
      String type, String category, int lastPostId, @CookieValue("userId") String userId) {
    return postService.getPosts(type, category, lastPostId, userId);
  }

  @GetMapping("/search")
  public JSONArray searchPosts(
      String keyword, int lastPostId, @CookieValue("userId") String userId) {
    return postService.searchPosts(keyword, lastPostId, userId);
  }
}
