package org.udg.pds.todoandroid.entity;


import org.udg.pds.todoandroid.service.ApiRest;

public class Pais {

    private Long id;

    private String pais;

    public Pais() {

    }

    public Pais(long id, String pais) {
        this.id = id;
        this.pais = pais;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

}
