package org.udg.pds.todoandroid.entity;

public class Imagen {

    private Long id;

    private String ruta;

    public Imagen (){

    }

    public Imagen(Long id, String ruta) {
        this.id = id;
        this.ruta = ruta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
