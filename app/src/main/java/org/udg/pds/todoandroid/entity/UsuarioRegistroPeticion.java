package org.udg.pds.todoandroid.entity;


import java.util.List;

public class UsuarioRegistroPeticion {

    private String nombre;

    private String apellidos;

    private String telefono;

    private String username;

    private String email;

    private String password;

    public String tokenFireBase;

    private Long municipio;

    private List<Long> deportesFavoritos;


    public UsuarioRegistroPeticion(){

    }

    public UsuarioRegistroPeticion(String nombre, String apellidos, String telefono, String username, String email, String password,String tokenFireBase, Long municipio, List<Long> deportesFavoritos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokenFireBase = tokenFireBase;
        this.municipio = municipio;
        this.deportesFavoritos = deportesFavoritos;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenFireBase() {
        return tokenFireBase;
    }

    public void setTokenFireBase(String tokenFireBase) {
        this.tokenFireBase = tokenFireBase;
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
