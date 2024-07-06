package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.persistence.EntityMarker;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a tag for blog posts.
 * <p>
 * This class maps to the "tag" table in the database and contains details about
 * a tag, including its name and associated blog posts.
 * </p>
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public final class BlogTag implements EntityMarker {

    /**
     * Unique identifier for the tag.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    /**
     * Name of the tag.
     * <p>
     * This field is unique and cannot be null.
     * </p>
     */
    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Blog posts associated with the tag.
     * <p>
     * This field is a many-to-many relationship with the {@link BlogPost} entity.
     * </p>
     */
    @ManyToMany(mappedBy = "tags")
    private Set<BlogPost> blogPosts = new HashSet<>();
}
