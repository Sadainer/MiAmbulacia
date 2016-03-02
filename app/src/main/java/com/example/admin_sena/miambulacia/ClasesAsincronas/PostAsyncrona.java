package com.example.admin_sena.miambulacia.ClasesAsincronas;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sadainer Hernandez on 09/03/2015.
 * Clase para consumir servicios rest mediante el metodo GET
 */
public class PostAsyncrona extends AsyncTask<String, Void, Void> {

    private String mData = null;
    URL url;
    HttpURLConnection connection;
    Context cnt;

    public PostAsyncrona(String data, Context context) {
        mData = data;
        cnt= context;
    }
    public void execute() {
        // TODO Auto-generated method stub
    }


    //Variable ruta se guarda la URI del servicio GET a consumir

    @Override
    protected Void doInBackground(String... params) {
        try {


            url = new URL(params[0]);

            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
            dStream.writeBytes(mData);
            dStream.flush();
            dStream.close();
            int responseCode = connection.getResponseCode();

            final StringBuilder output = new StringBuilder("Request URL " + url);
            output.append(System.getProperty("line.separator") + "Request Parameters " + mData);
            output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
            output.append(System.getProperty("line.separator") + "Type " + "POST");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();
            System.out.println("output===============" + br);
            while((line = br.readLine()) != null ) {
                responseOutput.append(line);
            }
            br.close();

            output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());

        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("IOException");
            e.printStackTrace();
        }
        return null;
    }

    public void onPostExecute(Void result) {
        super.onPostExecute(result);
        //Se retorna un string que contiene un JSON con los datos obtenidos
    }
}