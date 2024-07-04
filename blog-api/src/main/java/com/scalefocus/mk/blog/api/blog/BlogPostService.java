package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.dto.BlogPostDto;
import com.scalefocus.mk.blog.api.shared.mapper.BlogPostMapper;
import com.scalefocus.mk.blog.api.shared.model.BlogPost;
import com.scalefocus.mk.blog.api.shared.model.BlogTag;
import com.scalefocus.mk.blog.api.shared.persistence.PersistenceService;
import com.scalefocus.mk.blog.api.shared.exceptions.EntityPersistenceException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
final class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    private final PersistenceService persistenceService;

    private final BlogTagRepository tagRepository;

    BlogPostService(BlogPostRepository blogPostRepository, PersistenceService persistenceService, BlogTagRepository tagRepository) {
        this.blogPostRepository = blogPostRepository;
        this.persistenceService = persistenceService;
        this.tagRepository = tagRepository;
    }

    ResponseEntity<String> createBlogPost(BlogPostDto blogPostDto) {
        if (blogPostRepository.existsByTitle(blogPostDto.title())) {
            return new ResponseEntity<>("Title already exists.", HttpStatus.CONFLICT);
        }
        BlogPost post = BlogPostMapper.createBlogPostDtoToEntity(blogPostDto);
        return persistenceService.persist(post) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Set<BlogPostDto> getAllBlogPosts() {
        return blogPostRepository.getAllBlogPostsAsDto();
    }

    ResponseEntity<String> updateBlogPost(BlogPostDto updatedPost, int id) {
        Optional<BlogPost> blogPostOpt = blogPostRepository.findById(id);
        if (blogPostOpt.isPresent()) {
            BlogPost blogPostEntity = blogPostOpt.get();
            if (!blogPostEntity.getTitle().equals(updatedPost.title())) {
                if (blogPostRepository.existsByTitle(updatedPost.title())) {
                    return new ResponseEntity<>("Title already exists.", HttpStatus.CONFLICT);
                } else {
                    blogPostEntity.setTitle(updatedPost.title());
                }
            }
            return persistenceService.update(blogPostEntity) ?
                    new ResponseEntity<>(HttpStatus.OK) :
                    new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            throw new EntityNotFoundException("Could not find entity with id: " + id);
        }
    }

    ResponseEntity<Void> addTagToPost(int postId, String tagName) {
        BlogTag blogTag = getBlogTagEntity(tagName);
        BlogPost post = getBlogPostEntity(postId);
        post.getTags().add(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private BlogPost getBlogPostEntity(int postId) {
        Optional<BlogPost> blogPostOpt = blogPostRepository.findByIdWithTags(postId);
        if (blogPostOpt.isPresent()) {
            return blogPostOpt.get();
        } else {
            throw new EntityNotFoundException("Could not find entity with id: " + postId);
        }
    }

    private BlogTag getBlogTagEntity(String tagName) {
        Optional<BlogTag> tagOpt = tagRepository.findByName(tagName);
        return tagOpt.orElseGet(() -> persistBlogTag(tagName));
    }

    private BlogTag persistBlogTag(String tagName) {
        BlogTag tag = new BlogTag();
        tag.setBlogPosts(new HashSet<>());
        tag.setName(tagName);
        boolean persisted = persistenceService.persist(tag);
        if (!persisted) {
            throw new EntityPersistenceException("Could not persist blog tag: " + tagName);
        }
        return tag;
    }

    ResponseEntity<Void> removeTagFromPost(int postId, String tagName) {
        BlogTag blogTag = getBlogTagEntity(tagName);
        BlogPost post = getBlogPostEntity(postId);
        post.getTags().remove(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Set<BlogPostDto> getBlogPostsByTag(String tagName) {
        return blogPostRepository.findAllByTag(tagName);
    }

}
