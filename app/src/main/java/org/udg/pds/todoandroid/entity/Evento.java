package org.udg.pds.todoandroid.entity;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable {

    private Long id;

    private String titulo;

    private String descripcion;

    private int duracion;

    private int numeroParticipantes;

    private Date fechaEvento;

    private Deporte deporte;

    private Estado estado;

    private int participantesRegistrados;

    private Administrador administrador;

    private Municipio municipio;

    public Evento() {
    }

    public Evento(Long id, String titulo, String descripcion, int duracion, int numeroParticipantes, Date fechaEvento, Deporte deporte, Estado estado, int participantesRegistrados, Administrador administrador, Municipio municipio) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.numeroParticipantes = numeroParticipantes;
        this.fechaEvento = fechaEvento;
        this.deporte = deporte;
        this.estado = estado;
        this.participantesRegistrados = participantesRegistrados;
        this.administrador = administrador;
        this.municipio = municipio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getParticipantesRegistrados() {
        return participantesRegistrados;
    }

    public void setParticipantesRegistrados(int participantesRegistrados) {
        this.participantesRegistrados = participantesRegistrados;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }
}
