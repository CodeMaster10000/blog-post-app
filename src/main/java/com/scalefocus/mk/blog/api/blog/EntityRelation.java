package com.scalefocus.mk.blog.api.blog;

/**
 * Enum representing the type of relationship to be loaded for an entity.
 * <p>
 * This enum is used to specify which related entities should be loaded
 * when retrieving a BlogPost entity from the database.
 * </p>
 */
public enum EntityRelation {

    /**
     * Load the tags related to the BlogPost entity.
     */
    TAGS,

    /**
     * Do not load any related entities.
     */
    EMPTY
}
