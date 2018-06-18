package org.udg.pds.todoandroid.entity;


import java.io.Serializable;

public class Pais implements Serializable {

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
