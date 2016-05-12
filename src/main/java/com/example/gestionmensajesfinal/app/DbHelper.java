package com.example.gestionmensajesfinal.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ALFREDO on 7/16/2014.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "bdMensajes3.sqlite";
    private static final int DB_SCHEME_VERSION = 1;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_SCHEME_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(DataBaseManager.CREATE_TABLE);
        Log.e("log_tag", "DbHelper: Creando tabla");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    /*Funcion para hacer repaldo de la base de datos local y poder testear*/
    public static void BD_backup() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());

        final String inFileName = "/data/data/com.mensajesandroid.gestionmensajes.app/databases/"+DB_NAME;
        File dbFile = new File(inFileName);
        FileInputStream fis = null;

        fis = new FileInputStream(dbFile);

        File path = Environment.getExternalStorageDirectory();
        String directorio = path + "/GestionMensajes";
        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
        }
        String outFileName = directorio + "/"+DB_NAME +"_"+timeStamp;

        OutputStream output = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        fis.close();

        Log.e("log_tag", "DbHelper: Se supone que creo archivo de respaldo");
    }
    /*fin de Funcion para hacer repaldo de la base de datos local y poder testear*/

}
