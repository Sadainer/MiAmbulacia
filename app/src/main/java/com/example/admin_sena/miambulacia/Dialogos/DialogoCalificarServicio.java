package com.example.admin_sena.miambulacia.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.example.admin_sena.miambulacia.R;

/**
 * Created by oscar on 25/02/16.
 */

//Dialogo Calificar Servicio
public class DialogoCalificarServicio extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_calificacion_servicio,null));
    builder.setTitle("Desea calificar servicio");
        return builder.create();
    }
}
