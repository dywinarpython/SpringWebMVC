package com.webapp.springBoot.controllers;




import java.time.LocalDateTime;

public class ObjectJson {
    private String name;
    private final LocalDateTime dataNow;

    public ObjectJson() {
        this.dataNow = LocalDateTime.now();
    }

    public String getNameObject(){
        return name;
    }
    public LocalDateTime getDataObject(){
        return dataNow;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "ObjectJson " + name + " дата создания: " + dataNow;
    }
}
