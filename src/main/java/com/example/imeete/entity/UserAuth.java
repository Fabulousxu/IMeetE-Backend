package com.example.imeete.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_auth")
@Getter
@Setter
@NoArgsConstructor
public class UserAuth {
  @Id private String userId;
  private String password;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  public UserAuth(String userId, String password) {
    this.userId = userId;
    this.password = password;
  }
}
