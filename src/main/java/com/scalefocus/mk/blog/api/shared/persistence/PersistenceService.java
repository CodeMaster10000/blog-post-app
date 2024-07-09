package com.scalefocus.mk.blog.api.shared.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling entity persistence operations.
 * <p>
 * This service provides methods to persist, update, and remove entities in the database.
 * It uses the EntityManager to interact with the persistence context and handles
 * transactions for these operations.
 * </p>
 */
@Service
public class PersistenceService {

    @PersistenceContext
    EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(PersistenceService.class);

    /**
     * Persists a given entity in the database.
     * <p>
     * This method attempts to persist the provided entity using the EntityManager.
     * If the operation is successful, it logs the action and returns true. Otherwise,
     * it logs the error and returns false.
     * </p>
     *
     * @param entity the entity to be persisted
     * @param <T>    the type of the entity, which must implement EntityMarker
     * @return true if the entity was successfully persisted, false otherwise
     */
    @Transactional
    public <T extends EntityMarker> boolean persist(T entity) {
        try {
            em.persist(entity);
            logger.info("Persisted entity: {}", entity);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Updates a given entity in the database.
     * <p>
     * This method attempts to merge the provided entity using the EntityManager.
     * If the operation is successful, it logs the action and returns true. Otherwise,
     * it logs the error and returns false.
     * </p>
     *
     * @param entity the entity to be updated
     * @param <T>    the type of the entity, which must implement EntityMarker
     * @return true if the entity was successfully updated, false otherwise
     */
    @Transactional
    public <T extends EntityMarker> boolean update(T entity) {
        try {
            em.merge(entity);
            logger.info("Merged entity: {}", entity);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Removes a given entity from the database.
     * <p>
     * This method attempts to remove the provided entity using the EntityManager.
     * If the operation is successful, it logs the action and returns true. Otherwise,
     * it logs the error and returns false.
     * </p>
     *
     * @param entity the entity to be removed
     * @param <T>    the type of the entity, which must implement EntityMarker
     * @return true if the entity was successfully removed, false otherwise
     */
    @Transactional
    public <T extends EntityMarker> boolean remove(T entity) {
        try {
            em.remove(entity);
            logger.info("Removed entity: {}", entity);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
