package com.example.portalrapexpo.Clases;

public class SliderItem {

    private int Imagen;
    private String Duracion,Base,Artista;
    private boolean isPlaying;

    public SliderItem(int imagen, String duracion, String base, String artista, Boolean isplaying) {
        Imagen = imagen;
        Duracion = duracion;
        Base = base;
        Artista = artista;
        isPlaying = isplaying;
    }


    public int getImagen() { return Imagen; }
    public void setImagen(int imagen) { Imagen = imagen; }

    public String getDuracion() { return Duracion; }
    public void setDuracion(String duracion) { Duracion = duracion; }

    public String getBase() { return Base; }
    public void setBase(String base) { Base = base; }

    public String getArtista() { return Artista; }
    public void setArtista(String artista) { Artista = artista; }

    public boolean isPlaying() { return isPlaying; }
    public void setPlaying(boolean playing) { isPlaying = playing; }
}
