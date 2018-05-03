package org.udg.pds.todoandroid.entity;

import java.io.Serializable;

public class ParticipanteEvento implements Serializable {

    private Long id;

    private String username;

    private String municipio;

    public ParticipanteEvento (){}

    public ParticipanteEvento(Long id, String username, String municipio) {
        this.id = id;
        this.username = username;
        this.municipio = municipio;
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

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}
