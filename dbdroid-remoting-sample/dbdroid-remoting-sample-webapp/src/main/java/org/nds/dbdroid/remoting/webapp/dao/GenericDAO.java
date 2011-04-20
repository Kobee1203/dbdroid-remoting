package org.nds.dbdroid.remoting.webapp.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T, ID extends Serializable> {

    T findById(ID id, boolean lock);

    List<T> findAll();

    List<T> findByExample(T exampleInstance, String... excludeProperty);

    T makePersistent(T entity); // create

    void makeTransient(T entity); // delete

    void delete(ID id);
}
