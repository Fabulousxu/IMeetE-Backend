package com.example.imeete.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.*;
import com.example.imeete.entity.Category;
import com.example.imeete.entity.Comment;
import com.example.imeete.entity.Post;
import com.example.imeete.entity.User;
import com.example.imeete.service.PostService;
import com.example.imeete.util.Util;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
  @Autowired private PostRepository postRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CategoryRepository categoryRepository;
  @Autowired private CommentRepository commentRepository;
  @Autowired private HttpServletResponse response;

  @Override
  public JSONObject getPostInfo(int postId, String selfId) throws IOException {
    Post post = postRepository.findById(postId).orElse(null);
    if (post == null) {
      response.sendError(404);
      return null;
    }
    post.setWatch(post.getWatch() + 1);
    postRepository.save(post);
    return post.toJson(selfId);
  }

  @Override
  public JSONArray getComments(int postId, long lastCommentId, String selfId) throws IOException {
    Post post = postRepository.findById(postId).orElse(null);
    if (post == null) response.sendError(404);
    return post == null ? null : post.get10CommentsJson(lastCommentId, selfId);
  }

  @Override
  public JSONObject post(
      String selfId,
      String title,
      String cover,
      String content,
      Set<String> categories,
      String mbti) {
    User user = userRepository.findById(selfId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    Post post = new Post();
    post.setUser(user);
    post.setTitle(title);
    post.setCover(cover);
    post.setContent(content);
    post.setMbti(mbti);
    postRepository.save(post);
    for (String categoryName : categories) {
      Category category = categoryRepository.findById(categoryName).orElse(null);
      if (category != null) {
        category.getPosts().add(post);
        categoryRepository.save(category);
      }
    }
    return Util.successResponse("发帖成功");
  }

  @Override
  public JSONObject comment(int postId, String selfId, String content) {
    User user = userRepository.findById(selfId).orElse(null);
    Post post = postRepository.findById(postId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (post == null) return Util.errorResponse("帖子不存在");
    Comment comment = new Comment();
    comment.setUser(user);
    comment.setPost(post);
    comment.setContent(content);
    post.getComments().add(comment);
    postRepository.save(post);
    return Util.successResponse("评论成功");
  }

  @Override
  public JSONObject like(int postId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Post post = postRepository.findById(postId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (post == null) return Util.errorResponse("帖子不存在");
    if (post.getLikers().contains(user)) return Util.errorResponse("帖子已点赞");
    post.getLikers().add(user);
    postRepository.save(post);
    return Util.successResponse("点赞成功");
  }

  @Override
  public JSONObject dislike(int postId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Post post = postRepository.findById(postId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (post == null) return Util.errorResponse("帖子不存在");
    if (!post.getLikers().contains(user)) return Util.errorResponse("帖子未点赞");
    post.getLikers().remove(user);
    postRepository.save(post);
    return Util.successResponse("取消点赞");
  }

  @Override
  public JSONObject collect(int postId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Post post = postRepository.findById(postId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (post == null) return Util.errorResponse("帖子不存在");
    if (post.getCollectors().contains(user)) return Util.errorResponse("帖子已收藏");
    post.getCollectors().add(user);
    postRepository.save(post);
    return Util.successResponse("收藏成功");
  }

  @Override
  public JSONObject uncollect(int postId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Post post = postRepository.findById(postId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (post == null) return Util.errorResponse("帖子不存在");
    if (!post.getCollectors().contains(user)) return Util.errorResponse("帖子未收藏");
    post.getCollectors().remove(user);
    postRepository.save(post);
    return Util.successResponse("取消收藏");
  }

  @Override
  public JSONArray getPosts(String type, String categoryName, int lastPostId, String selfId) {
    JSONArray res = new JSONArray();
    if (type.equals("发现") && !categoryName.equals("推荐")) {
      Category category = categoryRepository.findById(categoryName).orElse(null);
      return category == null ? res : category.get10PostsJson(lastPostId, selfId);
    }
    for (Post post :
        postRepository.findTop10ByPostIdBeforeOrderByPostIdDesc(
            lastPostId == 0 ? Integer.MAX_VALUE : lastPostId)) res.add(post.toJson(selfId));
    return res;
  }

  @Override
  public JSONArray getPostsByMbti(Set<String> mbti, int lastPostId, String selfId) {
    JSONArray res = new JSONArray();
    for (Post post :
        postRepository.findTop10ByMbtiInAndPostIdBeforeOrderByPostIdDesc(
            mbti, lastPostId == 0 ? Integer.MAX_VALUE : lastPostId)) res.add(post.toJson(selfId));
    return res;
  }

  @Override
  public JSONArray searchPosts(String keyword, int lastPostId, String selfId) {
    JSONArray res = new JSONArray();
    if (lastPostId == 0) lastPostId = Integer.MAX_VALUE;
    for (Post post :
        postRepository
            .findTop10ByTitleContainsAndPostIdBeforeOrContentContainsAndPostIdBeforeOrUser_NicknameContainsAndPostIdBeforeOrderByPostIdDesc(
                keyword, lastPostId, keyword, lastPostId, keyword, lastPostId))
      res.add(post.toJson(selfId));
    return res;
  }

  @Override
  public JSONObject likeComment(long commentId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Comment comment = commentRepository.findById(commentId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (comment == null) return Util.errorResponse("评论不存在");
    if (comment.getLikers().contains(user)) return Util.errorResponse("评论已点赞");
    comment.getLikers().add(user);
    commentRepository.save(comment);
    return Util.successResponse("点赞成功");
  }

  @Override
  public JSONObject dislikeComment(long commentId, String selfId) {
    User user = userRepository.findById(selfId).orElse(null);
    Comment comment = commentRepository.findById(commentId).orElse(null);
    if (user == null) return Util.errorResponse("用户不存在");
    if (comment == null) return Util.errorResponse("评论不存在");
    if (!comment.getLikers().contains(user)) return Util.errorResponse("评论未点赞");
    comment.getLikers().remove(user);
    commentRepository.save(comment);
    return Util.successResponse("取消点赞成功");
  }
}
