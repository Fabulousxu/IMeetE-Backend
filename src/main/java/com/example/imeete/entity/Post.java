package com.example.imeete.entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "post")
@Getter
@Setter
public class Post {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int postId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  private String title;
  private String cover;
  private String content;
  private String mbti;
  private int watch;
  private int share;

  @ManyToMany(mappedBy = "posts", cascade = CascadeType.ALL)
  private Set<Category> categories = new HashSet<>();

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("commentId DESC")
  private Set<Comment> comments;

  @ManyToMany
  @JoinTable(
      name = "post_like",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> likers;

  @ManyToMany
  @JoinTable(
      name = "collect",
      joinColumns = @JoinColumn(name = "post_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> collectors;

  public JSONObject toJson(String selfId) {
    JSONObject json = new JSONObject();
    json.put("id", postId);
    json.put("user", user.toJson(selfId));
    json.put("title", title);
    json.put("cover", cover);
    json.put("content", content);
    json.put("time", createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    json.put("watchCount", watch);
    json.put("likeCount", likers.size());
    json.put("collectCount", collectors.size());
    json.put("shareCount", share);
    json.put("commentCount", comments.size());
    json.put("liked", likers.stream().anyMatch(u -> u.getUserId().equals(selfId)));
    json.put("collected", collectors.stream().anyMatch(u -> u.getUserId().equals(selfId)));
    return json;
  }

  public JSONArray get10CommentsJson(long lastCommentId, String selfId) {
    JSONArray json = new JSONArray();
    for (Comment comment : comments)
      if (comment.getCommentId() < lastCommentId || lastCommentId == 0) {
        json.add(comment.toJson(selfId));
        if (json.size() == 10) break;
      }
    return json;
  }
}
