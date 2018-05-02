package org.udg.pds.todoandroid.entity;


public class MensajeForo {

    private String text; // message body
    private DatosUsuarioForo data; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

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
