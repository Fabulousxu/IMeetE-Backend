package com.example.imeete.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
  private static final MockCookie cookie = new MockCookie("userId", "u1");
  @Autowired private MockMvc mockMvc;
  @Autowired private PostController postController;

  @Test
  public void getPostInfoTest() throws Exception {
    mockMvc.perform(get("/post?id=1").cookie(cookie)).andExpect(status().isOk());
  }

  @Test
  public void getCommentsTest() throws Exception {
    mockMvc
        .perform(get("/post/comment?postId=1&lastCommentId=1").cookie(cookie))
        .andExpect(status().isOk());
  }

  @Test
  public void postTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("title", "单元测试帖子");
    body.put("cover", "");
    body.put("content", "单元测试帖子内容");
    body.put("category", "生活");
    body.put("mbti", "INTJ");
    mockMvc
        .perform(
            post("/post")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void commentTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("id", 1);
    body.put("content", "单元测试评论");
    mockMvc
        .perform(
            post("/post/comment")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void likeTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("id", 1);
    mockMvc
        .perform(
            post("/post/like")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void dislikeTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("id", 1);
    mockMvc
        .perform(
            post("/post/dislike")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void collectTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("id", 1);
    mockMvc
        .perform(
            post("/post/collect")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void uncollectTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("id", 1);
    mockMvc
        .perform(
            post("/post/uncollect")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void getPostsByMbtiTest() throws Exception {
    mockMvc
        .perform(get("/post/mbti?mbti=INTJ&lastPostId=1").cookie(cookie))
        .andExpect(status().isOk());
  }

  @Test
  public void likeCommentTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("commentId", 1);
    mockMvc
        .perform(
            post("/post/comment/like")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }

  @Test
  public void dislikeCommentTest() throws Exception {
    JSONObject body = new JSONObject();
    body.put("commentId", 1);
    mockMvc
        .perform(
            post("/post/comment/dislike")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body.toJSONString()))
        .andExpect(status().isOk());
  }
}
