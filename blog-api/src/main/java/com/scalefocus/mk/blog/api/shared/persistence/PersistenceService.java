package com.scalefocus.mk.blog.api.shared.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PersistenceService {

    @PersistenceContext
    EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(PersistenceService.class);

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