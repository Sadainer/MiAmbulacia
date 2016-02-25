package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity_Pedido extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public LocationManager locationManager;
    private LocationListener locationListener;
    private Location loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity__pedido);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);  //
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);        //declarar tipo de mapa
        mapFragment.getMapAsync(this);

        Button btnEnviarAlerta = (Button)findViewById(R.id.butPedirAmbulancia);
    btnEnviarAlerta.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent =
                    new Intent(MapActivity_Pedido.this,MapsActivity_Seguimiento.class);
                    startActivity(intent);
        }
    });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Mi Ubicación"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}
