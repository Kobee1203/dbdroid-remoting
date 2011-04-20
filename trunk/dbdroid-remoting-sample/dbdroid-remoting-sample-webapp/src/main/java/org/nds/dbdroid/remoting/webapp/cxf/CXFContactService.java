package org.nds.dbdroid.remoting.webapp.cxf;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.nds.dbdroid.remoting.commons.entity.Contact;

@WebService
public interface CXFContactService {

    List<Contact> listContact();

    Contact findById(Integer id);

    Contact save(Contact c);

    Contact update(Contact contact);

    @WebMethod(operationName = "deleteById")
    void delete(Integer id);

    @WebMethod(operationName = "deleteContact")
    void delete(Contact contact);
}
