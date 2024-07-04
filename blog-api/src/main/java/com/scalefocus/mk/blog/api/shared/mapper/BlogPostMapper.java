package com.scalefocus.mk.blog.api.shared.mapper;

import com.scalefocus.mk.blog.api.shared.dto.BlogPostDto;
import com.scalefocus.mk.blog.api.shared.model.BlogPost;

public final class BlogPostMapper {

    private BlogPostMapper() {
        throw new IllegalStateException("Can not create instances of this utility class");
    }

    public static BlogPost createBlogPostDtoToEntity(BlogPostDto dto) {
        return blogPostDtoToEntity(dto);
    }

    public static BlogPost blogPostDtoToEntity(BlogPostDto dto) {
        if (dto == null) {
            return null;
        }

        BlogPost entity = new BlogPost();
        entity.setTitle(dto.title());
        entity.setText(dto.text());

        return entity;
    }

}
