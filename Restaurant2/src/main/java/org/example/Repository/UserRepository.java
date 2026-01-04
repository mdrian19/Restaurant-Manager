package org.example.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.example.Entity.User;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    public Optional<User> findByUsername(String username){
        try (EntityManager em = DatabaseManager.getEntityManager()){
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    public List<User> getAllUsers(){
        try (EntityManager em = DatabaseManager.getEntityManager()){
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        }
    }

    public void save(User user){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            if (user.getId() == null) em.persist(user);
            else em.merge(user);
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

    public void delete(User user){
        EntityManager em = DatabaseManager.getEntityManager();
        try{
            em.getTransaction().begin();
            User managedUser = em.find(User.class, user.getId());
            if (managedUser != null) {
                em.remove(managedUser);
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

    public User getUsersByRole(User.Role role) {
        try (EntityManager em = DatabaseManager.getEntityManager()){
            return em.createQuery("SELECT u FROM User u WHERE u.role = :role", User.class)
                    .setParameter("role", role)
                    .getSingleResult();
        }
    }
}
