package sr.unasat.subscription.api.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import sr.unasat.subscription.api.entity.Prices;

import java.util.List;

public class PricesService {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("unasat");

    public List<Prices> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Prices p", Prices.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Prices findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Prices.class, id);
        } finally {
            em.close();
        }
    }

    public Prices save(Prices price) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(price);
            tx.commit();
            return price;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Prices update(Prices price) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Prices updated = em.merge(price);
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
            Prices price = em.find(Prices.class, id);
            if (price != null) {
                em.remove(price);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }
}
