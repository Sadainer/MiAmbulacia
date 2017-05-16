package com.example.admin_sena.miambulacia.Dto;

/**
 * Created by oscar on 2/06/16.
 */
public class CancelPedidoDto extends UbicacionPacienteDto{
    private String RazonCancela;
    private String IdAmbulancia;

    public CancelPedidoDto(String IdAmbulancia) {
        this.IdAmbulancia = IdAmbulancia;

    }

    public void setRazonCancela(String razonCancela) {
        this.RazonCancela = razonCancela;
    }
}
