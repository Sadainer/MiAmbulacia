package com.example.admin_sena.miambulacia.mapActivities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.miambulacia.BDPedidos;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.actividades.HistorialActivity;
import com.example.admin_sena.miambulacia.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapActivity_Pedido extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    Button btnEnviarAlerta;
    EditText edtDireccion;
    RadioGroup rGrpTipoEmergencia, rGrpNumPacientes;
    Context cnt;
    UbicacionPacienteDto ubicacionPaciente;
    FirebaseDatabase database;
    DatabaseReference reference, currentEmergency;
    ArrayList<String> keysAmbulancias;
    ArrayList<Location> ambuLocations;
    ArrayList<Float> distancias;
    String idAmbulancia;

    //Variable para guardar la posicion inicial del equipo
    private Location posicionActual = null;
    SimpleDateFormat sdf;
    String currentDateandTime;
    ProgressDialog progress;
    int menorDistancia;

    double latPedidoAnterior;
    double lngPedidoAnterior;
    BDPedidos baseDatosPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity__pedido);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.
                    Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("");

        latPedidoAnterior = 0;
        lngPedidoAnterior = 0;

        currentEmergency = reference.child("CurrentEmergency");
        initPedidoCerca();
        baseDatosPedidos = new BDPedidos(MapActivity_Pedido.this, "My BaseDatos", null, 1);
        sdf = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss", Locale.US);

        //SQLiteDatabase db1 = baseDatosPedidos.getWritableDatabase();

        //db1.close();

        distancias = new ArrayList<>();
        ambuLocations = new ArrayList<>();
        keysAmbulancias = new ArrayList<>();

        cnt = this;
        progress = new ProgressDialog(this);
        progress.setTitle("Enviando emergencia");
        progress.setMessage("Por favor espere");

        ubicacionPaciente = new UbicacionPacienteDto();

        mapFragment.getMapAsync(this);
        btnEnviarAlerta = (Button) findViewById(R.id.btnCancelarPedido);
        edtDireccion = (EditText) findViewById(R.id.edtDireccion);
        rGrpNumPacientes = (RadioGroup) findViewById(R.id.grpNumPaciente);
        rGrpTipoEmergencia = (RadioGroup) findViewById(R.id.grpTipEmergencia);

        rGrpNumPacientes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radPaciente1) {
                    ubicacionPaciente.setNumeroPacientes(1);
                } else if (checkedId == R.id.radPaciente2) {
                    ubicacionPaciente.setNumeroPacientes(2);
                } else if (checkedId == R.id.radPaciente3) {
                    ubicacionPaciente.setNumeroPacientes(3);
                } else if (checkedId == R.id.radPacienteMultiple) {
                    ubicacionPaciente.setNumeroPacientes(4);
                }
            }
        });

        rGrpTipoEmergencia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radAccidente) {
                    ubicacionPaciente.setTipoEmergencia("Accidente de tránsito");
                } else if (checkedId == R.id.radCardioRespiratorio) {
                    ubicacionPaciente.setTipoEmergencia("Cardiorespiratorio");
                } else if (checkedId == R.id.radQuemaduras) {
                    ubicacionPaciente.setTipoEmergencia("Quemaduras");
                } else if (checkedId == R.id.radOtro) {
                    ubicacionPaciente.setTipoEmergencia("Otro");
                }

            }
        });

        final Toast escoja_num_pacientes = Toast.makeText(getApplication(), "Por favor elija un numero de pacientes", Toast.LENGTH_SHORT);
        final Toast escoja_tipo_emergencia = Toast.makeText(getApplicationContext(), "Por favor elija un tipo de Emergencia", Toast.LENGTH_SHORT);
        // Boton para envio de alerta a la ambulancia
        btnEnviarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validar tipo de emergencia
                if (rGrpTipoEmergencia.getCheckedRadioButtonId() == -1) {
                    escoja_tipo_emergencia.show();
                }
                //Validar Numero de pacientes
                else if (rGrpNumPacientes.getCheckedRadioButtonId() == -1) {
                    escoja_num_pacientes.show();
                } else {        //////preguntar si en esa posicion se realizo un pedido
                    if (((latPedidoAnterior != 0) && (lngPedidoAnterior != 0)) ){

                        Toast.makeText(MapActivity_Pedido.this, "Ya se realizó una solicitud a esta ubicación.", Toast.LENGTH_SHORT).show();
                    }else if (pedidoCerca()){

                        Toast.makeText(MapActivity_Pedido.this, "Ya se realizó una solicitud a esta ubicación.", Toast.LENGTH_SHORT).show();

                    }else {

                        recuperarAmbulancias();
                    }

                }

            }

        });

    }

    private void mostrarDialogo2() {

        CustomDialog dialog = new CustomDialog(MapActivity_Pedido.this);
        dialog.show();
        dialog.setTitle("Enviar Emergencia?");
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run() {
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.US);
                        String date = df.format(Calendar.getInstance().getTime());
                        SecureRandom secureRandom = new SecureRandom();
                        ubicacionPaciente.setIdPaciente(String.valueOf(secureRandom.nextInt()));
                        ubicacionPaciente.setLatitud(posicionActual.getLatitude());
                        ubicacionPaciente.setLongitud(posicionActual.getLongitude());
                        ubicacionPaciente.setDireccion(edtDireccion.getText().toString());
                        ubicacionPaciente.setAceptado(false);
                        ubicacionPaciente.setFecha(date);
                        ubicacionPaciente.setIdAmbulancia(idAmbulancia);

                        Log.e("FechaPedido", ubicacionPaciente.getFecha());
                        Log.e("IdAmbulancia", ubicacionPaciente.getIdAmbulancia());

                        reference.child("Pedidos").child("Pedido:" + ubicacionPaciente.getIdPaciente()).setValue(ubicacionPaciente);
                        reference.child("Ambulancias").child(idAmbulancia).child("Pedido").child("aceptado").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.e("data key: ", dataSnapshot.getKey());

                                try {
                                    boolean aceptado = (boolean) dataSnapshot.getValue();
                                    Log.e("boolean: ", String.valueOf(dataSnapshot.getValue()));
                                    if (aceptado) { //aceptado es True
                                        database.getReference("RegistrosAmbulancias/" + idAmbulancia + "/Pedidos/" + ubicacionPaciente.getIdPaciente()).setValue(true);
                                        //primer tiempo para medicion
                                        currentDateandTime = sdf.format(new Date());
                                        reference.child("Pedidos").child("Pedido:" + ubicacionPaciente.getIdPaciente()).child("tiempos").child("2").setValue(currentDateandTime);

                                        progress.dismiss();
                                        //pasar a seguimiento:
                                        irAseguimiento();
                                    }
                                } catch (NullPointerException e) {

                                    progress.dismiss();
                                    Log.e("No se ha aceptado", "la emergencia");
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        // reference.child("Pedidos").child("Pedido:" + ubicacionPaciente.getIdPaciente()).child("aceptado").setValue(true);
                        pedidoFirebase(ubicacionPaciente);
                        //EnviarUbicacion(ubicacionPaciente, MapActivity_Pedido.this);
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 1000);
                ///////////////Mostrar Progress Dialog
                progress.show();
            }
        });
    }

    private void irAseguimiento() {
        currentEmergency.child("latitud").setValue(ubicacionPaciente.getLatitud());
        currentEmergency.child("longitud").setValue(ubicacionPaciente.getLongitud());
        final double[] latAmbu = new double[1];
        final double[] lonAmbu = new double[1];
        reference.child("Ambulancias").child(idAmbulancia).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                latAmbu[0] = (double) snapshot.child("latitud").getValue();
                lonAmbu[0] = (double) snapshot.child("longitud").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Intent i = new Intent(this, MapsActivity_Seguimiento.class);
        //guardar variables en intent
        i.putExtra("LatAmbulancia", latAmbu[0]).putExtra("LongAmbulancia", lonAmbu[0]);
        //i.putExtra("MiLatitud",ubicacion.getLatitud()).putExtra("MiLongitud",ubicacion.getLongitud());
        i.putExtra("IdAmbulancia", idAmbulancia);

        //Log.e("Idambulanciarecibido", outputtojson.getCedula());
        i.putExtra("ab", ubicacionPaciente);
        startActivity(i);
    }

    private void pedidoFirebase(UbicacionPacienteDto ubicacionPaciente) {
        //se le hace el pedido a la ambulancia mas cercana
        reference.child("Ambulancias").child(keysAmbulancias.get(menorDistancia)).child("Pedido").setValue(ubicacionPaciente);

        //primer tiempo para medicion
        currentDateandTime = sdf.format(new Date());
        reference.child("Pedidos").child("Pedido:" + ubicacionPaciente.getIdPaciente()).child("tiempos").child("1").setValue(currentDateandTime);


    }

    private void recuperarAmbulancias() {
        Query consulta = reference.child("Ambulancias").orderByKey();

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.e("la consulta es", " exitosa");
                    compararDistancias(dataSnapshot);
                    mostrarDialogo2();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity_Pedido.this);
                    builder.setIcon(R.mipmap.ic_launcher)
                            .setCancelable(false)
                            .setTitle("No hay ambulancias disponibles")
                            .setPositiveButton("¿Llamar al CRUE?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent llamarCrue = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "3136302690"));
                                    if (ActivityCompat.checkSelfPermission(MapActivity_Pedido.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                        startActivity(llamarCrue);
                                    }

                                    }
                                })
                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.create();
                        builder.show();
                        Log.e("la consulta es", " nula");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void compararDistancias(DataSnapshot dataSnapshot) {
        int c = 0;
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
            //Llenar una lista con los keys de las ambulancias
            keysAmbulancias.add(snapshot.getKey());
            // Crear objeto latlng y guardarlo en una lista
            double latiAmbu = (double)snapshot.child("latitud").getValue();
            double longAmbu = (double)snapshot.child("longitud").getValue();

            Location location = new Location("");
            location.setLatitude(latiAmbu);
            location.setLongitude(longAmbu);
            ambuLocations.add(location);
            Log.e("key:", keysAmbulancias.get(c));
            c++;
            distancias.add(posicionActual.distanceTo(location));

        }
        menorDistancia = distancias.indexOf(Collections.min(distancias));  // devuelve el indice de la posicion de la ambulancia mas cercana
        Log.e("menorDistancia:", String.valueOf(menorDistancia));
        idAmbulancia = keysAmbulancias.get(menorDistancia);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_historial:
                Intent pasarAhistorial = new Intent(MapActivity_Pedido.this, HistorialActivity.class);
                startActivity(pasarAhistorial);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        posicionActual = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (posicionActual != null) {
            CrearMarcador(posicionActual, "milocation");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapActivity_Pedido.this, "Conexion con api places FAILED", Toast.LENGTH_SHORT).show();
    }

    ///Dialogo para validar envio de emergencia
    private class CustomDialog extends Dialog {
        Button SiEnviar, btnsalir;
        Activity mActivity;

        CustomDialog(Activity activity) {
            super(activity);
            mActivity = activity;
            setContentView(R.layout.activity_dialogo_enviar_emergencia);
            final TextView txt_info_pedido = (TextView) findViewById(R.id.txt_info_pedido);
            txt_info_pedido.setText(ubicacionPaciente.getTipoEmergencia() + " con " +
                    ubicacionPaciente.getNumeroPacientes() + " pacientes involucrados " + " en " + edtDireccion.getText().toString());
            SiEnviar = (Button) findViewById(R.id.btnSi_Enviar);
            SiEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancel();
                }
            });
            btnsalir = (Button) findViewById(R.id.btn_no_salir);
            btnsalir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private boolean pedidoCerca(){

        boolean pedidoEstaCerca = false;
        if ((latPedidoAnterior != 0) && (lngPedidoAnterior != 0)){
            // comparar distancia con mi posicion actual
            Location oldLocation = new Location(LOCATION_SERVICE);
            Location MyLocation = new Location(LOCATION_SERVICE);
            oldLocation.setLatitude(latPedidoAnterior);
            oldLocation.setLongitude(lngPedidoAnterior);
            MyLocation.setLatitude(ubicacionPaciente.getLatitud());
            MyLocation.setLongitude(ubicacionPaciente.getLongitud());

            float radio = MyLocation.distanceTo(oldLocation);
            Log.e("Radio", String.valueOf(radio));
            if (!(radio < 100.0)){
                pedidoEstaCerca = true;
            }

        }else {
            pedidoEstaCerca = false;
        }

        return pedidoEstaCerca;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void CrearMarcador(Location location, String Titulo)
    {
        //reference.child("Pedidos").child("Pedido" + ubicacionPaciente.getIdPaciente()).setValue(ubicacionPaciente.getIdPaciente());
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(Titulo).icon(BitmapDescriptorFactory.fromResource(R.drawable.user)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));

        Geocoder geocoder;
        List<Address> Direccion;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            Direccion = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
           // if (edtDireccion.getText().equals("")){
                String Direccion2 = Direccion.get(0).getAddressLine(0);
                String[] separated = Direccion2.split("-");
                String Direccion_incompleta = separated[0] + "-";
            edtDireccion.setText(Direccion_incompleta);
            int distancia = separated[0].length() +1;
            edtDireccion.setSelection(distancia);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initPedidoCerca(){

        Log.e("initPedidocerca","insideMetodo");
        currentEmergency.child("latitud").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() != null){

                    latPedidoAnterior = (double)dataSnapshot.getValue();
                    Log.e("LngPedidoAnterior", String.valueOf(lngPedidoAnterior));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        currentEmergency.child("longitud").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() != null){

                    Log.e("lat snapshot", String.valueOf(dataSnapshot.getValue()));
                    lngPedidoAnterior = (double)dataSnapshot.getValue();
                    Log.e("LatPedidoAnterior", String.valueOf(latPedidoAnterior));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        database.goOffline();
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        super.onStop();

    }
}
