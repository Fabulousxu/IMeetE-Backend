package com.example.imeete.dao;

import com.example.imeete.entity.MbtiIntro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MbtiIntroRepository extends JpaRepository<MbtiIntro, String> {}
