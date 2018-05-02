package org.udg.pds.todoandroid.entity;



public class DatosUsuarioForo {

    private String name;
    private String color;

    public DatosUsuarioForo(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public DatosUsuarioForo() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
