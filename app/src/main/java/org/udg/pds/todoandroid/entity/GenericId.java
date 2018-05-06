package org.udg.pds.todoandroid.entity;


public class GenericId {

    private Long id;

    public GenericId () {}

    public GenericId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
