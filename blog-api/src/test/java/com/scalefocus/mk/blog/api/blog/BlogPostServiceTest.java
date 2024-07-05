package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.auth.AuthService;
import com.scalefocus.mk.blog.api.shared.persistence.PersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
final class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PersistenceService persistenceService;

    @Mock
    private BlogTagRepository tagRepository;

    @Mock
    private BlogPostMapper blogPostMapper;

    @InjectMocks
    private BlogPostService blogPostService;

    private static final String MOCKITO_USER = "Mockito";
    private static final String TAG_NAME = "Test tag";

    @Test
    void createBlogPost() {
        BlogPostDto blogPostDto = new BlogPostDto("Test Title", "Test Content");
        BlogPost blogPost = new BlogPost();
        setCreateBlogPostConditions(blogPostDto, blogPost);
        ResponseEntity<String> response = blogPostService.createBlogPost(blogPostDto);
        verifyCreateBlogPostExecution(blogPostDto, blogPost, response.getStatusCode());
    }

    private void verifyCreateBlogPostExecution(BlogPostDto blogPostDto, BlogPost blogPost, HttpStatusCode status) {
        assertEquals(HttpStatus.CREATED, status);
        verify(blogPostRepository, times(1)).existsByTitle(blogPostDto.title());
        verify(persistenceService, times(1)).persist(blogPost);
        verify(authService, times(1)).getCurrentUsername();
    }

    private void setCreateBlogPostConditions(BlogPostDto blogPostDto, BlogPost blogPost) {
        when(blogPostRepository.existsByTitle(blogPostDto.title())).thenReturn(false);
        when(blogPostMapper.blogPostDtoToEntity(blogPostDto)).thenReturn(blogPost);
        when(persistenceService.persist(blogPost)).thenReturn(true);
        when(authService.getCurrentUsername()).thenReturn(MOCKITO_USER);
    }

    @Test
    void getAllBlogPosts() {
        Set<BlogPostDto> blogPosts = new HashSet<>();
        when(blogPostRepository.getAllBlogPostsAsDto()).thenReturn(blogPosts);
        Set<BlogPostDto> result = blogPostService.getAllBlogPosts();
        assertEquals(blogPosts, result);
        verify(blogPostRepository, times(1)).getAllBlogPostsAsDto();
    }

    @Test
    void updateBlogPost() {
        BlogPostDto updatedPost = new BlogPostDto("Updated Title", "Updated Content");
        BlogPost blogPostEntity = mockBlogPost();
        int id = 1;
        setUpdateBlogPostConditions(id, blogPostEntity);
        ResponseEntity<String> response = blogPostService.updateBlogPost(updatedPost, id);
        verifyUpdateBlogPostExecution(response.getStatusCode(), id, blogPostEntity);
    }

    private void setUpdateBlogPostConditions(int id, BlogPost blogPostEntity) {
        when(blogPostRepository.findById(id)).thenReturn(Optional.of(blogPostEntity));
        when(persistenceService.update(blogPostEntity)).thenReturn(true);
    }

    private void verifyUpdateBlogPostExecution(HttpStatusCode code, int id, BlogPost blogPostEntity) {
        assertEquals(HttpStatus.OK, code);
        verify(blogPostRepository, times(1)).findById(id);
        verify(persistenceService, times(1)).update(blogPostEntity);
        verify(authService, times(1)).validateCurrentUsername(MOCKITO_USER);
    }

    @Test
    void removeBlogPost() {
        BlogPost post = new BlogPost();
        int postId = 1;
        setRemoveBlogPostConditions(postId, post);
        ResponseEntity<Void> response = blogPostService.removeBlogPost(postId);
        verifyRemoveBlogPostExecution(response.getStatusCode(), postId, post);
    }

    private void verifyRemoveBlogPostExecution(HttpStatusCode code, int postId, BlogPost post) {
        assertEquals(HttpStatus.OK, code);
        verify(blogPostRepository, times(1)).findById(postId);
        verify(persistenceService, times(1)).remove(post);
    }

    private void setRemoveBlogPostConditions(int id, BlogPost blogPostEntity) {
        when(blogPostRepository.findById(id)).thenReturn(Optional.of(blogPostEntity));
        when(persistenceService.remove(blogPostEntity)).thenReturn(true);
    }

    @Test
    void addTagToPost() {
        BlogPost post = mockBlogPost();
        int postId = 1;
        BlogTag tag = new BlogTag();
        String tagName = TAG_NAME;
        setModifyTagToPostConditions(postId, post, tagName, tag);
        ResponseEntity<Void> response = blogPostService.addTagToPost(postId, tagName);
        verifyAddTagToPostExecution(response.getStatusCode(), postId, tagName, post);
    }

    private void verifyAddTagToPostExecution(HttpStatusCode status, int postId, String tagName, BlogPost post) {
        assertEquals(HttpStatus.CREATED, status);
        verify(blogPostRepository, times(1)).findByIdWithTags(postId);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(persistenceService, times(1)).update(post);
        verify(authService, times(1)).validateCurrentUsername(MOCKITO_USER);
    }

    @Test
    void removeTagFromPost() {
        BlogPost post = new BlogPost();
        BlogTag tag = new BlogTag();
        int postId = 1;
        String tagName = TAG_NAME;
        setModifyTagToPostConditions(postId, post, tagName, tag);
        ResponseEntity<Void> response = blogPostService.removeTagFromPost(postId, tagName);
        verifyDeleteTagFromPostExecution(response.getStatusCode(), postId, tagName, post);
    }

    private void verifyDeleteTagFromPostExecution(HttpStatusCode status, int postId, String tagName, BlogPost post) {
        assertEquals(HttpStatus.OK, status);
        verify(blogPostRepository, times(1)).findByIdWithTags(postId);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(persistenceService, times(1)).update(post);
    }

    private void setModifyTagToPostConditions(int postId, BlogPost post, String tagName, BlogTag tag) {
        when(blogPostRepository.findByIdWithTags(postId)).thenReturn(Optional.of(post));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(persistenceService.update(post)).thenReturn(true);
    }

    @Test
    void getBlogPostsByTag() {
        Set<BlogPostDto> blogPosts = new HashSet<>();
        String tagName = TAG_NAME;
        when(blogPostRepository.findAllByTag(tagName)).thenReturn(blogPosts);
        Set<BlogPostDto> result = blogPostService.getBlogPostsByTag(tagName);
        assertEquals(blogPosts, result);
        verify(blogPostRepository, times(1)).findAllByTag(tagName);
    }

    private static BlogPost mockBlogPost() {
        BlogPost blogPostEntity = new BlogPost();
        blogPostEntity.setTitle("Old Title");
        blogPostEntity.setOwnerUsername(MOCKITO_USER);
        return blogPostEntity;
    }

}
