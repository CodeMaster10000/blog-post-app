package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.auth.AuthService;
import com.scalefocus.mk.blog.api.shared.persistence.PersistenceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link BlogPostService} class.
 * <p>
 * This class uses Mockito to mock dependencies and test the functionality of the BlogPostService class.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
final class BlogPostServiceTest {

    private static final String MOCKITO_USER = "Mockito";
    private static final String TAG_NAME = "Test tag";

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

    //Parameterized tests

    @Test
    void createBlogPost() {
        BlogPostDto blogPostDto = new BlogPostDto("Test Title", "Test Content");
        BlogPost blogPost = new BlogPost();

        when(blogPostRepository.existsByTitle(blogPostDto.title())).thenReturn(false);
        when(blogPostMapper.blogPostDtoToEntity(blogPostDto)).thenReturn(blogPost);
        when(persistenceService.persist(blogPost)).thenReturn(true);
        when(authService.getCurrentUsername()).thenReturn(MOCKITO_USER);

        ResponseEntity<String> response = blogPostService.createBlogPost(blogPostDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(blogPostRepository, times(1)).existsByTitle(blogPostDto.title());
        verify(persistenceService, times(1)).persist(blogPost);
        verify(authService, times(1)).getCurrentUsername();
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
        BlogPost blogPostEntity = new BlogPost();
        blogPostEntity.setTitle("Old Title");
        blogPostEntity.setOwnerUsername(MOCKITO_USER);
        int id = 1;

        when(blogPostRepository.findById(id)).thenReturn(Optional.of(blogPostEntity));
        when(persistenceService.update(blogPostEntity)).thenReturn(true);

        ResponseEntity<String> response = blogPostService.updateBlogPost(updatedPost, id);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(blogPostRepository, times(1)).findById(id);
        verify(persistenceService, times(1)).update(blogPostEntity);
        verify(authService, times(1)).validateCurrentUsername(MOCKITO_USER);
    }

    @Test
    void removeBlogPost() {
        BlogPost post = new BlogPost();
        int postId = 1;

        when(blogPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(persistenceService.remove(post)).thenReturn(true);

        ResponseEntity<Void> response = blogPostService.removeBlogPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(blogPostRepository, times(1)).findById(postId);
        verify(persistenceService, times(1)).remove(post);
    }

    @Test
    void addTagToPost() {
        BlogPost post = new BlogPost();
        post.setTitle("Old Title");
        post.setOwnerUsername(MOCKITO_USER);
        int postId = 1;
        BlogTag tag = new BlogTag();
        String tagName = TAG_NAME;

        when(blogPostRepository.findByIdWithTags(postId)).thenReturn(Optional.of(post));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(persistenceService.update(post)).thenReturn(true);

        ResponseEntity<Void> response = blogPostService.addTagToPost(postId, tagName);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
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

        when(blogPostRepository.findByIdWithTags(postId)).thenReturn(Optional.of(post));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(tag));
        when(persistenceService.update(post)).thenReturn(true);

        ResponseEntity<Void> response = blogPostService.removeTagFromPost(postId, tagName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(blogPostRepository, times(1)).findByIdWithTags(postId);
        verify(tagRepository, times(1)).findByName(tagName);
        verify(persistenceService, times(1)).update(post);
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

}
