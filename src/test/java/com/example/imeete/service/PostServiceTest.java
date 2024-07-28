package com.example.imeete.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.imeete.dao.CategoryRepository;
import com.example.imeete.dao.CommentRepository;
import com.example.imeete.dao.PostRepository;
import com.example.imeete.dao.UserRepository;
import com.example.imeete.entity.Category;
import com.example.imeete.entity.Comment;
import com.example.imeete.entity.Post;
import com.example.imeete.entity.User;
import com.example.imeete.service.impl.PostServiceImpl;
import com.example.imeete.util.Util;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.grammars.hql.HqlParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostServiceTest {

  @MockBean
  private PostRepository postRepository;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private CategoryRepository categoryRepository;

  @MockBean
  private CommentRepository commentRepository;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private PostServiceImpl postService;

  private User user;
  private Post post;
  private Category category;
  private Comment comment;

  private List<Post> mockPosts;

  private List<Comment> comments;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    user = new User();
    user.setUserId("u1");
    user.setPosts(new HashSet<>());
    user.setCollects(new HashSet<>());
    user.setLikes(new HashSet<>());
    user.setFollowers(new HashSet<>());
    user.setFollowings(new HashSet<>());
    user.setLikeComments(new HashSet<>());

    post = new Post();
    post.setCategories(new HashSet<>());
    post.setPostId(1);
    post.setComments(new HashSet<>());
    post.setCollectors(new HashSet<>());
    post.setCategories(new HashSet<>());
    post.setLikers(new HashSet<>());
    post.setUser(user);
    LocalDateTime localDateTime = LocalDateTime.now();
    post.setCreatedAt(localDateTime);

    category = new Category();

    comment = new Comment();
    comment.setLikers(new HashSet<>());
    comment.setPost(post);
    comment.setUser(user);
    comment.setCommentId(1);

    mockPosts = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      Post post1 = new Post();
      post1.setPostId(i);
      post1.setTitle("Post " + i);
      post1.setComments(new HashSet<>());
      post1.setCollectors(new HashSet<>());
      post1.setCategories(new HashSet<>());
      post1.setLikers(new HashSet<>());
      post1.setCategories(new HashSet<>());
      post1.setUser(user);
      post1.setCreatedAt(localDateTime);
      mockPosts.add(post1);
    }

    comments = new ArrayList<>();

    for(int i = 1; i <= 10; ++ i)
    {
      Comment comment1 = new Comment();
      comment1.setLikers(new HashSet<>());
      comment1.setPost(post);
      comment1.setUser(user);
      comment1.setCommentId(1);

      comments.add(comment1);
    }
  }

  @Test
  public void testGetPostInfo_Success() throws IOException {
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.getPostInfo(1, "u1");

    assertEquals(1, result.getIntValue("id"));
  }

  @Test
  public void testGetPostInfo_PostNotFound() throws IOException {
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.getPostInfo(1, "u1");

    assertEquals(null, result);
    verify(response, times(1)).sendError(404);
  }

  @Test
  public void testPost_UserNotFound() {
    when(userRepository.findById("u1")).thenReturn(Optional.empty());

    Set<String> categories = new HashSet<>();
    categories.add("category1");

    JSONObject response = postService.post("u1", "title", "cover", "content", categories, "INTJ");

    assertEquals(Util.errorResponse("用户不存在"), response);
  }

  @Test
  public void testPost_AllCategoriesExist() {
    when(userRepository.findById("u1")).thenReturn(Optional.of(user));
    when(categoryRepository.findById("category1")).thenReturn(Optional.of(category));

    Set<String> categories = new HashSet<>();
    categories.add("category1");

    JSONObject response = postService.post("u1", "title", "cover", "content", categories, "INTJ");

    assertEquals(Util.successResponse("发帖成功"), response);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  public void testPost_PartialCategoriesExist() {
    when(userRepository.findById("u1")).thenReturn(Optional.of(user));
    when(categoryRepository.findById("category1")).thenReturn(Optional.of(category));
    when(categoryRepository.findById("category2")).thenReturn(Optional.empty());

    Set<String> categories = new HashSet<>();
    categories.add("category1");
    categories.add("category2");

    JSONObject response = postService.post("u1", "title", "cover", "content", categories, "INTJ");

    assertEquals(Util.successResponse("发帖成功"), response);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  public void testPost_Success() {
    when(userRepository.findById("u1")).thenReturn(Optional.of(user));
    when(categoryRepository.findById("category1")).thenReturn(Optional.of(category));

    Set<String> categories = new HashSet<>();
    categories.add("category1");

    JSONObject response = postService.post("u1", "title", "cover", "content", categories, "INTJ");

    assertEquals(Util.successResponse("发帖成功"), response);
    verify(postRepository, times(1)).save(any(Post.class));
  }

  @Test
  public void testGetComments_PostNotFound() throws IOException {
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONArray result = postService.getComments(1, 0, "u1");

    assertEquals(null, result);
    verify(response, times(1)).sendError(404);
  }

  @Test
  public void testGetComments_PostFound() throws IOException {
    Post post2 = Mockito.mock(Post.class);
    post2.setCategories(new HashSet<>());
    post2.setPostId(2);
    post2.setComments(new HashSet<>());
    post2.setCollectors(new HashSet<>());
    post2.setCategories(new HashSet<>());
    post2.setLikers(new HashSet<>());
    post2.setUser(user);
    LocalDateTime localDateTime = LocalDateTime.now();
    post2.setCreatedAt(localDateTime);
    // Stub the findById method to return Optional.of(post) for any integer (1 in this case)
    when(postRepository.findById(eq(1))).thenReturn(Optional.of(post2));

    // Mock an empty JSONArray for comments
    JSONArray json = new JSONArray();
    for (Comment comment : comments)
      json.add(comment.toJson(comment.getUser().getUserId()));

    when(post2.get10CommentsJson(anyLong(), anyString())).thenReturn(json);

    // Call the method under test
    JSONArray result = postService.getComments(1, 0, "u1");

    // Assert that the result matches the mockComments
    assertEquals(json, result);

    // Verify that postRepository.save(post) was never called
    verify(postRepository, never()).save(post);
    }

  @Test
  public void testComment_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.comment(1, "u1", "Test Comment");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testComment_PostNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.comment(1, "u1", "Test Comment");

    assertEquals("帖子不存在", result.getString("message"));
  }

  @Test
  public void testComment_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.comment(1, "u1", "Test Comment");

    assertEquals("评论成功", result.getString("message"));
    assertEquals(1, post.getComments().size());
  }

  @Test
  public void testLike_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.like(1, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testLike_PostNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.like(1, "u1");

    assertEquals("帖子不存在", result.getString("message"));
  }

  @Test
  public void testLike_AlreadyLiked() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
    post.getLikers().add(user);

    JSONObject result = postService.like(1, "u1");

    assertEquals("帖子已点赞", result.getString("message"));
  }

  @Test
  public void testLike_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.like(1, "u1");

    assertEquals("点赞成功", result.getString("message"));
    assertEquals(1, post.getLikers().size());
  }

  @Test
  public void testDislike_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.dislike(1, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testDislike_PostNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.dislike(1, "u1");

    assertEquals("帖子不存在", result.getString("message"));
  }

  @Test
  public void testDislike_NotLiked() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.dislike(1, "u1");

    assertEquals("帖子未点赞", result.getString("message"));
  }

  @Test
  public void testDislike_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
    post.getLikers().add(user);

    JSONObject result = postService.dislike(1, "u1");

    assertEquals("取消点赞", result.getString("message"));
    assertEquals(0, post.getLikers().size());
  }

  @Test
  public void testCollect_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.collect(1, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testCollect_PostNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.collect(1, "u1");

    assertEquals("帖子不存在", result.getString("message"));
  }

  @Test
  public void testCollect_AlreadyCollected() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
    post.getCollectors().add(user);

    JSONObject result = postService.collect(1, "u1");

    assertEquals("帖子已收藏", result.getString("message"));
  }

  @Test
  public void testCollect_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.collect(1, "u1");

    assertEquals("收藏成功", result.getString("message"));
    assertEquals(1, post.getCollectors().size());
  }

  @Test
  public void testUncollect_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.uncollect(1, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testUncollect_PostNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

    JSONObject result = postService.uncollect(1, "u1");

    assertEquals("帖子不存在", result.getString("message"));
  }

  @Test
  public void testUncollect_NotCollected() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

    JSONObject result = postService.uncollect(1, "u1");

    assertEquals("帖子未收藏", result.getString("message"));
  }

  @Test
  public void testUncollect_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
    post.getCollectors().add(user);

    JSONObject result = postService.uncollect(1, "u1");

    assertEquals("取消收藏", result.getString("message"));
    assertEquals(0, post.getCollectors().size());
  }

  @Test
  public void testGetPosts_ByCategory() {
    // 模拟传入的参数
    String type = "发现";
    String categoryName = "科技";
    int lastPostId = 5;
    String selfId = "u1";

    // 模拟 categoryRepository 返回的 Category 对象
    Category category = new Category();
    category.setName("科技");
    category.setPosts(new HashSet<>(mockPosts.subList(0, 5))); // 设置 Category 的帖子集合

    when(categoryRepository.findById(anyString())).thenReturn(Optional.of(category));

    JSONArray result = postService.getPosts(type, categoryName, lastPostId, selfId);

    assertEquals(4, result.size()); // 验证返回的 JSONArray 大小应为 5
  }

  @Test
  public void testGetPosts_Default() {
    // 模拟传入的参数
    String type = "推荐";
    String categoryName = "";
    int lastPostId = 0;
    String selfId = "u1";

    // 模拟 postRepository 返回的帖子列表
    when(postRepository.findTop10ByPostIdBeforeOrderByPostIdDesc(anyInt()))
            .thenReturn(mockPosts);

    JSONArray result = postService.getPosts(type, categoryName, lastPostId, selfId);

    assertEquals(10, result.size()); // 验证返回的 JSONArray 大小应为 10
  }

  @Test
  public void testGetPostsByMbti() {
    Set<String> mbti = new HashSet<>(Arrays.asList("INTJ", "ENTP"));
    int lastPostId = 3;
    String selfId = "u1";

    when(postRepository.findTop10ByMbtiInAndPostIdBeforeOrderByPostIdDesc(anySet(), anyInt()))
            .thenReturn(mockPosts);

    JSONArray result = postService.getPostsByMbti(mbti, lastPostId, selfId);

    assertEquals(mockPosts.size(), result.size());
  }

  @Test
  public void testSearchPosts() {
    String keyword = "Title";
    int lastPostId = 0;
    String selfId = "u1";

    when(postRepository
            .findTop10ByTitleContainsAndPostIdBeforeOrContentContainsAndPostIdBeforeOrUser_NicknameContainsAndPostIdBeforeOrderByPostIdDesc(
                    anyString(), anyInt(), anyString(), anyInt(), anyString(), anyInt()))
            .thenReturn(mockPosts);

    JSONArray result = postService.searchPosts(keyword, lastPostId, selfId);

    assertEquals(mockPosts.size(), result.size());
    for (int i = 0; i < result.size(); i++) {
      assertEquals(mockPosts.get(i).toJson(mockPosts.get(i).getUser().getUserId()), result.get(i));
    }
  }

  @Test
  public void testLikeComment_Success() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

    JSONObject result = postService.likeComment(1L, "u1");

    assertEquals("点赞成功", result.getString("message"));
  }

  @Test
  public void testLikeComment_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.likeComment(1L, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testLikeComment_CommentNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

    JSONObject result = postService.likeComment(1L, "u1");

    assertEquals("评论不存在", result.getString("message"));
  }

  @Test
  public void testLikeComment_AlreadyLiked() {
    comment.getLikers().add(user);
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

    JSONObject result = postService.likeComment(1L, "u1");

    assertEquals("评论已点赞", result.getString("message"));
  }

  @Test
  public void testDislikeComment_Success() {
    comment.getLikers().add(user);
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

    JSONObject result = postService.dislikeComment(1L, "u1");

    assertEquals("取消点赞成功", result.getString("message"));
  }

  @Test
  public void testDislikeComment_UserNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.empty());

    JSONObject result = postService.dislikeComment(1L, "u1");

    assertEquals("用户不存在", result.getString("message"));
  }

  @Test
  public void testDislikeComment_CommentNotFound() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

    JSONObject result = postService.dislikeComment(1L, "u1");

    assertEquals("评论不存在", result.getString("message"));
  }

  @Test
  public void testDislikeComment_NotLiked() {
    when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
    when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

    JSONObject result = postService.dislikeComment(1L, "u1");

    assertEquals("评论未点赞", result.getString("message"));
  }
}
