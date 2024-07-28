package com.example.imeete.dao;

import com.example.imeete.entity.MbtiTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MbtiTestRepository extends JpaRepository<MbtiTest, Integer> {}
