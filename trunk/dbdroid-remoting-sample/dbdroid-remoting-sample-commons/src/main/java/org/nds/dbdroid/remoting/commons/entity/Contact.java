package org.nds.dbdroid.remoting.commons.entity;

import java.io.Serializable;

import org.nds.dbdroid.annotation.Column;
import org.nds.dbdroid.annotation.Entity;
import org.nds.dbdroid.annotation.Id;

@Entity(name = "CONTACTS")
public class Contact implements Serializable {

    private static final long serialVersionUID = -3247556077199553090L;

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "FIRSTNAME")
    private String firstname;

    @Column(name = "LASTNAME")
    private String lastname;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TELEPHONE")
    private String telephone;

    // Default Constructor
    public Contact() {
    }

    public Contact(String firstname, String lastname, String email, String telephone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + this.getId() + "] " + this.getFirstname() + " " + this.getLastname() + " (" + this.getEmail() + ", " + this.getTelephone() + ")";
    }
}
