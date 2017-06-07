package com.example.admin_sena.miambulacia.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class BdPedidoAux {

    private Context context;

    public BdPedidoAux(Context c) {

        this.context = c;
    }

    public void guardarEnBd(final String ubicacion){

        new Thread(new Runnable() {
            @Override
            public void run() {

                SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getApplicationContext()
                                .getDatabasePath("My BaseDatos").getPath(),
                                null, SQLiteDatabase.OPEN_READWRITE);

                db.execSQL("INSERT INTO TablaPedidos (Pedidos) VALUES('"+ubicacion+"')");
                db.close();
            }
        }).start();
    }
}
