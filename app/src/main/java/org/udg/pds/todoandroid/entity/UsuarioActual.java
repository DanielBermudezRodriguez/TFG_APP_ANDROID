package org.udg.pds.todoandroid.entity;


import java.util.List;

public class UsuarioActual {

    private static UsuarioActual mInstancia = null;

    private Long id;

    private String username;

    private String mail;



    private UsuarioActual(){
        id = -1L;
        username = "";
        mail = "";
    }

    public static UsuarioActual getInstance(){
        if(mInstancia == null)
        {
            mInstancia = new UsuarioActual();
        }
        return mInstancia;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
