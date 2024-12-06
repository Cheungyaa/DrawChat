package service;

import config.DatabaseConfig;
import entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class AuthService {
    public void register(String username, String password) {
        EntityManager em = DatabaseConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            em.persist(user);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public boolean login(String username, String password) {
        EntityManager em = DatabaseConfig.getEntityManager();
        try {
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
            return user != null;
        } catch (Exception e) {
            return false;
        } finally {
            em.close();
        }
    }
}
