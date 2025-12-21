package org.example.Repository;

import jakarta.persistence.*;
import org.example.Entity.Product;

import java.util.List;

public class ProductRepository {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("RestaurantPU");

    public void addProduct(Product p){
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
        em.close();
    }

    public List<Product> getAllProducts(){
        EntityManager em = emf.createEntityManager();
        List<Product> list = em.createQuery("select p from Product p", Product.class).getResultList();
        em.close();
        return list;
    }

    public void removeAll(){
        EntityManager em = emf.createEntityManager();
        em.createQuery("delete from Product p").executeUpdate();
        em.close();
    }
}
