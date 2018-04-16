package org.udg.pds.todoandroid.entity;

import android.widget.ProgressBar;

import java.util.Date;
import java.util.List;

public class Usuario {

    private Long id;

    private String username;

    private String nombre;

    private String apellidos;

    private String email;

    private Date fechaRegistro;

    private Municipio municipio;

    private Provincia provincia;

    private Pais pais;

    private List<Deporte> deportesFavoritos;

    public Usuario() {
    }

    public Usuario(Long id, String username, String nombre, String apellidos, String email, Date fechaRegistro, Municipio municipio, Provincia provincia, Pais pais, List<Deporte> deportesFavoritos) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.fechaRegistro = fechaRegistro;
        this.municipio = municipio;
        this.provincia = provincia;
        this.pais = pais;
        this.deportesFavoritos = deportesFavoritos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public List<Deporte> getDeportesFavoritos() {
        return deportesFavoritos;
    }

    public void setDeportesFavoritos(List<Deporte> deportesFavoritos) {
        this.deportesFavoritos = deportesFavoritos;
    }
}
