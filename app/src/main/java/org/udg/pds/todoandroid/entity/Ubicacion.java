package org.udg.pds.todoandroid.entity;


import java.io.Serializable;

public class Ubicacion implements Serializable {

    private double latitud;

    private double longitud;

    private String direccion;

    private String municipio;

    public Ubicacion() {

    }

    public Ubicacion(double latitud, double longitud, String direccion, String municipio) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.direccion = direccion;
        this.municipio = municipio;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}
