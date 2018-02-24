package org.udg.pds.todoandroid.entity;


public class UsuarioLoginPeticion {

    private String email;
    private String password;

    public UsuarioLoginPeticion(){

    }

    public UsuarioLoginPeticion(String email,String password){
        this.email = email;
        this.password = password;
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
}
