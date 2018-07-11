package org.udg.pds.todoandroid.entity;


public class MensajeForo {

    private String text; // Contenido del mensaje
    private DatosUsuarioForo data; // datos usuario foro
    private boolean belongsToCurrentUser; // es usuario actual propietario mensaje?

    public MensajeForo(String text, DatosUsuarioForo data, boolean belongsToCurrentUser) {
        this.text = text;
        this.data = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public DatosUsuarioForo getData() {
        return data;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

}
