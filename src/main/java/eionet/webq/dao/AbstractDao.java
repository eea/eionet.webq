package eionet.webq.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Common logic for DAO classes.
 *
 * @param <E> Entity
 */
@Transactional
public abstract class AbstractDao<E> {
    /**
     * Session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    protected Criteria getCriteria() {
        return getCurrentSession().createCriteria(getEntityClass());
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Finds entity by id.
     *
     * @param id id
     * @return entity
     */
    @SuppressWarnings("unchecked")
    public E findById(int id) {
        return (E) getCurrentSession().byId(getEntityClass()).load(id);
    }

    /**
     * Removes entity with its children.
     *
     * @param criterion criterion to search entities.
     */
    protected void removeByCriterion(Criterion criterion) {
        Session currentSession = getCurrentSession();
        List files = getCriteria().add(criterion).list();
        for (Object fileId : files) {
            currentSession.delete(fileId);
        }
    }

    /**
     * Return dto class.
     *
     * @return dto class
     */
    abstract Class<E> getEntityClass();
}
