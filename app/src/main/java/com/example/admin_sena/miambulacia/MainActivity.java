package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    Timer timer = new Timer();
    LocationManager locManager;
    int requestcode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("GPS Desactivado") //
                    .setMessage("Necesita activar GPS para mejorar su ubicación") //
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // TODO
                            Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                            startActivityForResult(myIntent,requestcode);
                        }
                    }); //
            builder.show();
//
        }else {
            Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
            startActivity(IntEmergencia);
            MainActivity.this.finish();
        }
/*
           timer.schedule(new TimerTask() {
           @Override
           public void run() {
               Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
               startActivity(IntEmergencia);
               MainActivity.this.finish();
           }
       }, 2000);
  */
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
      if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
      {
          Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
          startActivity(IntEmergencia);
      }
        MainActivity.this.finish();
    }
}
