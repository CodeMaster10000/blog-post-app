package com.scalefocus.mk.blog.api.blog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BlogTagRepository extends JpaRepository<BlogTag, Integer> {

    Optional<BlogTag> findByName(String tagName);

    void deleteByName(String tagName);
}
