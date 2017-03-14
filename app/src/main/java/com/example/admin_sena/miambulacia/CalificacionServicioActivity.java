package com.example.admin_sena.miambulacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.admin_sena.miambulacia.mapActivities.MapActivity_Pedido;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CalificacionServicioActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    String calificacion = "Excelente" ;
    FirebaseDatabase database;
    DatabaseReference reference;
    String IdPedido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_servicio);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup3);
        IdPedido = getIntent().getStringExtra("IdPedido");
        Log.e("IDpedidocal ", IdPedido);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Pedidos/"+"Pedido:"+IdPedido);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.radExcelente){
                    calificacion = "Excelente";
                }else if (checkedId==R.id.radBueno){
                    calificacion = "Bueno";
                }else if (checkedId==R.id.radRegular){
                    calificacion = "Regular";
                }else if (checkedId==R.id.radMalo){
                    calificacion = "Malo";
                }else if (checkedId==R.id.radPesimo){
                    calificacion = "Pesimo";
                }
            }
        });
    }

    public void salir(View v) {
        volver();
    }


    public void enviarCalificacion(View v) {

        reference.child("Calification").setValue(calificacion);
        Toast.makeText(CalificacionServicioActivity.this, "Gracias por usar nuestros servicios!", Toast.LENGTH_LONG).show();
        volver();
    }

    private void volver() {
        Intent intent = new Intent(CalificacionServicioActivity.this,MapActivity_Pedido.class);
        startActivity(intent);
        finish();
    }


}
