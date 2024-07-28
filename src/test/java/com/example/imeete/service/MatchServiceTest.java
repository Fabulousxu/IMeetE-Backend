package com.example.imeete.service;

import com.example.imeete.dao.UserRepository;
import com.example.imeete.entity.User;
import com.example.imeete.service.impl.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MatchServiceTest {

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private MatchServiceImpl matchService;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // 初始化测试用户
        user1 = new User();
        user1.setUserId("u1");
        user1.setMbti("INTJ");
        user1.setSex(1);

        user2 = new User();
        user2.setUserId("u2");
        user2.setMbti("INTJ");
        user2.setSex(1);
    }

    @Test
    public void testMatchUser_UserNotFound() {
        // 模拟 userRepository 返回空
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        // 调用方法并获取结果
        CompletableFuture<User> future = matchService.matchUser("non_existing_user", "INTJ", 1);

        // 验证 future 立即完成并返回 null
        assertNull(future.join());

        // 额外验证：确保代码进入了 else 块
        assertTrue(future.isDone());
    }

    @Test
    public void testMatchUser_UserFound_NoMatch() throws ExecutionException, InterruptedException, TimeoutException {
        User user = new User();
        user.setUserId("user1");
        user.setMbti("INTJ");
        user.setSex(1);

        when(userRepository.findById("user1")).thenReturn(Optional.of(user));

        CompletableFuture<User> future = matchService.matchUser("user1", "INTJ", 1);

        assertNull(future.get(4, TimeUnit.SECONDS));
        verify(userRepository, times(1)).findById("user1");
    }

    @Test
    public void testMatchUser_UserFound_Match() {
        User user1 = new User();
        user1.setUserId("user1");
        user1.setMbti("INTJ");
        user1.setSex(1);

        User user2 = new User();
        user2.setUserId("user2");
        user2.setMbti("INTJ");
        user2.setSex(1);

        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.of(user2));

        // Simulate user2 already in the queue
        CompletableFuture<User> future2 = matchService.matchUser("user2", "INTJ", 1);
        assertFalse(future2.isDone());

        CompletableFuture<User> future1 = matchService.matchUser("user1", "INTJ", 1);

        assertEquals(user2, future1.join());
        assertEquals(user1, future2.join());
    }
}
