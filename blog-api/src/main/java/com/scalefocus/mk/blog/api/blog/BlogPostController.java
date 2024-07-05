package com.scalefocus.mk.blog.api.blog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/posts")
public final class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    public ResponseEntity<String> createBlogPost(@RequestBody BlogPostDto blogPost) {
        return blogPostService.createBlogPost(blogPost);
    }

    @GetMapping
    public ResponseEntity<Set<BlogPostDto>> getAllBlogPosts() {
        Set<BlogPostDto> blogPosts = blogPostService.getAllBlogPosts();
        return new ResponseEntity<>(blogPosts, HttpStatus.OK);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<String> updateBlogPost(
            @PathVariable int postId,
            @RequestBody BlogPostDto updatedPost) {
        return blogPostService.updateBlogPost(updatedPost, postId);
    }

    @PostMapping("/{postId}/tags/{tagName}")
    public ResponseEntity<Void> addTagToPost(
            @PathVariable int postId,
            @PathVariable String tagName) {
        return blogPostService.addTagToPost(postId, tagName);
    }

    @DeleteMapping("/{postId}/tags/{tagName}")
    public ResponseEntity<Void> removeTagFromPost(
            @PathVariable int postId,
            @PathVariable String tagName) {
        return blogPostService.removeTagFromPost(postId, tagName);
    }

    @GetMapping("/tags/{tagName}")
    public ResponseEntity<Set<BlogPostDto>> getBlogPostsByTag(
            @PathVariable String tagName) {
        Set<BlogPostDto> blogPosts = blogPostService.getBlogPostsByTag(tagName);
        return new ResponseEntity<>(blogPosts, HttpStatus.OK);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable int postId) {
        return blogPostService.removeBlogPost(postId);
    }

}