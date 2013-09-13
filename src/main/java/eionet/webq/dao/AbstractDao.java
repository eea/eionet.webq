package eionet.webq.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Common logic for DAO classes.
 *
 * @param <T> DTO
 */
@Transactional
public abstract class AbstractDao<T> {
    /**
     * Session factory.
     */
    @Autowired
    private SessionFactory sessionFactory;

    protected Criteria getCriteria() {
        return getCurrentSession().createCriteria(getDtoClass());
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Return dto class.
     *
     * @return dto class
     */
    abstract Class<T> getDtoClass();
}
