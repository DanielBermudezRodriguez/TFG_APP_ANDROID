package org.udg.pds.todoandroid.entity;



public class Provincia {

    private Long id;

    private String provincia;

    public Provincia() {

    }

    public Provincia(Long id, String provincia) {
        this.id = id;
        this.provincia = provincia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
}
