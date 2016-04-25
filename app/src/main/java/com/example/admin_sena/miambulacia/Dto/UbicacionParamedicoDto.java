package com.example.admin_sena.miambulacia.Dto;

import java.util.Date;

/**
 * Created by oscar on 25/04/16.
 */
public class UbicacionParamedicoDto {
    int UbicacionAmbulancia;
    String Cedula;
    Date Fecha;
    Double Latitud;
    Double Longitud;
    String paramedicos;

    public int getUbicacionAmbulancia() {
        return UbicacionAmbulancia;
    }

    public void setUbicacionAmbulancia(int ubicacionAmbulancia) {
        UbicacionAmbulancia = ubicacionAmbulancia;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date fecha) {
        Fecha = fecha;
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

    public String getParamedicos() {
        return paramedicos;
    }

    public void setParamedicos(String paramedicos) {
        this.paramedicos = paramedicos;
    }
}
