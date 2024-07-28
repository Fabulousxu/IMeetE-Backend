package com.example.imeete.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.service.PostService;
import com.example.imeete.util.Util;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@CrossOrigin
public class PostController {
  @Autowired private PostService postService;

  @GetMapping
  public JSONObject getPostInfo(int id, @CookieValue("userId") String userId) throws IOException {
    return postService.getPostInfo(id, userId);
  }

  @GetMapping("/comment")
  public JSONArray getComments(int postId, long lastCommentId, @CookieValue("userId") String userId)
      throws IOException {
    return postService.getComments(postId, lastCommentId, userId);
  }

  @PostMapping
  public JSONObject post(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    System.out.println(body);
    return postService.post(
        userId,
        body.getString("title"),
        body.getString("cover"),
        body.getString("content"),
        Set.of(body.getString("category").split("\\s+")),
        body.getString("mbti"));
  }

  @PostMapping("/comment")
  public JSONObject comment(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.comment(body.getIntValue("id"), userId, body.getString("content"));
  }

  @PostMapping("/like")
  public JSONObject like(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.like(body.getIntValue("id"), userId);
  }

  @PostMapping("/dislike")
  public JSONObject dislike(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.dislike(body.getIntValue("id"), userId);
  }

  @PostMapping("/collect")
  public JSONObject collect(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.collect(body.getIntValue("id"), userId);
  }

  @PostMapping("/uncollect")
  public JSONObject uncollect(@RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.uncollect(body.getIntValue("id"), userId);
  }

  @GetMapping("/mbti")
  public JSONArray getPostsByMbti(
      String mbti, int lastPostId, @CookieValue("userId") String userId) {
    return postService.getPostsByMbti(Util.getMbtiSet(mbti), lastPostId, userId);
  }

  @PostMapping("/comment/like")
  public JSONObject likeComment(
      @RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.likeComment(body.getLongValue("commentId"), userId);
  }

  @PostMapping("/comment/dislike")
  public JSONObject dislikeComment(
      @RequestBody JSONObject body, @CookieValue("userId") String userId) {
    return postService.dislikeComment(body.getLongValue("commentId"), userId);
  }
}
