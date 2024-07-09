package com.scalefocus.mk.blog.api.blog;

/**
 * Data Transfer Object (DTO) for BlogPost.
 * <p>
 * This record represents a simple DTO for transferring blog post data,
 * including the title and text of the blog post.
 * </p>
 *
 * @param title the title of the blog post
 * @param text  the text content of the blog post
 */
public record BlogPostDto(String title, String text) {
}
