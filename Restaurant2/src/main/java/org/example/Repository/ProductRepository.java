package org.example.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.example.Entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private volatile EntityManagerFactory emf;

    private EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (this) {
                if (emf == null) {
                    try {
                        emf = Persistence.createEntityManagerFactory("RestaurantPU");
                    } catch (PersistenceException ex) {
                        throw new IllegalStateException(
                                "Failed to create EntityManagerFactory. Check entity mappings and persistence.xml", ex);
                    }
                }
            }
        }
        return emf;
    }

    public EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public void removeAll() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Product").executeUpdate();
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Product> getAllProducts() {
        EntityManager em = getEntityManager();
        List<Product> products = new ArrayList<>();
        try {
            products = em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }
        catch(Exception e) {
            System.out.println("Error reading config file (invalid or corrupted). Using default values.");
        } finally {
            em.close();
        }
        return products;
    }

    public void addProduct(Product p) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
