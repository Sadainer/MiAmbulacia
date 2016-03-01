package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapActivity_Pedido extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button btnEnviarAlerta;
    EditText edtDireccion;

    //Variable para guardar la posicion inicial del equipo
    private Location posicionActual = null;
    private LocationManager locationMangaer = null;
    //Listener de ubicacion
    private LocationListener locationListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity__pedido);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationListener = new MiUbicacion();
        locationMangaer = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        //Configuramos el listener para que verifique la ubicaciones cada 10000 milisegundos y 20 metros, si cumple las dos condiciones
        //se dispara el metodo
        locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10, locationListener);

        locationMangaer.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 20, locationListener);
        mapFragment.getMapAsync(this);
        btnEnviarAlerta = (Button)findViewById(R.id.butPedirAmbulancia);
        edtDireccion= (EditText)findViewById(R.id.edtDireccion);
        btnEnviarAlerta.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intent =
                     new Intent(MapActivity_Pedido.this, MapsActivity_Seguimiento.class);
             startActivity(intent);
             }
         });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        //Despues de validar los permisos obtenemos la ultima ubicacion registrada del dispositivo
        posicionActual = locationMangaer.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Si la posicion es diferente de null creamos un marcados con el titulo Posicion inicial
        if (posicionActual != null) {

            CrearMarcador(posicionActual,"Tu Ubicación");

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
            //}

        } catch (IOException e) {
            e.printStackTrace();
        }
//
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
