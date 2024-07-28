package com.example.imeete.service;
import com.example.imeete.entity.User;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public interface MatchService {
    CompletableFuture<User> matchUser(String waitingUserId, String mbti, int sex);
    void initializeUserCollections(User user);
}
