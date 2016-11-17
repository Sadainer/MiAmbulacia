package com.example.admin_sena.miambulacia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.miambulacia.BDPedidos;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.mapActivities.MapActivity_Pedido;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class HistorialActivity extends AppCompatActivity {

    RecyclerView historialRecycler;
    ArrayList<UbicacionPacienteDto> miLista;
    TextView tvDataBaseVacia;
    Gson json = new Gson();
    MenuItem menu;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);



        tvDataBaseVacia =(TextView)findViewById(R.id.tvDataBaseVacia);

        //Si no hay elementos en el historial (nunca se ha hecho un pedido)

        miLista = new ArrayList<UbicacionPacienteDto>();


        Log.e("Path ", getApplicationContext().getDatabasePath("My BaseDatos").getPath());



       db = SQLiteDatabase.openDatabase(getApplicationContext().getDatabasePath("My BaseDatos").getPath(),null,SQLiteDatabase.OPEN_READWRITE);
        ///data/data/com.example.admin_sena.miambulacia/databases/My BaseDatos
        //Comprobar si la tabla esta vacia
        String count = "SELECT COUNT(*) FROM TablaPedidos";
        Cursor mcursor = db.rawQuery(count, null);

        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0){
            //La tabla existe y contiene registros
           tvDataBaseVacia.setVisibility(View.INVISIBLE);
            Log.i("tabla","llena");

                Log.e("Entro al ","if");
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Log.e("Entro al ","do");
                   // Log.e("json:",mcursor.getString(2));
                } while(mcursor.moveToNext());
            String[] campos = new String[] {"Pedidos"};
            mcursor = db.query("TablaPedidos", campos, null, null, null, null, null);
            Log.e("TmañoCursorColumnas", String.valueOf(mcursor.getColumnCount()));

            Log.e("TmañoCursorFilas", String.valueOf(mcursor.getCount()));

            mcursor.moveToFirst();
            do {

                Log.e("json:",mcursor.getString(0));
               UbicacionPacienteDto dto = json.fromJson(mcursor.getString(0), UbicacionPacienteDto.class);
                Log.e("Funcionando","DTO");
                miLista.add(dto);

            } while(mcursor.moveToNext());


            mcursor.close();
            db.close();


            historialRecycler = (RecyclerView)findViewById(R.id.rvHistorial);
            historialRecycler.setHasFixedSize(true);

            HistorialAdapter historialAdapter =new HistorialAdapter(miLista, this);
            historialRecycler.setAdapter(historialAdapter);

            historialRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        }

        else{
            tvDataBaseVacia.setVisibility(View.VISIBLE);
            tvDataBaseVacia.setText("No has realizado ningun pedido aún :)");

            Log.i("tabla","vacia");
            //tabla vacia
        }
        db.close();
        mcursor.close();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.menu_historial_activity,m);
        menu = m.findItem(R.id.cubo_basura);
//        menu.setVisible(true);
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.cubo_basura:
                break;
            case R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(HistorialActivity.this, MapActivity_Pedido.class);
        startActivity(i);
        finish();
    }

}
