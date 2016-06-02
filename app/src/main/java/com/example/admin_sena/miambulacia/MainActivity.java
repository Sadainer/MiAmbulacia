package com.example.admin_sena.miambulacia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
                Bundle bundle = new Bundle();
                //bundle.
                //IntEmergencia.putExtra("Direcciones",);
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

    private List<Address> setDirecciones(){
        Geocoder geocoder;
        List<Address> direccionesClinicas = null;
        geocoder= new Geocoder(this,Locale.getDefault());
        try {
            direccionesClinicas=geocoder.getFromLocation(10.4706104,-73.2593356,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

     /*   Arrays.fill(direccionesClinicas,new Address(new Locale("es_ES")));
        //Clinica Laura Daniela
        direccionesClinicas[0].setFeatureName("Clinica Laura Daniela");
        direccionesClinicas[0].setLatitude(10.4706104);
        direccionesClinicas[0].setLongitude(-73.2593356);
        //Clinica Cesar
        direccionesClinicas[1].setFeatureName("Clinica Cesar. Cll 16 #1490");
        direccionesClinicas[1].setLatitude(10.472058);
        direccionesClinicas[1].setLongitude(-73.2543377);
        //Clinica Valledupar
        direccionesClinicas[2].setFeatureName("Clinica Valledupar");
        direccionesClinicas[2].setLatitude(10.471491);
        direccionesClinicas[2].setLongitude(-73.2544852);
        //Clinica Erasmo
        direccionesClinicas[3].setFeatureName("Clínica de Fractura Eramvs");
        direccionesClinicas[3].setLatitude(10.48782);
        direccionesClinicas[3].setLongitude(-73.2663187);
        //Hospital Eduardo Arredondo Nevada
        direccionesClinicas[4].setFeatureName("Hospital Eduardo Arredondo, Cl. 6");
        direccionesClinicas[4].setLatitude(10.476754);
        direccionesClinicas[4].setLongitude(-73.283247);
        //Clinica Medicos Ltda.
        direccionesClinicas[5].setFeatureName("Clinica Medicos Ltda Cra 11.");
        direccionesClinicas[5].setLatitude(10.4724154);
        direccionesClinicas[5].setLongitude(-73.2496831);
        //Hospital Rosario Pumarejo De López
        direccionesClinicas[6].setFeatureName("Hospital Rosario Pumarejo De López");
        direccionesClinicas[6].setLatitude(10.471491);
        direccionesClinicas[6].setLongitude(-73.2544852);


        /*
        direccionesClinicas[7].setFeatureName("");
        direccionesClinicas[7].setLatitude();
        direccionesClinicas[7].setLongitude();
        //
        direccionesClinicas[8].setFeatureName("");
        direccionesClinicas[8].setLatitude();
  //      direccionesClinicas[8].setLongitude();
        //
    //    direccionesClinicas[].setFeatureName("");
      //  direccionesClinicas[].setLatitude();
      //  direccionesClinicas[].setLongitude();
        //
        direccionesClinicas[].setFeatureName("");
        direccionesClinicas[].setLatitude();
        direccionesClinicas[].setLongitude();
        //
        direccionesClinicas[].setFeatureName("");
        direccionesClinicas[].setLatitude();
        direccionesClinicas[].setLongitude();
*/


        return direccionesClinicas;
    }


}
