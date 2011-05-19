package org.nds.dbdroid.remoting.commons.service;

import java.util.List;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;
import org.nds.dbdroid.service.IAndroidService;

@EndPoint("contactService")
public interface IContactService extends IAndroidService {

    List<Contact> listContact();

    Contact findById(Integer id);

    @EndPoint(httpMethod = { HttpMethod.POST, HttpMethod.PUT })
    Contact save(Contact c);

    @EndPoint(httpMethod = HttpMethod.POST)
    Contact update(Contact contact);

    @EndPoint(value = "deleteContact")
    void delete(Contact contact);

    @EndPoint(httpMethod = HttpMethod.DELETE)
    void delete(Integer id);
}
