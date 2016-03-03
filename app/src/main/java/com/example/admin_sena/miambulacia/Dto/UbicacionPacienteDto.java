package com.example.admin_sena.miambulacia.Dto;

import java.util.Date;

/**
 * Created by Admin_Sena on 01/03/2016.
 */
public class UbicacionPacienteDto {
    private String IdPaciente;

    public String getIdPaciente() {
        return IdPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        IdPaciente = idPaciente;
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

    private Double Latitud;
    private Double Longitud;

}
