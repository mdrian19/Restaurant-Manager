package org.example.Repository;

import jakarta.persistence.EntityManager;
import org.example.Entity.Order;
import org.example.Entity.User;
import java.util.List;

public class OrderRepository {
    public void save(Order order){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            if (order.getId() == null) em.persist(order);
            else em.merge(order);
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

    public List<Order> findAll(){
        try (EntityManager em = DatabaseManager.getEntityManager()){
            return em.createQuery("SELECT o FROM Order o JOIN FETCH o.user", Order.class)
                    .getResultList();
        }
    }

    public List<Order> findByUser(User user){
        try (EntityManager em = DatabaseManager.getEntityManager()){
            return em.createQuery("SELECT o FROM Order o JOIN FETCH o.user WHERE o.user = :user", Order.class)
                    .setParameter("user", user)
                    .getResultList();
        }
    }
}
