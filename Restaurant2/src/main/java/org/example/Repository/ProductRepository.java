package org.example.Repository;

import jakarta.persistence.EntityManager;
import org.example.Entity.Product;
import java.util.List;

public class ProductRepository {
    public List<Product> getAllProducts(){
        try (EntityManager em = DatabaseManager.getEntityManager()){
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        }
    }

    public void addProduct(Product p){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } catch (Exception e){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateProduct(Product p){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        } catch (Exception e){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void deleteProduct(Product p){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            Product managedProduct = em.find(Product.class, p.getId());
            if (managedProduct != null) {
                em.remove(managedProduct);
            }
            em.getTransaction().commit();
        } catch (Exception e){
            if (em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
