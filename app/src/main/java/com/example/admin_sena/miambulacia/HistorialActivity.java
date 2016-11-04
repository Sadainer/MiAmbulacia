package com.example.admin_sena.miambulacia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    RecyclerView historialRecycler;
    ArrayList<ObjetoPruebaRecycler> miLista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        miLista = new ArrayList<ObjetoPruebaRecycler>();
        for (int i=0;i<4;i++){
            miLista.add(new ObjetoPruebaRecycler("Nombre"+String.valueOf(i), "Apellidos"+String.valueOf(i), "Cedula"+ String.valueOf(i) ));



        }

        historialRecycler = (RecyclerView)findViewById(R.id.rvHistorial);
        historialRecycler.setHasFixedSize(true);

        HistorialAdapter historialAdapter =new HistorialAdapter(miLista);
        historialRecycler.setAdapter(historialAdapter);

        historialRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}
