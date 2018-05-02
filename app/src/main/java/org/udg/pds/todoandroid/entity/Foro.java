package org.udg.pds.todoandroid.entity;

import java.io.Serializable;

public class Foro implements Serializable {

    private Long id;

    private Boolean esPublico;

    private String titulo;

    public Foro (){

    }

    public Foro(Long id, Boolean esPublico, String titulo) {
        this.id = id;
        this.esPublico = esPublico;
        this.titulo = titulo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEsPublico() {
        return esPublico;
    }

    public void setEsPublico(Boolean esPublico) {
        this.esPublico = esPublico;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
