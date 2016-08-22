package com.example.admin_sena.miambulacia.MapActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin_sena.miambulacia.CalificacionServicioActivity;
import com.example.admin_sena.miambulacia.ClasesAsincronas.GetAsyncrona;
import com.example.admin_sena.miambulacia.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.miambulacia.Dto.CancelPedidoDto;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.Dto.UbicacionParamedicoDto;
import com.example.admin_sena.miambulacia.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class MapsActivity_Seguimiento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap2;
    private static String DIR_URL = "http://190.109.185.138:8013/api/UbicacionAmbulancias/";
    private static String DIR_URL_CANCELAR = "http://190.109.185.138:8013/api/PedidoAmbulancia/Cancelar";
//    private boolean flag = false;

    Context cnt;
    Timer timer;
    TimerTask timerTask;
    Gson jsson = new Gson();
    final Handler handler = new Handler();
    UbicacionPacienteDto miUbicacion = new UbicacionPacienteDto();
    LatLng MiPosicion = new LatLng(0, 0);
    Marker marcadorAmbulancia;
    Location mylocation = new Location("point b"), ambuLocation = new Location("point a");
    AlertDialog alert;
    AlertDialog irCalificar;
    UbicacionParamedicoDto ubicacionParamedicoDto;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity__seguimiento);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment2);
        mapFragment.getMapAsync(this);
        cnt = this;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("");
        Intent a = getIntent();
        miUbicacion = (UbicacionPacienteDto)a.getExtras().getSerializable("ab");
        reference.child("Pedidos").child("Pedido" + miUbicacion.getIdPaciente()).setValue(miUbicacion);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap2 = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap2.setMyLocationEnabled(true);
       // Intent a = getIntent();
       //  miUbicacion = (UbicacionPacienteDto)a.getExtras().getSerializable("ab");
        if (miUbicacion != null) {
            MiPosicion = new LatLng(miUbicacion.getLatitud(),miUbicacion.getLongitud());

            mylocation.setLatitude(miUbicacion.getLatitud());
            mylocation.setLongitude(miUbicacion.getLongitud());
            mMap2.addMarker(new MarkerOptions()
                    .position(MiPosicion)
                    .title("Mi ubicacion")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));
            mMap2.moveCamera(CameraUpdateFactory.newLatLng(MiPosicion));
            mMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion, 14.0f));

        }
       // LatLng PosicionAmbulancia= new LatLng(a.getDoubleExtra("LatAmbulancia",0),a.getDoubleExtra("LongAmbulancia",0));


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
        //inicializa la tarea de TimerTask
        initializeTimerTask();
        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 4000, 15000); //
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

    public void cancelarPedido(View view){
        final CancelPedidoDto cancelPedidoDto = new CancelPedidoDto(ubicacionParamedicoDto.getCedula());


        final String[] items = {"Me equivoque", "Llegó otra ambulancia","Ya no es necesario el servicio"};
        cancelPedidoDto.setRazonCancela(items[1]);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity_Seguimiento.this);
        alert = builder.create();
        builder.setCancelable(false);
        builder.setTitle("Deseo cancelar la emergencia porque...") //
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enviarCancelarEmergencia(cancelPedidoDto);
                        Intent irAcalificar= new Intent(MapsActivity_Seguimiento.this,CalificacionServicioActivity.class);
                        irAcalificar.putExtra("IdAmbulancia",ubicacionParamedicoDto.getCedula());
                        reference.child("Pedidos").child("Pedido" + miUbicacion.getIdPaciente()).child("Cancelado").setValue(true);
                        Log.e("Pedido cancelado","Pedido");
                        startActivity(irAcalificar);
                        finish();

                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();

                    }
                })
                .setSingleChoiceItems(items,1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 1:
                                cancelPedidoDto.setRazonCancela(items[0]);
                                break;
                            case 2:
                                cancelPedidoDto.setRazonCancela(items[1]);
                                break;
                            case 3:
                                cancelPedidoDto.setRazonCancela(items[2]);
                                break;
                        }
                    }
                });

        builder.show();
    }

    private void enviarCancelarEmergencia(CancelPedidoDto cancelPedidoDto) {
        Log.e("antes de execute:",":)");
        PostAsyncrona cancelar = new PostAsyncrona(jsson.toJson(cancelPedidoDto), MapsActivity_Seguimiento.this, new PostAsyncrona.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.e("resultadoCancelar:",output);
            }
        });
        cancelar.execute(DIR_URL_CANCELAR);
        Log.e("despues de ","execute");
    }
///////////////////Actualizar posicion ambulancia//////////////////////////////////////////////////

    private void ActualizarUbicacionAmbulancias(){
        final   Intent a = getIntent();
        GetAsyncrona Actualizar = new GetAsyncrona();
        try {
            String resultado =  Actualizar.execute(DIR_URL + a.getStringExtra("IdAmbulancia")).get();
            ubicacionParamedicoDto = jsson.fromJson(resultado, UbicacionParamedicoDto.class);
            LatLng posicionAmbu = new LatLng(ubicacionParamedicoDto.getLatitud(),ubicacionParamedicoDto.getLongitud());
            ambuLocation.setLongitude(ubicacionParamedicoDto.getLongitud());
            ambuLocation.setLatitude(ubicacionParamedicoDto.getLatitud());
            if (marcadorAmbulancia!=null){          /////El marcador ya se dibujo por primera vez y debe borrarse para dibujar otro.
                marcadorAmbulancia.remove();
                marcadorAmbulancia = mMap2.addMarker(new MarkerOptions().title("Ambulancia").position(posicionAmbu)
                                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));
            }else {
                marcadorAmbulancia = mMap2.addMarker(new MarkerOptions().title("Ambulancia").position(posicionAmbu)
                                          .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));
                float distancia = mylocation.distanceTo(ambuLocation);
                if (distancia<20){
                    timer.cancel();
                    timerTask.cancel();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(MapsActivity_Seguimiento.this);
                    irCalificar = builder2.create();
                    builder2.setTitle("Su ambulancia ha llegado")
                            .setMessage("Desea calificar servicio?")
                            .setCancelable(false).setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent irAcalificar= new Intent(MapsActivity_Seguimiento.this,CalificacionServicioActivity.class);
                            irAcalificar.putExtra("IdAmbulancia",ubicacionParamedicoDto.getCedula());
                            startActivity(irAcalificar);
                            finish();
                        }
                    }).setNegativeButton("No, Gracias", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent salir = new Intent(MapsActivity_Seguimiento.this, MapActivity_Pedido.class);
                            startActivity(salir);
                            finish();
                        }
                    });
                builder2.show();
                }
            }

        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

}
