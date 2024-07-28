package com.example.imeete.entity;

import com.alibaba.fastjson2.JSONArray;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {
  @Id String category;

  @ManyToMany
  @JoinTable(
      name = "post_category",
      joinColumns = @JoinColumn(name = "category"),
      inverseJoinColumns = @JoinColumn(name = "post_id"))
  @OrderBy("postId DESC")
  private Set<Post> posts;

  public JSONArray get10PostsJson(int lastPostId, String selfId) {
    JSONArray json = new JSONArray();
    for (Post post : posts)
      if (post.getPostId() < lastPostId || lastPostId == 0) {
        json.add(post.toJson(selfId));
        if (json.size() == 10) break;
      }
    return json;
  }

  public void setName(String name) {
    this.category = name;
  }

  public void setPosts(HashSet<Post> posts) {
    this.posts = posts;
  }
}
