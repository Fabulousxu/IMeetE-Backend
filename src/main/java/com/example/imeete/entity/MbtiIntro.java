package com.example.imeete.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MbtiIntro {
  @Id private String mbti;
  private String intro;
}
