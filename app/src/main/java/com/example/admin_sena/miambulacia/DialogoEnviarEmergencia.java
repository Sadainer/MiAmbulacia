package com.example.admin_sena.miambulacia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by oscar on 12/03/16.
 */
public class DialogoEnviarEmergencia extends DialogFragment {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
          //  builder.setView(inflater.inflate(R.layout.activity_dialogo_enviar_emergencia,null));


            return builder.create();
        }

/*
si_enviar.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View v) {
        mostrar.setText("Si funciona");
    }
});
*/
 }

