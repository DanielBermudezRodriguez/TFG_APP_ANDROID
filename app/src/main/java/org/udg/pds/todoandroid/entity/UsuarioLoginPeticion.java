package org.udg.pds.todoandroid.entity;


public class UsuarioLoginPeticion {

    private String email;
    private String password;
    private String tokenFireBase;

    public UsuarioLoginPeticion(){

    }

    public UsuarioLoginPeticion(String email,String password, String tokenFireBase){
        this.email = email;
        this.password = password;
        this.tokenFireBase = tokenFireBase;
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
}
