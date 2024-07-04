package com.scalefocus.mk.blog.api.blog;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    boolean existsByTitle(String title);

    @Query("SELECT NEW com.scalefocus.mk.blog.api.blog.BlogPostDto(bp.title, substring(bp.text, 1, 50)) FROM BlogPost bp")
    Set<BlogPostDto> getAllBlogPostsAsDto();

    @EntityGraph(attributePaths = "tags")
    @Query("SELECT bp FROM BlogPost bp WHERE bp.id = ?1")
    Optional<BlogPost> findByIdWithTags(int postId);

    @Query("SELECT NEW com.scalefocus.mk.blog.api.blog.BlogPostDto(bp.title, SUBSTRING(bp.text, 1, 50)) " +
            "FROM BlogPost bp JOIN bp.tags t WHERE t.name = ?1")
    Set<BlogPostDto> findAllByTag(String tagName);

}
