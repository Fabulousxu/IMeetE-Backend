package com.example.imeete.dao;

import com.example.imeete.entity.Post;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
  List<Post> findTop10ByPostIdBeforeOrderByPostIdDesc(int lastPostId);

  List<Post> findTop10ByMbtiInAndPostIdBeforeOrderByPostIdDesc(Set<String> mbti, int lastPostId);

  List<Post>
      findTop10ByTitleContainsAndPostIdBeforeOrContentContainsAndPostIdBeforeOrUser_NicknameContainsAndPostIdBeforeOrderByPostIdDesc(
          String keyword1,
          int lastPostId1,
          String keyword2,
          int lastPostId2,
          String keyword3,
          int lastPostId3);
}
