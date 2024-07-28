package com.example.imeete.dao;

import com.example.imeete.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {}
