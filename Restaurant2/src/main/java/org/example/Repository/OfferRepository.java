package org.example.Repository;

import jakarta.persistence.EntityManager;
import org.example.Entity.Offer;

public class OfferRepository {
    public Offer findByName(String name) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            return em.find(Offer.class, name);
        } finally {
            em.close();
        }
    }

    public void save(Offer offer) {
        EntityManager em = DatabaseManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(offer);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
}
