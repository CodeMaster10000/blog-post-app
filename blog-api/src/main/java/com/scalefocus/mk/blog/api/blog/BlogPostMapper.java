package com.scalefocus.mk.blog.api.blog;


import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    BlogPost blogPostDtoToEntity(BlogPostDto dto);

}