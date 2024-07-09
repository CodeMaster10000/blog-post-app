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

/**
 * Service for managing blog posts.
 * <p>
 * This service provides methods for creating, retrieving, updating, and deleting blog posts.
 * It also allows for adding and removing tags from blog posts. The service interacts with
 * the repository layer to perform database operations and ensures proper authorization and
 * validation through the AuthService.
 * </p>
 */
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

    /**
     * Creates a new blog post.
     * <p>
     * This method checks if a blog post with the same title already exists. If it does,
     * it returns a conflict status. Otherwise, it maps the BlogPostDto to a BlogPost entity,
     * sets the owner username, and persists the entity in the database.
     * </p>
     *
     * @param blogPostDto the blog post data transfer object
     * @return the response entity with status indicating the result of the operation
     */
    ResponseEntity<String> createBlogPost(BlogPostDto blogPostDto) {
        if (blogPostRepository.existsByTitle(blogPostDto.title())) {
            return new ResponseEntity<>("Title already exists.", HttpStatus.CONFLICT);
        }

        BlogPost post = blogPostMapper.blogPostDtoToEntity(blogPostDto);
        post.setOwnerUsername(authService.getCurrentUsername());
        return persistenceService.persist(post) ? new ResponseEntity<>(HttpStatus.CREATED) : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Retrieves all blog posts.
     * <p>
     * This method fetches all blog posts from the database and returns them as a set of
     * BlogPostDto objects.
     * </p>
     *
     * @return a set of blog post data transfer objects
     */
    Set<BlogPostDto> getAllBlogPosts() {
        return blogPostRepository.getAllBlogPostsAsDto();
    }

    /**
     * Updates an existing blog post.
     * <p>
     * This method first fetches the blog post from the database. It then checks if the title
     * of the existing blog post is different from the updated title. If they are different,
     * it verifies that the new title does not already exist in the database. If the new title
     * is unique, the title is updated. Finally, the updated blog post entity is persisted
     * in the database.
     * </p>
     *
     * @param updatedPost the updated blog post data transfer object
     * @param id the id of the blog post to update
     * @return the response entity with status indicating the result of the operation
     */
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


    /**
     * Adds a tag to a blog post.
     * <p>
     * This method fetches the blog post and the tag from the database. If the tag does not exist,
     * it creates and persists a new tag. The tag is then added to the blog post, and the updated
     * blog post is persisted in the database.
     * </p>
     *
     * @param postId the id of the blog post
     * @param tagName the name of the tag to add
     * @return the response entity with status indicating the result of the operation
     */
    ResponseEntity<Void> addTagToPost(int postId, String tagName) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.TAGS);
        BlogTag blogTag = getBlogTagEntity(tagName);
        post.getTags().add(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Removes a tag from a blog post.
     * <p>
     * This method fetches the blog post and the tag from the database. The tag is then removed
     * from the blog post, and the updated blog post is persisted in the database.
     * </p>
     *
     * @param postId the id of the blog post
     * @param tagName the name of the tag to remove
     * @return the response entity with status indicating the result of the operation
     */
    ResponseEntity<Void> removeTagFromPost(int postId, String tagName) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.TAGS);
        BlogTag blogTag = getBlogTagEntity(tagName);
        post.getTags().remove(blogTag);
        return persistenceService.update(post) ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Removes a blog post.
     * <p>
     * This method fetches the blog post from the database and then removes it.
     * </p>
     *
     * @param postId the id of the blog post to remove
     * @return the response entity with status indicating the result of the operation
     */
    @Transactional
    public ResponseEntity<Void> removeBlogPost(int postId) {
        BlogPost post = getBlogPostFromDatabase(postId, EntityRelation.EMPTY);
        return persistenceService.remove(post) ?
                new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Retrieves blog posts by tag.
     * <p>
     * This method fetches all blog posts associated with the specified tag from the database
     * and returns them as a set of BlogPostDto objects.
     * </p>
     *
     * @param tagName the name of the tag
     * @return a set of blog post data transfer objects
     */
    Set<BlogPostDto> getBlogPostsByTag(String tagName) {
        return blogPostRepository.findAllByTag(tagName);
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

}
