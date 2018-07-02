package org.udg.pds.todoandroid.entity;

import java.io.Serializable;

public class Notificaciones implements Serializable {

    private Boolean altaUsuario;

    private Boolean bajaUsuario;

    private Boolean eventoCancelado;

    private Boolean datosModificados;

    private Boolean usuarioEliminado;

    public Notificaciones() {
    }

    public Notificaciones(Boolean altaUsuario, Boolean bajaUsuario, Boolean eventoCancelado, Boolean datosModificados, Boolean usuarioEliminado) {
        this.altaUsuario = altaUsuario;
        this.bajaUsuario = bajaUsuario;
        this.eventoCancelado = eventoCancelado;
        this.datosModificados = datosModificados;
        this.usuarioEliminado = usuarioEliminado;
    }

    public Boolean getAltaUsuario() {
        return altaUsuario;
    }

    public void setAltaUsuario(Boolean altaUsuario) {
        this.altaUsuario = altaUsuario;
    }

    public Boolean getBajaUsuario() {
        return bajaUsuario;
    }

    public void setBajaUsuario(Boolean bajaUsuario) {
        this.bajaUsuario = bajaUsuario;
    }

    public Boolean getEventoCancelado() {
        return eventoCancelado;
    }

    public void setEventoCancelado(Boolean eventoCancelado) {
        this.eventoCancelado = eventoCancelado;
    }

    public Boolean getDatosModificados() {
        return datosModificados;
    }

    public void setDatosModificados(Boolean datosModificados) {
        this.datosModificados = datosModificados;
    }

    public Boolean getUsuarioEliminado() {
        return usuarioEliminado;
    }

    public void setUsuarioEliminado(Boolean usuarioEliminado) {
        this.usuarioEliminado = usuarioEliminado;
    }
}
