package org.udg.pds.todoandroid.entity;


import java.io.Serializable;

public class Administrador implements Serializable {

    private Long id;

    private String username;

    private String email;

    public Administrador() {
    }

    public Administrador(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
