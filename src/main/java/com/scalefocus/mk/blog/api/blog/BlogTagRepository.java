package com.scalefocus.mk.blog.api.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository interface for BlogTag entities.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for BlogTag entities.
 * It also defines custom queries for finding and deleting tags by name.
 * </p>
 */
@Repository
@Transactional(readOnly = true)
public interface BlogTagRepository extends JpaRepository<BlogTag, Integer> {

    /**
     * Finds a tag by its name.
     *
     * @param tagName the name of the tag to find
     * @return an Optional containing the found BlogTag, or empty if not found
     */
    Optional<BlogTag> findByName(String tagName);

    /**
     * Deletes a tag by its name.
     *
     * @param tagName the name of the tag to delete
     */
    void deleteByName(String tagName);
}
