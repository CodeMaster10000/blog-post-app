package com.scalefocus.mk.blog.api.blog;


final class BlogPostMapper {

    private BlogPostMapper() {
        throw new IllegalStateException("Can not create instances of this utility class");
    }

    static BlogPost createBlogPostDtoToEntity(BlogPostDto dto) {
        return blogPostDtoToEntity(dto);
    }

    static BlogPost blogPostDtoToEntity(BlogPostDto dto) {
        if (dto == null) {
            return null;
        }

        BlogPost entity = new BlogPost();
        entity.setTitle(dto.title());
        entity.setText(dto.text());

        return entity;
    }

}
