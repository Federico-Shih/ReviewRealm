package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.models.MissionProgress;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.keys.MissionProgressId;
import ar.edu.itba.paw.persistenceinterfaces.MissionDao;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MissionHibernateDao implements MissionDao {
    @PersistenceContext
    private EntityManager em;

    @Override
    public MissionProgress create(User user, Mission mission, Float progress, LocalDate date) {
        MissionProgress missionProgress = new MissionProgress(user, mission, progress, date, 0);
        em.persist(missionProgress);
        em.flush();
        return missionProgress;
    }

    @Override
    public Optional<MissionProgress> findById(User user, Mission mission) {
        return Optional.ofNullable(em.find(MissionProgress.class, new MissionProgressId(user, mission)));
    }

    @Override
    public MissionProgress updateProgress(User user, Mission mission, Float progress) {
        MissionProgress missionProgress = em.find(MissionProgress.class, new MissionProgressId(user, mission));
        if (missionProgress == null) return null;
        missionProgress.setProgress(progress);
        return missionProgress;
    }

    @Override
    public MissionProgress resetProgress(User user, Mission mission) {
        MissionProgress missionProgress = em.find(MissionProgress.class, new MissionProgressId(user, mission));
        if (missionProgress == null) return null;
        missionProgress.setProgress(0f);
        missionProgress.setStartDate(LocalDate.now());
        return missionProgress;
    }

    @Override
    public MissionProgress completeMission(User user, Mission mission) {
        MissionProgress missionProgress = em.find(MissionProgress.class, new MissionProgressId(user, mission));
        if (missionProgress == null) return null;
        missionProgress.setTimes(missionProgress.getTimes() + 1);
        return missionProgress;
    }

    @Override
    public List<MissionProgress> findAll() {
        TypedQuery<MissionProgress> query = em.createQuery("from MissionProgress", MissionProgress.class);
        return query.getResultList();
    }

}
