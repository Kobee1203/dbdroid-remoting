package org.nds.dbdroid.remoting.webapp.service;

import java.util.List;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.webapp.dao.ContactDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@ServiceProvider(service = IContactService.class)
public class ContactServiceImpl implements IContactService {

    @Autowired
    private ContactDAO contactDAO;

    @Transactional
    public List<Contact> listContact() {
        return contactDAO.findAll();
    }

    @Transactional
    public Contact findById(Integer id) {
        return contactDAO.findById(id, false);
    }

    @Transactional
    public Contact save(Contact contact) {
        return contactDAO.makePersistent(contact);
    }

    @Transactional
    public Contact update(Contact contact) {
        return contactDAO.makePersistent(contact);
    }

    @Transactional
    public void delete(Integer id) {
        contactDAO.delete(id);
    }

    @Transactional
    public void delete(Contact contact) {
        contactDAO.makeTransient(contact);
    }
}
