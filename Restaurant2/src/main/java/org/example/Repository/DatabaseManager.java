package org.example.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DatabaseManager {
    private static final EntityManagerFactory emf;

    static{
        try{
            emf = Persistence.createEntityManagerFactory("RestaurantPU");
        } catch (Throwable ex) {
            System.out.println("Initial EntityManagerFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    public static void close(){
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
