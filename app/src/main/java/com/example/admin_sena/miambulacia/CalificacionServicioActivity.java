package com.example.admin_sena.miambulacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class CalificacionServicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_servicio);
        final RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup3);
        Button Enviar = (Button)findViewById(R.id.btnEnviarCalificacion);
        Button noSalir = (Button)findViewById(R.id.but_no_gracias);
        noSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalificacionServicioActivity.this,MapActivity_Pedido.class);
                startActivity(intent);
                finish();

            }
        });
        Enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(CalificacionServicioActivity.this,MapActivity_Pedido.class);
                startActivity(intent2);
                finish();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId==R.id.radExcelente){

                        }else if (checkedId==R.id.radBueno){

                        }else if (checkedId==R.id.radRegular){

                        }else if (checkedId==R.id.radPesimo){

                        }
                    }
                });
            }
        });
    }
}
