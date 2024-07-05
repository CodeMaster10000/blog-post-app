package com.scalefocus.mk.blog.api.blog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalefocus.mk.blog.api.blog.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
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
    private TokenUtil tokenUtil;

    @BeforeEach
    void validateAndRefreshToken() {
        createBlogPostIfNotExists();
        createBlogTagIfNotExists();
    }

    @AfterEach
    void deleteBlogPostIfExists() {
        blogPostRepository.deleteById(BLOG_POST_ID);
        blogTagRepository.deleteByName(TAG_NAME);
    }

    @Test
    void createBlogPost() throws Exception {
        BlogPostDto blogPostDto = new BlogPostDto("Test Title", "Test Content");
        mockMvc.perform(post(POSTS_URL)
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getAllBlogPosts() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateBlogPost() throws Exception {
        BlogPostDto blogPostDto = new BlogPostDto("Updated Title", "Updated Content");
        mockMvc.perform(put(String.format(POSTS_URL + "/%d", BLOG_POST_ID))
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blogPostDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addTagToPost() throws Exception {
        mockMvc.perform(post(String.format(POSTS_TAGS_URL, BLOG_POST_ID, TAG_NAME))
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken()))
                .andExpect(status().isCreated());
    }

    @Test
    void removeTagFromPost() throws Exception {
        mockMvc.perform(delete(String.format(POSTS_TAGS_URL, BLOG_POST_ID, TAG_NAME))
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken()))
                .andExpect(status().isOk());
    }

    @Test
    void getBlogPostsByTag() throws Exception {
        mockMvc.perform(get(String.format(TAGS_URL, TAG_NAME))
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBlogPost() throws Exception {
        mockMvc.perform(delete(String.format(POSTS_URL + "/%d", BLOG_POST_ID))
                        .header("Authorization", "Bearer " + tokenUtil.getJwtToken()))
                .andExpect(status().isOk());
    }

    private void createBlogTagIfNotExists() {
        blogTagRepository.save(new BlogTag(null, TAG_NAME, new HashSet<>()));
    }

    private void createBlogPostIfNotExists() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Initial Title");
        blogPost.setText("Initial Content");
        blogPost.setTags(new HashSet<>());
        blogPost.setOwnerUsername(tokenUtil.getKeycloakUsername());
        blogPostRepository.save(blogPost);
        BLOG_POST_ID = blogPost.getId();
    }

}
