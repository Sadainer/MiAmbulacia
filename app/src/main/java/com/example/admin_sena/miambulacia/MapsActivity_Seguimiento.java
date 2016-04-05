package com.example.admin_sena.miambulacia;

import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity_Seguimiento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap2;
//    private static String DIR_URL1 = "http://190.109.185.138:8013/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_activity__seguimiento);
        //Bundle bundle = this.getIntent().getExtras();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment2);
        mapFragment.getMapAsync(this);
        ImageButton btnCancelarPedido = (ImageButton)findViewById(R.id.btnCancelarPedido);
        final TextView mostrar = (TextView)findViewById(R.id.txtmostrar);

        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
mostrar.setText(bundle.getString("LatAmbulancia") + bundle.getString("LongAmbulancia") );
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
        //SharedPreferences prefss = getSharedPreferences("prefUbicacion",MODE_PRIVATE);
        Bundle bundle = this.getIntent().getExtras();
        LatLng MiPosicion = new LatLng(bundle.getDouble("MiLatitud"),bundle.getDouble("MiLongitud"));
       LatLng PosicionAmbulancia= new LatLng(Double.valueOf(bundle.getString("LatAmbulancia")),Double.valueOf(bundle.getString("LongAmbulancia")));
        mMap2 = googleMap;
        mMap2.setMyLocationEnabled(true);

CrearMarcador(MiPosicion, "Mi Posicion");
        CrearMarcador(PosicionAmbulancia,"Ambulancia");

    }


    public void CrearMarcador(LatLng MiPosicion, String Titulo) {
        //mMap2.clear();
        mMap2.addMarker(new MarkerOptions()

                .position(MiPosicion)
                .title(Titulo));
        mMap2.moveCamera(CameraUpdateFactory.newLatLng(MiPosicion));
        mMap2.animateCamera(CameraUpdateFactory.newLatLngZoom(MiPosicion, 14.0f));
    }


}
