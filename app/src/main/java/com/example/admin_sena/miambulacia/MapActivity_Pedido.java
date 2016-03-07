package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.admin_sena.miambulacia.ClasesAsincronas.PostAsyncrona;
import com.example.admin_sena.miambulacia.Dto.UbicacionPacienteDto;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapActivity_Pedido extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnEnviarAlerta;
    EditText edtDireccion;
    RadioGroup rGrpTipoEmergencia;
    RadioGroup rGrpNumPacientes;


    UbicacionPacienteDto ubicacionPaciente;

    //Variable para guardar la posicion inicial del equipo
    private Location posicionActual = null;
    //Variable para guardar al mejor proveedor para obtener la ubicacion
    String MejorProveedor = null;
    private static String DIR_URL = "http://190.109.185.138:8013/api/";
    private LocationManager locationMangaer = null;
    //Listener de ubicacion
    private LocationListener locationListener = null;
    //Variable que controla el tiempo en que se actualiza la ubicacion en segundos
    private static int TIEMPO_ACTUALIZACION = 10;
    //Variable que controla la actualizacion del radio de movimiento de la ambulancia en metros
    private static int RADIO_ACTUALIZACION = 1;
    final Gson gsson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity__pedido);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationMangaer = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);


        Criteria req = new Criteria();
        req.setAccuracy(Criteria.ACCURACY_FINE);
        req.setAltitudeRequired(true);

        //Mejor proveedor por criterio
        MejorProveedor = locationMangaer.getBestProvider(req, false);

        ubicacionPaciente = new UbicacionPacienteDto();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        //Configuramos el listener para que verifique la ubicaciones cada 10000 milisegundos y 20 metros, si cumple las dos condiciones
        //se dispara el metodo
        locationListener = new MiUbicacion();
        locationMangaer.requestLocationUpdates(MejorProveedor, TIEMPO_ACTUALIZACION, RADIO_ACTUALIZACION, locationListener);

        mapFragment.getMapAsync(this);


        btnEnviarAlerta = (Button) findViewById(R.id.butPedirAmbulancia);
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
                    ubicacionPaciente.setTipoEmergencia("Accidente");
                } else if (checkedId == R.id.radCardioRespiratorio) {
                    ubicacionPaciente.setTipoEmergencia("Cardiorespiratorio");
                } else if (checkedId == R.id.radQuemaduras) {
                    ubicacionPaciente.setTipoEmergencia("Quemaduras");
                } else if (checkedId == R.id.radOtro) {
                    ubicacionPaciente.setTipoEmergencia("Otro");
                }
            }
        });


        // Boton para envio de alerta a la ambulancia
        btnEnviarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                Location posicionActual = locationMangaer.getLastKnownLocation(MejorProveedor);


                ubicacionPaciente.setIdPaciente("Sadainer");
                ubicacionPaciente.setLatitud(posicionActual.getLatitude());
                ubicacionPaciente.setLongitud(posicionActual.getLongitude());
                ubicacionPaciente.setDireccion(edtDireccion.getText().toString());

                EnviarUbicacion(ubicacionPaciente);
                //EnviarUbicacion(posicionActual); // Enviar posicion al servidor
//             Intent intent =
//                     new Intent(MapActivity_Pedido.this, MapsActivity_Seguimiento.class);
//             startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        boolean network_enabled = locationMangaer.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (network_enabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            Location location = locationMangaer.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null){
                CrearMarcador(location, "Tu Ubicación");
            }
        }
    }

    //metodo para crear marcadores
    public void CrearMarcador(Location location, String Titulo)
    {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title(Titulo));
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

                //edtDireccion.setText(Direccion.get(0).getAddressLine(0));
            edtDireccion.setText(separated[0] + "-");
            int distancia = separated[0].length() +1;
            edtDireccion.setSelection(distancia);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Metodo para enviar Ubicacion al servidor
    private void EnviarUbicacion(UbicacionPacienteDto ubicacion){

        System.out.println(gsson.toJson(ubicacion));
        PostAsyncrona EnviarUbicacion = new PostAsyncrona(gsson.toJson(ubicacion),getApplicationContext());
        System.out.println(gsson.toJson(ubicacion));
        try {
            String resultado = EnviarUbicacion.execute(DIR_URL).get();
            System.out.println(resultado);
        } catch (InterruptedException e) {
            System.out.println("Error i");
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Error e");
            e.printStackTrace();
        }

    }
    //Clase que permite escuchar las ubicaciones, cada vez que cambia la ubicacion se activa el metodo onLocationChanged y creamos un
    //nuevo marcador con la ubicacion y como titulo la hora del registro de la ubicacion
    private class MiUbicacion implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {

            CrearMarcador(location, "Tu Ubicación");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
