package org.udg.pds.todoandroid.entity;



public class DatosUsuarioForo {
    private Long id;
    private String name;
    //private String color;

    public DatosUsuarioForo(String name, Long id) {
        this.name = name;
        // this.color = color;
        this.id = id;
    }

    public DatosUsuarioForo() {
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
