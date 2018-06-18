package org.udg.pds.todoandroid.entity;

import java.util.List;

public class UsuarioModificarPerfil {

    private String username;

    private String nombre;

    private String apellidos;

    private Long municipio;

    private List<Long> deportesFavoritos;

    public UsuarioModificarPerfil() {
    }

    public UsuarioModificarPerfil(String username, String nombre, String apellidos, Long municipio, List<Long> deportesFavoritos) {
        this.username = username;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.municipio = municipio;
        this.deportesFavoritos = deportesFavoritos;
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

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }

    public List<Long> getDeportesFavoritos() {
        return deportesFavoritos;
    }

    public void setDeportesFavoritos(List<Long> deportesFavoritos) {
        this.deportesFavoritos = deportesFavoritos;
    }
}
