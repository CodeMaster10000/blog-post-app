package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.auth.AuthService;
import com.scalefocus.mk.blog.api.shared.exceptions.EntityPersistenceException;
import com.scalefocus.mk.blog.api.shared.persistence.PersistenceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    private final PersistenceService persistenceService;

    private final BlogTagRepository tagRepository;

    private final BlogPostMapper blogPostMapper;

    private final AuthService authService;

    BlogPostService(BlogPostRepository blogPostRepository, PersistenceService persistenceService, BlogTagRepository tagRepository, BlogPostMapper blogPostMapper, AuthService authService) {
        this.blogPostRepository = blogPostRepository;
        this.persistenceService = persistenceService;
        this.tagRepository = tagRepository;
        this.blogPostMapper = blogPostMapper;
        this.authService = authService;
    }

    ResponseEntity<String> createBlogPost(BlogPostDto blogPostDto) {
        if (blogPostRepository.existsByTitle(blogPostDto.title())) {
            return new ResponseEntity<>("Title already exists.", HttpStatus.CONFLICT);
        }

        BlogPost post = blogPostMapper.blogPostDtoToEntity(blogPostDto);
        post.setOwnerUsername(authService.getCurrentUsername());
        return persistenceService.persist(post) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    Set<BlogPostDto> getAllBlogPosts() {
        return blogPostRepository.getAllBlogPostsAsDto();
    }

    ResponseEntity<String> updateBlogPost(BlogPostDto updatedPost, int id) {
        BlogPost blogPostEntity = getBlogPostFromDatabase(id, EntityRelation.EMPTY);
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
    }

    ResponseEntity<Void> addTagToPost(int postId, String tagName) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.TAGS);
        BlogTag blogTag = getBlogTagEntity(tagName);
        post.getTags().add(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    ResponseEntity<Void> removeTagFromPost(int postId, String tagName) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.TAGS);
        BlogTag blogTag = getBlogTagEntity(tagName);
        post.getTags().remove(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Transactional
    public ResponseEntity<Void> removeBlogPost(int postId) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.EMPTY);
        return persistenceService.remove(post) ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private BlogPost getBlogPostEntity(int postId, EntityRelation relation) {
        Optional<BlogPost> blogPostOpt = relation.equals(EntityRelation.TAGS)
                ? blogPostRepository.findByIdWithTags(postId)
                : blogPostRepository.findById(postId);
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
            throw new EntityPersistenceException(
                    "Could not persist blog tag: " + tagName,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return tag;
    }

    private BlogPost getBlogPostFromDatabase(int postId, EntityRelation relation) {
        BlogPost post = getBlogPostEntity(postId, relation);
        authService.validateCurrentUsername(post.getOwnerUsername());
        return post;
    }

    Set<BlogPostDto> getBlogPostsByTag(String tagName) {
        return blogPostRepository.findAllByTag(tagName);
    }

}
