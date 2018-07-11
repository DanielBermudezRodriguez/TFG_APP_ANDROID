package org.udg.pds.todoandroid.entity;



public class DatosUsuarioForo {
    private Long id;
    private String name;
    private String date;

    public DatosUsuarioForo(String name, Long id, String date) {
        this.name = name;
        this.id = id;
        this.date = date;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
