package com.example.admin_sena.miambulacia.Dto;

/**
 * Created by oscar on 2/06/16.
 */
public class CancelPedidoDto extends UbicacionPacienteDto{
    private String razonCancela;
    private String IdAmbulanciaCancel;

    public String getIdAmbulanciaCancel() {
        return IdAmbulanciaCancel;
    }

    public void setIdAmbulanciaCancel(String idAmbulanciaCancel) {
        IdAmbulanciaCancel = idAmbulanciaCancel;
    }

    public String getRazonCancela() {
        return razonCancela;
    }

    public void setRazonCancela(String razonCancela) {
        this.razonCancela = razonCancela;
    }
}
