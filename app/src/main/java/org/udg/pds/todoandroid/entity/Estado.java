package org.udg.pds.todoandroid.entity;

import java.io.Serializable;

public class Estado implements Serializable {

    private Long id;

    private String estado;

    public Estado() {
    }

    public Estado(Long id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
