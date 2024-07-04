package com.scalefocus.mk.blog.api.blog;

import com.scalefocus.mk.blog.api.shared.persistence.EntityMarker;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
final class BlogTag implements EntityMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Integer id;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<BlogPost> blogPosts = new HashSet<>();

}