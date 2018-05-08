package org.udg.pds.todoandroid.entity;

import java.util.Date;

public class EventoCrearPeticion {

    public String titulo;

    public String descripcion;

    public int duracion;

    public int numeroParticipantes;

    public String fechaEvento;

    public Boolean esPublico;

    public Long municipio;

    public Long deporte;

    public Ubicacion ubicacionGPS;

    public String tituloForo;

    public EventoCrearPeticion (){}

    public EventoCrearPeticion(String titulo, String descripcion, int duracion, int numeroParticipantes, String fechaEvento, Boolean esPublico, Long municipio, Long deporte, Ubicacion ubicacionGPS, String tituloForo) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.numeroParticipantes = numeroParticipantes;
        this.fechaEvento = fechaEvento;
        this.esPublico = esPublico;
        this.municipio = municipio;
        this.deporte = deporte;
        this.ubicacionGPS = ubicacionGPS;
        this.tituloForo = tituloForo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getNumeroParticipantes() {
        return numeroParticipantes;
    }

    public void setNumeroParticipantes(int numeroParticipantes) {
        this.numeroParticipantes = numeroParticipantes;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Boolean getEsPublico() {
        return esPublico;
    }

    public void setEsPublico(Boolean esPublico) {
        this.esPublico = esPublico;
    }

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }

    public Long getDeporte() {
        return deporte;
    }

    public void setDeporte(Long deporte) {
        this.deporte = deporte;
    }

    public Ubicacion getUbicacionGPS() {
        return ubicacionGPS;
    }

    public void setUbicacionGPS(Ubicacion ubicacionGPS) {
        this.ubicacionGPS = ubicacionGPS;
    }

    public String getTituloForo() {
        return tituloForo;
    }

    public void setTituloForo(String tituloForo) {
        this.tituloForo = tituloForo;
    }
}
