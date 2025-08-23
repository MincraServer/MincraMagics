package jp.mincra.mincramagics.db.dao;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.db.model.JobReward;
import jp.mincra.mincramagics.db.model.JobRewardId;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Data Access Object (DAO) for the JobReward entity.
 * Encapsulates all database operations (CRUD).
 */
public class JobRewardDao {

    private final SessionFactory sessionFactory;

    public JobRewardDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Executes a transactional operation that returns a value.
     * Handles session and transaction management.
     */
    private <T> T executeQuery(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            MincraLogger.error("Error executing database operation: " + e.getMessage(), e);
            // Depending on the use case, you might re-throw a custom exception
            return null;
        }
    }

    /**
     * Executes a transactional operation that does not return a value.
     * Handles session and transaction management.
     */
    private void executeUpdate(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            MincraLogger.error("Error executing database update operation: " + e.getMessage(), e);
        }
    }

    // --- CRUD Operations ---

    /**
     * SELECT: Finds a JobReward by its composite ID.
     *
     * @param id The composite primary key.
     * @return An Optional containing the JobReward if found, otherwise empty.
     */
    public Optional<JobReward> findById(JobRewardId id) {
        return Optional.ofNullable(executeQuery(session -> session.find(JobReward.class, id)));
    }

    /**
     * SELECT ALL: Retrieves all JobReward records from the database.
     *
     * @return A list of all JobRewards.
     */
    public List<JobReward> findAll() {
        return executeQuery(session ->
                session.createQuery("FROM JobReward", JobReward.class).list()
        );
    }



    /**
     * INSERT: Saves a new JobReward entity to the database.
     *
     * @param jobReward The entity to save.
     */
    public void save(JobReward jobReward) {
        executeUpdate(session -> session.persist(jobReward));
    }

    /**
     * UPDATE: Updates an existing JobReward entity in the database.
     *
     * @param jobReward The entity with updated values.
     */
    public void update(JobReward jobReward) {
        executeUpdate(session -> session.merge(jobReward));
    }

    /**
     * DELETE: Removes a JobReward entity from the database.
     *
     * @param jobReward The entity to delete.
     */
    public void delete(JobReward jobReward) {
        executeUpdate(session -> session.remove(jobReward));
    }
}
