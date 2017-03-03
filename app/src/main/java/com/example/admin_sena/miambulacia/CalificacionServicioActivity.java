package com.example.admin_sena.miambulacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.example.admin_sena.miambulacia.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.miambulacia.Dto.CalificacionDto;
import com.example.admin_sena.miambulacia.mapActivities.MapActivity_Pedido;
import com.google.gson.Gson;

public class CalificacionServicioActivity extends AppCompatActivity {

    Gson jsson = new Gson();
    RadioGroup radioGroup;
    CalificacionDto calificacion;
    public static final String URL_CALIFICAR = "http://myambulancia.azurewebsites.net/api/Calificar";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_servicio);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup3);
        String IdAmbulancia = getIntent().getStringExtra("IdAmbulancia");
        calificacion = new CalificacionDto();
        calificacion.setIdAmbulancia(IdAmbulancia);
        calificacion.setIdPaciente("Oscar");
    }

    public void salir(View v) {
        Intent intent = new Intent(CalificacionServicioActivity.this,MapActivity_Pedido.class);
        startActivity(intent);
        finish();
    }

    public void enviarCalificacion(View v) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.radExcelente){
                    calificacion.setCalificacionServicio(5);
                }else if (checkedId==R.id.radBueno){
                    calificacion.setCalificacionServicio(4);
                }else if (checkedId==R.id.radRegular){
                    calificacion.setCalificacionServicio(3);
                }else if (checkedId==R.id.radMalo){
                    calificacion.setCalificacionServicio(2);
                }else if (checkedId==R.id.radPesimo){
                    calificacion.setCalificacionServicio(1);
                }
            }
        });
        PostAsyncrona enviar = new PostAsyncrona(jsson.toJson(calificacion), CalificacionServicioActivity.this, new PostAsyncrona.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.e("EnvioCalificacion:",output);
                if (output != null){
                    Intent intent2 = new Intent(CalificacionServicioActivity.this, MapActivity_Pedido.class);
                    startActivity(intent2);
                    finish();
                }
            }
        });
        enviar.execute(URL_CALIFICAR);

    }


}
