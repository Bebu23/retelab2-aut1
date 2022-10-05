package hu.bme.aut.retelab2.repository;

import hu.bme.aut.retelab2.SecretGenerator.SecretGenerator;
import hu.bme.aut.retelab2.domain.Ad;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class AdRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Ad save(Ad ad) {
        ad.setSecret(SecretGenerator.generate());
        return em.merge(ad);
    }

    public List<Ad> searchByPrice(int min, int max) {
        List<Ad> result = em.createQuery("select a from Ad a where a.price between ?1 and ?2", Ad.class)
                .setParameter(1, min)
                .setParameter(2, max)
                .getResultList();
        for (Ad ad : result) {
            ad.setSecret(null);
        }
        return result;
    }

    @Transactional
    public Ad updateAd(Ad updated) {
        Ad found = em.find(Ad.class, updated.getId());
        if (!updated.getSecret().equals(found.getSecret())) {
            return null;
        }
        save(updated);
        return updated;
    }

    public List<Ad> findByTag(String tag) {
        List<Ad> result = em.createQuery("select a from Ad a where ?1 member a.tags", Ad.class)
                .setParameter(1, tag)
                .getResultList();
        for (Ad ad : result) {
            ad.setSecret(null);
        }
        return result;
    }

    @Scheduled(fixedDelay = 6000)
    @Transactional
    public void deleteOldEntries() {
        List<Ad> forDeletion = em.createQuery("select a from Ad a where a.expiryDate < ?1", Ad.class)
                .setParameter(1, LocalDateTime.now())
                .getResultList();
        for (Ad ad : forDeletion) {
            em.remove(ad);
        }
    }
}
