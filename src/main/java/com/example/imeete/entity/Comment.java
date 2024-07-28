package com.example.imeete.entity;

import com.alibaba.fastjson2.JSONObject;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long commentId;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  private String content;

  @ManyToMany
  @JoinTable(
      name = "comment_like",
      joinColumns = @JoinColumn(name = "comment_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> likers;

  public JSONObject toJson(String selfId) {
    JSONObject json = new JSONObject();
    json.put("id", commentId);
    json.put("time", createdAt);
    json.put("content", content);
    json.put("likeCount", likers.size());
    json.put("user", user.toJson(selfId));
    json.put("liked", likers.stream().anyMatch(user -> user.getUserId().equals(selfId)));
    return json;
  }
}
