package sr.unasat.subscription.api.services;

import sr.unasat.subscription.api.entity.Prices;
import sr.unasat.subscription.api.entity.Subscription;
import jakarta.persistence.*;
import java.util.List;

public class SubscriptionService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("unasat");

    public Subscription save(Subscription subscription) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Prices price = subscription.getPrice();
            if (price != null && price.getId() == 0) {
                em.persist(price);
            }
            em.persist(subscription);
            tx.commit();
            return subscription;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public List<Subscription> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Subscription> result = em.createQuery("SELECT s FROM Subscription s", Subscription.class).getResultList();
            return result != null ? result : List.of();
        } finally {
            em.close();
        }
    }

    public Subscription findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Subscription.class, id);
        } finally {
            em.close();
        }
    }

    public Subscription update(Subscription subscription) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Prices price = subscription.getPrice();
            if (price != null && price.getId() == 0) {
                em.persist(price);
            }
            Subscription updated = em.merge(subscription);
            tx.commit();
            return updated;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void delete(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Subscription subscription = em.find(Subscription.class, id);
            if (subscription != null) {
                em.remove(subscription);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
