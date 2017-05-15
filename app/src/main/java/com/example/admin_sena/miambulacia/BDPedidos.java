package com.example.admin_sena.miambulacia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BDPedidos extends SQLiteOpenHelper {

/*

    public static final int VERSION = 1;
    public static final String Nombre_BaseDatos = "baseDatosPedidos.db";

    public static final String nombreTabla = "Pedidos";

    //////////////Campos o columnas de la tabla//////////////////////7

    public static final String col_ID = "ID";
    public static final String col_TipoEmergencia = "TIPO DE EMERGENCIA";
    public static final String col_calificacion = "CALIFICACION";
    public static final String col_Fecha ="FECHA";
    public static final String col_NumeroPacientes ="NUMERO DE PACIENTES";
    public static final String col_Direccion ="LUGAR";
*/
    ///////Sentencia SQL para crear Tabla///////////////

    private String sql = "CREATE TABLE TablaPedidos (ID INTEGER PRIMARY KEY AUTOINCREMENT, Pedidos TEXT)";
    /*
    String sqlCreate = "Crear tabla Pedidos("+col_ID+"INTEGER,"+
            col_Fecha+"LONG,"+col_Direccion+"TEXT,"+
            col_TipoEmergencia+"TEXT,"+col_NumeroPacientes + "INTEGER,"+
            col_Direccion+"TEXT,"+col_calificacion+"TEXT"+")";
*/

    public BDPedidos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        Log.e("TablaPedidos","CREADA");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
