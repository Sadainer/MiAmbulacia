package com.example.admin_sena.miambulacia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    Timer timer = new Timer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               Intent IntEmergencia = new Intent(MainActivity.this, MapActivity_Pedido.class);
               startActivity(IntEmergencia);
               MainActivity.this.finish();
           }
       }, 2000);
    }
}
