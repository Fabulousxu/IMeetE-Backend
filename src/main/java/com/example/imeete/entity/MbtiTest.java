package com.example.imeete.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MbtiTest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int testId;

  private String question;
}
