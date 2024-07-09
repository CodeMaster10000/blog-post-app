package com.scalefocus.mk.blog.api.blog;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for BlogPost entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for BlogPost entities.
 * It also defines custom queries for checking the existence of a blog post by title,
 * retrieving all blog posts as DTOs, finding a blog post by ID with its tags, and finding
 * all blog posts by a specific tag.
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    /**
     * Checks if a blog post with the specified title exists.
     *
     * @param title the title to check
     * @return true if a blog post with the title exists, false otherwise
     */
    boolean existsByTitle(String title);

    /**
     * Retrieves all blog posts as BlogPostDto objects with truncated text.
     *
     * @return a set of BlogPostDto objects
     */
    @Query("SELECT NEW com.scalefocus.mk.blog.api.blog.BlogPostDto(bp.title, substring(bp.text, 1, 50)) FROM BlogPost bp")
    Set<BlogPostDto> getAllBlogPostsAsDto();

    /**
     * Finds a blog post by its ID, including its tags.
     *
     * @param postId the ID of the blog post to find
     * @return an Optional containing the found BlogPost, or empty if not found
     */
    @EntityGraph(attributePaths = "tags")
    @Query("SELECT bp FROM BlogPost bp WHERE bp.id = ?1")
    Optional<BlogPost> findByIdWithTags(int postId);

    /**
     * Finds all blog posts associated with a specific tag.
     *
     * @param tagName the name of the tag
     * @return a set of BlogPostDto objects
     */
    @Query("SELECT NEW com.scalefocus.mk.blog.api.blog.BlogPostDto(bp.title, SUBSTRING(bp.text, 1, 50)) " +
            "FROM BlogPost bp JOIN bp.tags t WHERE t.name = ?1")
    Set<BlogPostDto> findAllByTag(String tagName);
}
