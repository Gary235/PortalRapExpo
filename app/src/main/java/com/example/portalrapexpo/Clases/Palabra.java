package com.example.portalrapexpo.Clases;

public class Palabra {

    private String Palabra,id;

    public Palabra() {
    }

    public Palabra(String palabra, String id) {
        this.Palabra = palabra;
        this.id = id;
    }


    public String getPalabra() { return Palabra; }
    public void setPalabra(String palabra) { this.Palabra = palabra; }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
