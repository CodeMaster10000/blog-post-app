package com.scalefocus.mk.blog.api.blog;


import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between BlogPostDto and BlogPost entities.
 * <p>
 * This interface uses MapStruct to generate the implementation for mapping
 * BlogPostDto to BlogPost entities.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    /**
     * Converts a BlogPostDto to a BlogPost entity.
     *
     * @param dto the BlogPostDto to convert
     * @return the corresponding BlogPost entity
     */
    BlogPost blogPostDtoToEntity(BlogPostDto dto);
}