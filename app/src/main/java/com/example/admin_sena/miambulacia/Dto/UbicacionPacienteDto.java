package com.example.admin_sena.miambulacia.Dto;

import java.util.Date;

/**
 * Created by Admin_Sena on 01/03/2016.
 */
public class UbicacionPacienteDto {
    private String IdPaciente;
    private Double Latitud;
    private Double Longitud;
    private String TipoEmergencia;
    private int NumeroPacientes;

    public String getTipoEmergencia() {
        return TipoEmergencia;
    }

    public void setTipoEmergencia(String tipoEmergencia) {
        TipoEmergencia = tipoEmergencia;
    }

    public int getNumeroPacientes() {
        return NumeroPacientes;
    }

    public void setNumeroPacientes(int numeroPacientes) {
        NumeroPacientes = numeroPacientes;
    }

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



}
