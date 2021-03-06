package org.udg.pds.todoandroid.entity;

import java.io.Serializable;

public class Deporte implements Serializable {

    private Long id;

    private String deporte;

    public Deporte (){

    }

    public Deporte(Long id, String deporte) {
        this.id = id;
        this.deporte = deporte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeporte() {
        return deporte;
    }

    public void setDeporte(String deporte) {
        this.deporte = deporte;
    }
}
