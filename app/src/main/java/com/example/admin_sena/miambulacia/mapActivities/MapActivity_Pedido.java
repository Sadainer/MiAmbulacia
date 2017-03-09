package com.example.admin_sena.miambulacia.mapActivities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.admin_sena.miambulacia.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.miambulacia.Dto.ParamedicoDto;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.example.admin_sena.miambulacia.HistorialActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MapActivity_Pedido extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    Button btnEnviarAlerta;
    EditText edtDireccion;
    RadioGroup rGrpTipoEmergencia, rGrpNumPacientes;
    Context cnt;
    UbicacionPacienteDto ubicacionPaciente;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<String> keysAmbulancias;
    ArrayList<Location> ambuLocations;
    ArrayList<Float> distancias;
    String idAmbulancia;

    //Variable para guardar la posicion inicial del equipo
    private Location posicionActual = null;

    ProgressDialog progress;
    int menorDistancia;

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

        baseDatosPedidos = new BDPedidos(MapActivity_Pedido.this,"My BaseDatos",null, 1);
        Log.e("nombre DB: ",baseDatosPedidos.getDatabaseName());
        Log.e("PATH DB: ", getApplicationContext().getDatabasePath(baseDatosPedidos.getDatabaseName()).getPath());

        SQLiteDatabase db1 = baseDatosPedidos.getWritableDatabase();

        db1.close();

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
                    ubicacionPaciente.setNumeroPacientes(0);
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
                recuperarAmbulancias();
                //   final Dialog dialogo = new Dialog(MapActivity_Pedido.this);
                //Validar tipo de emergencia
                if (rGrpTipoEmergencia.getCheckedRadioButtonId() == -1) {
                    escoja_tipo_emergencia.show();
                }
                //Validar Numero de pacientes
                else if (rGrpNumPacientes.getCheckedRadioButtonId() == -1) {
                    escoja_num_pacientes.show();
                } else {

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
                                    reference.child("Pedidos").child("Pedido:" + ubicacionPaciente.getIdPaciente()).setValue(ubicacionPaciente);
                                    reference.child("Ambulancias").child(idAmbulancia).child("Pedido").child("aceptado").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.e("data key: ",dataSnapshot.getKey());
                                            boolean aceptado = (boolean)dataSnapshot.getValue();
                                            Log.e("boolean: ",String.valueOf(dataSnapshot.getValue()));
                                            if (aceptado){ //aceptado es True
                                                progress.dismiss();
                                                //pasar a seguimiento:
                                                irAseguimiento();
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

            }

        });

    }

    private void irAseguimiento() {
        final double[] latAmbu = new double[1];
        final double[] lonAmbu = new double[1];
        reference.child("Ambulancias").child(idAmbulancia).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                latAmbu[0] = (double)snapshot.child("latitud").getValue();
                lonAmbu[0] = (double)snapshot.child("longitud").getValue();
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
        i.putExtra("ab",ubicacionPaciente);
        startActivity(i);
    }

    private void pedidoFirebase(UbicacionPacienteDto ubicacionPaciente) {
        reference.child("Ambulancias").child(keysAmbulancias.get(menorDistancia)).child("Pedido").setValue(ubicacionPaciente);
    }


    private void recuperarAmbulancias() {
        Query consulta = reference.child("Ambulancias").orderByKey();
        if (consulta != null){

            consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    compararDistancias(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.e("la consulta es", " exitosa");
        }else {
            Log.e("la consulta es", " nula");
        }

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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MapActivity_Pedido.this, "Conexion con api places FAILED", Toast.LENGTH_SHORT).show();
    }

    ///Dialogo para validar envio de emergencia
    public class CustomDialog extends Dialog {
        Button SiEnviar, btnsalir;
        Activity mActivity;

        public CustomDialog(Activity activity) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    //metodo para crear marcadores
    public void CrearMarcador(Location location, String Titulo)
    {

        reference.child("Pedidos").child("Pedido" + ubicacionPaciente.getIdPaciente()).setValue(ubicacionPaciente.getIdPaciente());
        mMap.clear();
        Marker marcador = mMap.addMarker(new MarkerOptions()
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

    //Clase que permite escuchar las ubicaciones, cada vez que cambia la ubicacion se activa el metodo onLocationChanged y creamos un
    //nuevo marcador con la ubicacion y como titulo la hora del registro de la ubicacion

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

}
