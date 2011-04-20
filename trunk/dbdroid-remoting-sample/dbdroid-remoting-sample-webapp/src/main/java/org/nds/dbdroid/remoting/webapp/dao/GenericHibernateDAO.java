package org.nds.dbdroid.remoting.webapp.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class GenericHibernateDAO<T, ID extends Serializable> extends HibernateDaoSupport implements GenericDAO<T, ID> {

    private final Class<T> persistentClass;

    //private Session session;

    @SuppressWarnings("unchecked")
    public GenericHibernateDAO() {
        Class<?> clazz = getClass();
        while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
            clazz = clazz.getSuperclass();
        }

        this.persistentClass = (Class<T>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Autowired
    public void init(SessionFactory factory) {
        setSessionFactory(factory);
    }

    /*public void setSession(Session s) {
    	this.session = s;
    }*/

    /*protected Session getSession() {
    	if (session == null) {
    		throw new IllegalStateException("Session has not been set on DAO before usage");
    	}
    	return session;
    }*/

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    @SuppressWarnings("unchecked")
    public T findById(ID id, boolean lock) {
        T entity;
        if (lock) {
            entity = (T) getSession().load(getPersistentClass(), id, LockMode.UPGRADE);
        } else {
            entity = (T) getSession().load(getPersistentClass(), id);
        }

        return entity;
    }

    public List<T> findAll() {
        return findByCriteria();
    }

    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    public T makePersistent(T entity) {
        getSession().saveOrUpdate(entity);
        return entity;
    }

    public void makeTransient(T entity) {
        /*List<T> list = findByExample(entity);
        if (null != list && !list.isEmpty()) {
            getSession().delete(list.get(0));
        }*/
        getSession().delete(entity);
    }

    public void delete(ID id) {
        T entity = findById(id, false);
        if (null != entity) {
            makeTransient(entity);
        }
    }

    public void flush() {
        getSession().flush();
    }

    public void clear() {
        getSession().clear();
    }

    /**
    * 	Use this inside subclasses as a convenience method.
    */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

}
