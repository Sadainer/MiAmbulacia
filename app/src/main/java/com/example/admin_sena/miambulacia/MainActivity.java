package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    LocationManager locationMangaer;
    ConnectivityManager connectivityManager;
    int requestcode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        locationMangaer = (LocationManager) getSystemService(LOCATION_SERVICE);



        if (isOnline()) { //Si esta conectado
            if (!locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS Desactivado") //
                        .setCancelable(false)
                        .setMessage("Necesita activar GPS para mejorar su ubicación") //
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(myIntent, requestcode);
                            }
                        }); //
                builder.show();
//
            } else {
                Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
                startActivity(IntEmergencia);
                MainActivity.this.finish();
            }
        }else { //No esta conectado
            Toast.makeText(MainActivity.this,"No hay conexion",Toast.LENGTH_SHORT).show();}
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
      if (locationMangaer.isProviderEnabled(LocationManager.GPS_PROVIDER))
      {
          Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
          startActivity(IntEmergencia);
      }
        MainActivity.this.finish();
    }
    //Validar Estado de red
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
