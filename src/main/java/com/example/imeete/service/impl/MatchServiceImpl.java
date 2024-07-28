package com.example.imeete.service.impl;

import com.example.imeete.dao.UserRepository;
import com.example.imeete.entity.User;
import com.example.imeete.service.MatchService;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchServiceImpl implements MatchService {
  private final Queue<User> waitingQueue = new ConcurrentLinkedQueue<>();
  private final ConcurrentHashMap<String, CompletableFuture<User>> futureMap =
      new ConcurrentHashMap<>();
  @Autowired private UserRepository userRepository;

  @Override
  public void initializeUserCollections(User user) {
      Hibernate.initialize(user.getFollowings());
      Hibernate.initialize(user.getFollowers());
      Hibernate.initialize(user.getPosts());
      Hibernate.initialize(user.getLikes());
      Hibernate.initialize(user.getCollects());
      Hibernate.initialize(user.getLikeComments());
  }

  @Override
  @Transactional
  public CompletableFuture<User> matchUser(String waitingUserId, String mbti, int sex) {
    User currentUser = userRepository.findById(waitingUserId).orElse(null);
    if (currentUser != null) {
        initializeUserCollections(currentUser);
    }
    else
    {
        CompletableFuture<User> future = new CompletableFuture<>();
        future.complete(null);
        return future;
    }
    synchronized (waitingQueue) {
        for (User matchedUser : waitingQueue) {
            if (!Objects.equals(matchedUser.getUserId(), waitingUserId)
                    && Objects.equals(matchedUser.getMbti(), mbti)
                    && matchedUser.getSex() == sex) {
                CompletableFuture<User> future = futureMap.remove(matchedUser.getUserId());
                if (future != null) {
                    future.complete(currentUser);
                }
                waitingQueue.remove(matchedUser);
                return CompletableFuture.completedFuture(matchedUser);
            }
        }
    }
    waitingQueue.add(currentUser);
    CompletableFuture<User> future = new CompletableFuture<>();
    futureMap.put(currentUser.getUserId(), future);
    return future
        .orTimeout(3, TimeUnit.SECONDS)
        .exceptionally(
            ex -> {
              waitingQueue.remove(currentUser);
              futureMap.remove(currentUser.getUserId());
              return null; // 返回null表示匹配失败
            });
  }
}
