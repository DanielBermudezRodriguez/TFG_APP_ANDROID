package org.udg.pds.todoandroid.entity;


import java.io.Serializable;

public class Municipio implements Serializable{

    private Long id;


    private String municipio;

    private String slug;

    private double latitudEstimada;

    private double longitudEstimada;

    public Municipio() {

    }

    public Municipio(Long id, String municipio, String slug, double latitud, double longitud) {
        this.id = id;
        this.municipio = municipio;
        this.slug = slug;
        this.latitudEstimada = latitud;
        this.longitudEstimada = longitud;
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

    public double getLatitudEstimada() {
        return latitudEstimada;
    }

    public void setLatitudEstimada(double latitudEstimada) {
        this.latitudEstimada = latitudEstimada;
    }

    public double getLongitudEstimada() {
        return longitudEstimada;
    }

    public void setLongitudEstimada(double longitudEstimada) {
        this.longitudEstimada = longitudEstimada;
    }
}
