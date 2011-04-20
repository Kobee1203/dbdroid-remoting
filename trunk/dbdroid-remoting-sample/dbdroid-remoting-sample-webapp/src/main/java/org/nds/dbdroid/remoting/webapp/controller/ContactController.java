package org.nds.dbdroid.remoting.webapp.controller;

import java.util.Map;
import java.util.Random;

import org.nds.dbdroid.remoting.commons.entity.Contact;
import org.nds.dbdroid.remoting.commons.service.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ContactController {

    @Autowired
    private IContactService contactService;

    @RequestMapping("/index")
    public String listContacts(Map<String, Object> map) {
        map.put("contact", new Contact());
        map.put("contactList", contactService.listContact());

        return "contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addContact(@ModelAttribute("contact") Contact contact, BindingResult result) {

        contactService.save(contact);

        return "redirect:/index";
    }

    @RequestMapping("/update/{contactId}")
    public String updateContact(Map<String, Object> map, @PathVariable("contactId") Integer contactId) {

        Contact c = null;
        if (contactId != null) {
            c = contactService.findById(contactId);
        }
        if (c == null) {
            c = new Contact();
        }

        map.put("contact", c);

        return "update_contact";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@ModelAttribute("contact") Contact contact, BindingResult result) {

        contactService.update(contact);

        return "redirect:/index";
    }

    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") Integer contactId) {

        contactService.delete(contactId);

        return "redirect:/index";
    }

    @RequestMapping(value = "/update_first_contact")
    public String updateFirstContact() {
        Random random = new Random();
        int i = random.nextInt();
        Contact c = new Contact("First name " + i, "Last name " + i, "new email", "new tel");
        c.setId(1);
        contactService.update(c);

        return "redirect:/index";
    }
}
