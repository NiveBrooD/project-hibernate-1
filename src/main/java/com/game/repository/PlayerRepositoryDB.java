package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    public static final String QUERY_GET_ALL = "SELECT * FROM rpg.player";
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        List<Player> players;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            NativeQuery<Player> nativeQuery = session.createNativeQuery(QUERY_GET_ALL, Player.class)
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize);
            players = nativeQuery.getResultList();
            session.getTransaction().commit();
        }
        return players;
    }

    @Override
    public int getAllCount() {
        int count;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<Player> namedQuery = session.createNamedQuery(Player.NAMED_GET_ALL, Player.class);
            count = namedQuery.getResultList().size();
            session.getTransaction().commit();
        }
        return count;
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(player);
            session.getTransaction().commit();
        }
        return player;
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(player);
            session.getTransaction().commit();
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Player player;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            player = session.get(Player.class, id);
            transaction.commit();
        }
        return Optional.ofNullable(player);
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(player);
            session.getTransaction().commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}