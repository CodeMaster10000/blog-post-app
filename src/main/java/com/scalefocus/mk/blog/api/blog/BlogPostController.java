package com.scalefocus.mk.blog.api.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;

/**
 * REST controller for managing blog posts.
 * <p>
 * This controller provides endpoints for creating, retrieving, updating, and deleting blog posts,
 * as well as for adding and removing tags from blog posts.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/posts")
public final class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    /**
     * Creates a new blog post.
     *
     * @param blogPost the blog post data transfer object
     * @return the response entity with status indicating the result of the operation
     */
    @PostMapping
    public ResponseEntity<String> createBlogPost(@RequestBody BlogPostDto blogPost) {
        return blogPostService.createBlogPost(blogPost);
    }

    /**
     * Retrieves all blog posts.
     *
     * @return a response entity containing a set of blog post data transfer objects
     */
    @GetMapping
    public ResponseEntity<Set<BlogPostDto>> getAllBlogPosts() {
        Set<BlogPostDto> blogPosts = blogPostService.getAllBlogPosts();
        return new ResponseEntity<>(blogPosts, HttpStatus.OK);
    }

    /**
     * Updates an existing blog post.
     *
     * @param postId the ID of the blog post to update
     * @param updatedPost the updated blog post data transfer object
     * @return the response entity with status indicating the result of the operation
     */
    @PutMapping("/{postId}")
    public ResponseEntity<String> updateBlogPost(
            @PathVariable int postId,
            @RequestBody BlogPostDto updatedPost) {
        return blogPostService.updateBlogPost(updatedPost, postId);
    }

    /**
     * Adds a tag to a blog post.
     *
     * @param postId the ID of the blog post
     * @param tagName the name of the tag to add
     * @return the response entity with status indicating the result of the operation
     */
    @PostMapping("/{postId}/tags/{tagName}")
    public ResponseEntity<Void> addTagToPost(
            @PathVariable int postId,
            @PathVariable String tagName) {
        return blogPostService.addTagToPost(postId, tagName);
    }

    /**
     * Removes a tag from a blog post.
     *
     * @param postId the ID of the blog post
     * @param tagName the name of the tag to remove
     * @return the response entity with status indicating the result of the operation
     */
    @DeleteMapping("/{postId}/tags/{tagName}")
    public ResponseEntity<Void> removeTagFromPost(
            @PathVariable int postId,
            @PathVariable String tagName) {
        return blogPostService.removeTagFromPost(postId, tagName);
    }

    /**
     * Retrieves blog posts by tag.
     *
     * @param tagName the name of the tag
     * @return a response entity containing a set of blog post data transfer objects
     */
    @GetMapping("/tags/{tagName}")
    public ResponseEntity<Set<BlogPostDto>> getBlogPostsByTag(
            @PathVariable String tagName) {
        Set<BlogPostDto> blogPosts = blogPostService.getBlogPostsByTag(tagName);
        return new ResponseEntity<>(blogPosts, HttpStatus.OK);
    }

    /**
     * Deletes a blog post.
     *
     * @param postId the ID of the blog post to delete
     * @return the response entity with status indicating the result of the operation
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable int postId) {
        return blogPostService.removeBlogPost(postId);
    }
}
