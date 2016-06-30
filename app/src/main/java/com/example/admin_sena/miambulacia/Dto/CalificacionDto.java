package com.example.admin_sena.miambulacia.Dto;

/**
 * Created by oscar on 2/06/16.
 */
public class CalificacionDto  {
    private int CalificacionServicio;
    private String IdAmbulancia;
    private String IdPaciente;


    public String getIdAmbulancia() {
        return IdAmbulancia;
    }

    public void setIdAmbulancia(String idAmbulancia) {
        IdAmbulancia = idAmbulancia;
    }

    public String getIdPaciente() {
        return IdPaciente;
    }

    public void setIdPaciente(String idPaciente) {
        IdPaciente = idPaciente;
    }

    public int getCalificacionServicio() {
        return CalificacionServicio;
    }

    public void setCalificacionServicio(int calificacionServicio) {
        this.CalificacionServicio = calificacionServicio;
    }
}
