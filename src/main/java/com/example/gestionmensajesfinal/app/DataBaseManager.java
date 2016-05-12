package com.example.gestionmensajesfinal.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ALFREDO ALFONZO on 7/16/2014.
 */
public class DataBaseManager {

    public static final String TABLE_NAME = "mensajes";

    public static final String CN_ID        = "_id";
    public static final String CN_MESSAGE   = "mensaje";
    public static final String CN_PHONE     = "telefono";

    //create table mensajes(
    //
    //
    //                      )

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
            + CN_ID + " integer primary key autoincrement, "
            + CN_MESSAGE + " text not null, "
            + CN_PHONE + "  text not null);";

    private DbHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(Context context) {

        helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    public void mInsertarMensaje(String lcMensaje, String lcNumeroTelefono){

        ContentValues loValores = new ContentValues();
        loValores.put(CN_MESSAGE,lcMensaje);
        loValores.put(CN_PHONE,lcNumeroTelefono);

    db.insert(TABLE_NAME,null,loValores);
        Log.e("log_tag", "DataBaseManager: Inserta registro");
    }

    public void mEliminarMensaje(String id){

        db.delete(TABLE_NAME,CN_ID + "=?",new String[]{id});
    }

    public void mModificarRegistro(String id,String lcMensaje,String lcNumeroTelefono){

        ContentValues loValores = new ContentValues();
        loValores.put(CN_MESSAGE,lcMensaje);
        loValores.put(CN_PHONE,lcNumeroTelefono);

        db.update(TABLE_NAME,loValores,CN_ID + "=?",new String[]{id});

    }

    public Cursor mDevolverListaMensajes(){

        String[] columnas = new String[]{CN_ID,CN_MESSAGE,CN_PHONE};

        return db.query(TABLE_NAME,columnas,null,null,null,null,null);
        //return db.rawQuery("SELECT mensaje, telefono FROM mensajes; ", null);

    }
}
