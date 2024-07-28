package com.example.imeete.entity;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.persistence.*;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
  @Id private String userId;
  private String nickname;
  private String avatar = "";
  private String mbti = "NONE";
  private int sex = -1;
  private int age = 0;
  private String area = "";
  private String intro = "";

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "follow",
      joinColumns = @JoinColumn(name = "follower_id"),
      inverseJoinColumns = @JoinColumn(name = "followee_id"))
  private Set<User> followings;

  @ManyToMany(mappedBy = "followings", cascade = CascadeType.ALL)
  private Set<User> followers;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("createdAt DESC")
  private Set<Post> posts;

  @ManyToMany(mappedBy = "likers", cascade = CascadeType.ALL)
  private Set<Post> likes;

  @ManyToMany(mappedBy = "collectors", cascade = CascadeType.ALL)
  private Set<Post> collects;

  @ManyToMany(mappedBy = "likers", cascade = CascadeType.ALL)
  private Set<Comment> likeComments;

  public User(String userId, String nickname) {
    this.userId = userId;
    this.nickname = nickname;
  }

  public JSONObject toJson(String selfId) {
    JSONObject json = new JSONObject();
    json.put("userId", userId);
    json.put("nickname", nickname);
    json.put("avatar", avatar);
    json.put("mbti", mbti);
    json.put("sex", sex);
    json.put("age", age);
    json.put("area", area);
    json.put("intro", intro);
    json.put("followingCount", followings.size());
    json.put("followerCount", followers.size());
    json.put("followed", followings.stream().anyMatch(user -> user.getUserId().equals(selfId)));
    return json;
  }

  public JSONArray getPostsJson(String selfId) {
    JSONArray json = new JSONArray();
    for (Post post : posts) json.add(post.toJson(selfId));
    return json;
  }

  public JSONArray getCollectsJson(String selfId) {
    JSONArray json = new JSONArray();
    for (Post post : collects) json.add(post.toJson(selfId));
    return json;
  }

  public JSONArray getFriendsJson() {
    JSONArray json = new JSONArray();
    for (User user : followings) if (followers.contains(user)) json.add(user.toJson(userId));
    return json;
  }
}
