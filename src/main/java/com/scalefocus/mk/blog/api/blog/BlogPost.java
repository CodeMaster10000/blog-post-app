package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.persistence.EntityMarker;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a blog post.
 * <p>
 * This class maps to the "blog_post" table in the database and contains details about
 * a blog post, including its title, text, owner username, and associated tags.
 * </p>
 */
@Entity
@Table(name = "blog_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class BlogPost implements EntityMarker {

    /**
     * Unique identifier for the blog post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    /**
     * Title of the blog post.
     * <p>
     * This field is unique and cannot be null.
     * </p>
     */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String title;

    /**
     * Text content of the blog post.
     */
    @Column(columnDefinition = "TEXT")
    private String text;

    /**
     * Username of the owner of the blog post.
     */
    @ToString.Include
    @EqualsAndHashCode.Include
    private String ownerUsername;

    /**
     * Tags associated with the blog post.
     * <p>
     * This field is a many-to-many relationship with the {@link BlogTag} entity.
     * </p>
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "blog_post_tags",
            joinColumns = @JoinColumn(name = "blog_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<BlogTag> tags = new HashSet<>();
}
