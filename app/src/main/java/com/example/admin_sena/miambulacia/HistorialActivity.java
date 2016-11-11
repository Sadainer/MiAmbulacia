package com.example.admin_sena.miambulacia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    RecyclerView historialRecycler;
    ArrayList<ObjetoPruebaRecycler> miLista;
    Button apareceMenu;
    MenuItem menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        apareceMenu = (Button)findViewById(R.id.btnApareceMenu);


        //Si no hay elementos en el historial (nunca se ha hecho un pedido)

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

    public void aparecermenu(View v) {
        menu.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_historial_activity,m);
        menu = m.findItem(R.id.cubo_basura);
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cubo_basura:

        }
        return super.onOptionsItemSelected(item);
    }


}
