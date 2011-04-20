package org.nds.dbdroid.remoting.webapp.cxf;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.webapp.dao.ContactDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@WebService(endpointInterface = "org.nds.dbdroid.remoting.webapp.cxf.CXFContactService")
public class CXFContactServiceImpl implements CXFContactService {

    @Autowired
    private ContactDAO contactDAO;

    @Transactional
    public List<Contact> listContact() {
        return contactDAO.findAll();
    }

    @Transactional
    public Contact findById(Integer id) {
        Contact c = contactDAO.findById(id, false);
        return c;
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
    @WebMethod(operationName = "deleteById")
    public void delete(Integer id) {
        contactDAO.delete(id);
    }

    @Transactional
    @WebMethod(operationName = "deleteContact")
    public void delete(Contact contact) {
        contactDAO.makeTransient(contact);
    }
}
