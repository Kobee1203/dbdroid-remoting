package org.nds.dbdroid.remoting.commons.dao;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.dao.AndroidDAO;
import org.nds.dbdroid.remoting.commons.entity.Contact;

public class ContactDAO extends AndroidDAO<Contact, Integer> implements IContactDAO {

    public ContactDAO(DataBaseManager dbManager) {
        super(dbManager);
    }

    public Contact save(Contact contact) {
        return saveOrUpdate(contact);
    }

    public Contact update(Contact contact) {
        return saveOrUpdate(contact);
    }

    public void deleteById(Integer id) {
        Contact contact = findById(id);
        if (null != contact) {
            delete(contact);
        }
    }
}
