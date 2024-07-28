package com.example.imeete.dao;

import com.example.imeete.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
  boolean existsByUserIdAndPassword(String userId, String password);
}
