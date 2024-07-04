package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.persistence.EntityMarker;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
final class BlogPost implements EntityMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ToString.Include
    @EqualsAndHashCode.Include
    private String ownerUsername;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "blog_post_tags",
            joinColumns = @JoinColumn(name = "blog_post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<BlogTag> tags = new HashSet<>();

}