package com.example.admin_sena.miambulacia.Dto;

/**
 * Created by oscar on 25/04/16.
 */
public class ParamedicoDto {

    String Cedula;
    Double Latitud;
    Double Longitud;
    int    Distancia;

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double latitud) {
        Latitud = latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double longitud) {
        Longitud = longitud;
    }

    public int getDistancia() {
        return Distancia;
    }

    public void setDistancia(int distancia) {
        Distancia = distancia;
    }
}
