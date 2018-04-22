package org.udg.pds.todoandroid.entity;


import java.io.Serializable;

public class Municipio implements Serializable{

    private Long id;


    private String municipio;

    private String slug;

    private double latitud;

    private double longitud;

    public Municipio() {

    }

    public Municipio(Long id, String municipio, String slug, double latitud, double longitud) {
        this.id = id;
        this.municipio = municipio;
        this.slug = slug;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
