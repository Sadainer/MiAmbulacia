package com.example.admin_sena.miambulacia.mapActivities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.admin_sena.miambulacia.CalificacionServicioActivity;
import com.example.admin_sena.miambulacia.Dto.CancelPedidoDto;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity_Seguimiento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap2;

    Context cnt;
    Timer timer;
    TimerTask timerTask;
    Gson jsson = new Gson();
    final Handler handler = new Handler();
    UbicacionPacienteDto miUbicacion = new UbicacionPacienteDto();
    LatLng MiPosicion = new LatLng(0, 0);
    Marker marcadorAmbulancia;
    Location mylocation = new Location("point b"), ambuLocation = new Location("point a");
    AlertDialog alert, irCalificar;
    FirebaseDatabase database;
    DatabaseReference reference;
    String idAmbulancia;

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
        idAmbulancia = a.getStringExtra("IdAmbulancia");
        Log.e("IdAmbulancia ",idAmbulancia);
        miUbicacion = (UbicacionPacienteDto)a.getExtras().getSerializable("ab");
        String saveInDB = jsson.toJson(miUbicacion);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(getApplicationContext().getDatabasePath("My BaseDatos").getPath(),null,SQLiteDatabase.OPEN_READWRITE);

        db.execSQL("INSERT INTO TablaPedidos (Pedidos) VALUES('"+saveInDB+"')");
        db.close();

        reference.child("Ambulancias").child(idAmbulancia).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("ambuPositionChanged"," "+dataSnapshot.getKey());
                actualizarPosicionAmbulancia(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void actualizarPosicionAmbulancia(DataSnapshot dataSnapshot) {
        double latAmbu = (double)dataSnapshot.child("latitud").getValue();
        double lngAmbu = (double)dataSnapshot.child("longitud").getValue();

        LatLng posicionAmbu = new LatLng(latAmbu, lngAmbu);
        if (marcadorAmbulancia!=null){          /////El marcador ya se dibujo por primera vez y debe borrarse para dibujar otro.
                                                //Dibujar polilinea entre posicion ultimo marcador y nuevo marcador
            marcadorAmbulancia.setPosition(posicionAmbu);
        }else {
            marcadorAmbulancia = mMap2.addMarker(new MarkerOptions().title("Ambulancia").position(posicionAmbu)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance3)));

        }

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
        timer.schedule(timerTask, 6000, 15000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        //
                        float distancia = mylocation.distanceTo(ambuLocation);
                        Log.e("distancia ", String.valueOf(distancia));
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
                                    irAcalificar.putExtra("IdAmbulancia", idAmbulancia);
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
                });
            }
        };
    }

    public void cancelarPedido(View view){
        final CancelPedidoDto cancelPedidoDto = new CancelPedidoDto(idAmbulancia);


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
                        irAcalificar.putExtra("IdAmbulancia", idAmbulancia);
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

        Log.e("despues de ","execute");
    }
///////////////////Actualizar posicion ambulancia//////////////////////////////////////////////////

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
