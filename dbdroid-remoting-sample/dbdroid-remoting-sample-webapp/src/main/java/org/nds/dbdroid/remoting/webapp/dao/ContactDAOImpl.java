package org.nds.dbdroid.remoting.webapp.dao;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.springframework.stereotype.Repository;

@Repository
public class ContactDAOImpl extends GenericHibernateDAO<Contact, Integer> implements ContactDAO {

}
