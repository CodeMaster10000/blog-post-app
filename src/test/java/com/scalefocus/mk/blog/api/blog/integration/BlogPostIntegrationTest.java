package com.scalefocus.mk.blog.api.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalefocus.mk.blog.api.blog.*;
import com.scalefocus.mk.blog.api.core.config.EnvironmentInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the BlogPost API.
 * <p>
 * This class contains integration tests for the BlogPost API endpoints, ensuring that
 * the endpoints function correctly with the actual application context.
 * </p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ContextConfiguration(initializers = EnvironmentInitializer.class, classes = TestContainersConfig.class)
class BlogPostIntegrationTest {

    private static int BLOG_POST_ID;
    private static final String TAG_NAME = "testing";
    private static final String POSTS_URL = "/api/v1/posts";
    private static final String POSTS_TAGS_URL = "/api/v1/posts/%d/tags/%s";
    private static final String TAGS_URL = "/api/v1/posts/tags/%s";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private BlogTagRepository blogTagRepository;

    @Autowired
    private KeycloakClient keycloakClient;

    /**
     * Sets up the test data before each test.
     * <p>
     * This method ensures that a blog post and a blog tag exist before each test is executed.
     * </p>
     */
    @BeforeEach
    void validateAndRefreshToken() {
        createBlogPostEntity();
        createBlogTagEntity();
    }

    /**
     * Cleans up the test data after each test.
     * <p>
     * This method deletes the blog post and the blog tag after each test is executed.
     * </p>
     */
    @AfterEach
    void deleteBlogPostIfExists() {
        blogPostRepository.deleteById(BLOG_POST_ID);
        blogTagRepository.deleteByName(TAG_NAME);
    }

    /**
     * Tests the creation of a blog post.
     * <p>
     * This test verifies that a new blog post can be created successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void createBlogPost() {
        BlogPostDto blogPostDto = new BlogPostDto("Test Title", "Test Content");
        mockMvc.perform(post(POSTS_URL)
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDto)))
                .andExpect(status().isCreated());
    }

    /**
     * Tests retrieving all blog posts.
     * <p>
     * This test verifies that all blog posts can be retrieved successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void getAllBlogPosts() {
        mockMvc.perform(get(POSTS_URL)
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Tests updating a blog post.
     * <p>
     * This test verifies that an existing blog post can be updated successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void updateBlogPost() {
        BlogPostDto blogPostDto = new BlogPostDto("Updated Title", "Updated Content");
        mockMvc.perform(put(String.format(POSTS_URL + "/%d", BLOG_POST_ID))
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDto)))
                .andExpect(status().isOk());
    }

    /**
     * Tests adding a tag to a blog post.
     * <p>
     * This test verifies that a tag can be added to a blog post successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void addTagToPost() {
        mockMvc.perform(post(String.format(POSTS_TAGS_URL, BLOG_POST_ID, TAG_NAME))
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken()))
                .andExpect(status().isCreated());
    }

    /**
     * Tests removing a tag from a blog post.
     * <p>
     * This test verifies that a tag can be removed from a blog post successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void removeTagFromPost() {
        mockMvc.perform(delete(String.format(POSTS_TAGS_URL, BLOG_POST_ID, TAG_NAME))
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken()))
                .andExpect(status().isOk());
    }

    /**
     * Tests retrieving blog posts by tag.
     * <p>
     * This test verifies that blog posts associated with a specific tag can be retrieved successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void getBlogPostsByTag() {
        mockMvc.perform(get(String.format(TAGS_URL, TAG_NAME))
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Tests deleting a blog post.
     * <p>
     * This test verifies that a blog post can be deleted successfully.
     * </p>
     */
    @Test
    @SneakyThrows
    void deleteBlogPost() {
        mockMvc.perform(delete(String.format(POSTS_URL + "/%d", BLOG_POST_ID))
                        .header("Authorization", "Bearer " + keycloakClient.getJwtToken()))
                .andExpect(status().isOk());
    }

    private void createBlogTagEntity() {
        blogTagRepository.save(new BlogTag(null, TAG_NAME, new HashSet<>()));
    }

    private void createBlogPostEntity() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Initial Title");
        blogPost.setText("Initial Content");
        blogPost.setTags(new HashSet<>());
        blogPost.setOwnerUsername(keycloakClient.getKeycloakUsername());
        blogPostRepository.save(blogPost);
        BLOG_POST_ID = blogPost.getId();
    }
}
