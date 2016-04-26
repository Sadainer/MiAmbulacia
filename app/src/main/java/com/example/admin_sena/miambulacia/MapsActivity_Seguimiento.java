package com.example.admin_sena.miambulacia;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin_sena.miambulacia.ClasesAsincronas.GetAsyncrona;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.Dto.UbicacionParamedicoDto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MapsActivity_Seguimiento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap2;
    private static String DIR_URL = "http://190.109.185.138:8013/api/UbicacionAmbulancias/";
    Context cnt;
    Timer timer;
    TimerTask timerTask;
    Gson jsson = new Gson();
    final Handler handler = new Handler();
    UbicacionPacienteDto miUbicacion = new UbicacionPacienteDto();
    LatLng MiPosicion = new LatLng(0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity__seguimiento);
        //Bundle bundle = this.getIntent().getExtras();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment2);
        mapFragment.getMapAsync(this);
        cnt=this;
        Button btnCancelarPedido = (Button)findViewById(R.id.btnCancelarPedido);
       // final TextView mostrar = (TextView)findViewById(R.id.txtmostrar);


        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//Toast.makeText(MapsActivity_Seguimiento.this,"Parcelable: "+"Latitud "+String.valueOf(miUbicacion.getLatitud())+"Longitud: "+String.valueOf(miUbicacion.getLongitud()),Toast.LENGTH_SHORT).show();
            }
        });
    /*    btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////
               android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                DialogoCalificarServicio dialogoCalificarServicio = new DialogoCalificarServicio();
                dialogoCalificarServicio.show(fragmentManager, "tagCalificarServicio");


            }
        });
*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent a = getIntent();
        miUbicacion = (UbicacionPacienteDto)a.getExtras().getSerializable("ab");
        if (miUbicacion != null) {
            MiPosicion = new LatLng(miUbicacion.getLatitud(),miUbicacion.getLongitud());
        }
        LatLng PosicionAmbulancia= new LatLng(a.getDoubleExtra("LatAmbulancia",0),a.getDoubleExtra("LongAmbulancia",0));
        mMap2 = googleMap;
        mMap2.setMyLocationEnabled(true);

        CrearMarcador(MiPosicion, "Mi Posicion",PosicionAmbulancia, "Ambulancia");
        //CrearMarcador(PosicionAmbulancia, "Ambulancia");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 5000, 15000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp

                        ActualizarUbicacionAmbulancias();
                    }
                });
            }
        };
    }


//Actualizar posicion ambulancia

    private void ActualizarUbicacionAmbulancias(){
        final   Intent a = getIntent();


        GetAsyncrona Actualizar = new GetAsyncrona();

        try {
            String resultado =  Actualizar.execute(DIR_URL + a.getStringExtra("IdAmbulancia")).get();

            Log.e("URL +ID: ", DIR_URL + a.getStringExtra("IdAmbulancia"));
            Log.e("Resultado", resultado);
            Toast.makeText(MapsActivity_Seguimiento.this,resultado,Toast.LENGTH_SHORT).show();
             //jsson =  jsson.toJson(resultado);
            UbicacionParamedicoDto ubicacionParamedicoDto = jsson.fromJson(resultado, UbicacionParamedicoDto.class);
            LatLng posicionAmbu = new LatLng(ubicacionParamedicoDto.getLatitud(),ubicacionParamedicoDto.getLongitud());
            CrearMarcador(MiPosicion, "Mi Posicion",posicionAmbu, "Ambulancia");


        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }

    }

    public void CrearMarcador(LatLng MiPosicion, String Titulo, LatLng PosAmbulancia, String Titulo2) {
        mMap2.clear();
        mMap2.addMarker(new MarkerOptions()
                .position(MiPosicion)
                .title(Titulo));
        mMap2.addMarker(new MarkerOptions()
                .position(PosAmbulancia)
                .title(Titulo2));
        mMap2.moveCamera(CameraUpdateFactory.newLatLng(MiPosicion));
        mMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion, 14.0f));
    }

}
