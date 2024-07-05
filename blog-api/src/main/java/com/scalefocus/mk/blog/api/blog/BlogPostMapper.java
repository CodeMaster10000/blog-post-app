package com.scalefocus.mk.blog.api.blog;


import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface BlogPostMapper {

    BlogPost blogPostDtoToEntity(BlogPostDto dto);

}