package com.example.admin_sena.miambulacia.Dto;

import java.io.Serializable;

public class UbicacionPacienteDto implements Serializable {

    private String IdPaciente;
    private Double Latitud, Longitud;
    private String TipoEmergencia;
    private int NumeroPacientes;
    private String Direccion;
    private boolean Aceptado;
    private String Fecha, idAmbulancia;

    public void setIdAmbulancia(String idAmbulancia) {
        this.idAmbulancia = idAmbulancia;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public void setAceptado(boolean aceptado) {
        Aceptado = aceptado;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

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

    public String getFecha() {
        return Fecha;
    }

    public String getIdAmbulancia() {
        return idAmbulancia;
    }
}
