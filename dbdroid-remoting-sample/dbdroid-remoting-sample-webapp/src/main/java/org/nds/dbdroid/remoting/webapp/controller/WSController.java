package org.nds.dbdroid.remoting.webapp.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.nds.dbdroid.remoting.controller.DBDroidRemotingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WSController extends DBDroidRemotingController {

    @Autowired
    private IContactService contactService;

    @RequestMapping("/dbdroid-remoting/index")
    public void getContacts(HttpServletRequest request, HttpServletResponse response) {
        String s = "";
        List<Contact> contacts = contactService.listContact();
        if (contacts != null) {
            for (Contact contact : contacts) {
                s += contact.toString() + "\n";
            }
        } else {
            s = "NO CONTACT";
        }
        try {
            ServletOutputStream out = response.getOutputStream();
            out.write(s.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@RequestMapping(value = { "/dbdroid-remoting/", "/dbdroid-remoting/{ws}/{method}" }, method = RequestMethod.POST)
    public void dbdroidRemoting(@PathVariable("ws") String ws, @PathVariable("method") String method, HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            String query = IOUtils.toString(request.getInputStream());
            String s = "query retrieved [" + ws + "." + method + "]:\n" + query;

            Object arguments = XStreamHelper.fromXML(query, null);
            Object[] args = arguments instanceof Object[] ? (Object[]) arguments : new Object[] { arguments };

            // Class<?> wsClass = Class.forName(ws);

            // Object service = serviceManager.getService(wsClass);
            Object o = MethodUtils.invokeMethod(contactService, method, args);
            String xml = XStreamHelper.toXML(o, null);

            byte[] bytes = xml.getBytes();
            response.setContentType("text/xml");
            response.setContentLength(bytes.length);
            out = response.getOutputStream();
            out.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
            }
        }
    }*/
}
