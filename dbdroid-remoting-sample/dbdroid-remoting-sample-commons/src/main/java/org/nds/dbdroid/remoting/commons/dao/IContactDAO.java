package org.nds.dbdroid.remoting.commons.dao;

import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.remoting.commons.entity.Contact;

public interface IContactDAO extends IAndroidDAO<Contact, Integer> {

    Contact save(Contact contact);

    Contact update(Contact contact);

    void deleteById(Integer id);
}
