package com.example.gestionmensajesfinal.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by ALFREDO ALFONZO on 7/29/2014.
 */
public class GestionaMensajes {

   public boolean glConectado;
   public Context loContexto;
    public DataBaseManager manager;
    public String IP_SERVIDOR;
    public String numero;
    public String mensaje;
    private static final int READ_BLOCK_SIZE = 100;

    public GestionaMensajes(Context context){
       loContexto = context;
        manager = new DataBaseManager(loContexto);
    }
    /*Funcion para enviar mensaje a base de datos web o local segun conexion*/
    public void mEnviarMensaje(String lcNumero,String lcMensaje){

        numero = lcNumero;
        mensaje = lcMensaje;
        IP_SERVIDOR = mRecuperarNombreServidor();

        new asynconexion().execute(IP_SERVIDOR, "Cualquier_cosa");


        /*if (glConectado==true){
            new asyncmensaje().execute(numero,mensaje);
        }
        if (glConectado==false){
            manager.mInsertarMensaje(mensaje,numero);
        }*/

    }
    /* Fin de Funcion para enviar mensaje a base de datos web o local segun conexion*/

    /*Clase asincrona de envio de mensajes al servidor web*/
    class asyncmensaje extends AsyncTask< String,String, String > {

        String numero,mensaje;

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            //obtnemos numero y mensaje
            numero=params[0];
            mensaje=params[1];

            mEnviarParametros(numero,mensaje);
            return "ok";
        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido pasamos a la sig. activity o mostramos error*/
        protected void onPostExecute(String result) {
            //pDialog.dismiss();//ocultamos progess dialog.
            Log.e("onPostExecute=", "" + result);
        }
    }

    public void mEnviarParametros(String lcNumero,String lcMensaje)
    {
        /***************************************************************/
        /*Creando los parametros post y enviando al servicio web       */
        /***************************************************************/
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
        String URL_connect= IP_SERVIDOR + "/MensajesAndroid/RecibirMensaje.php";//ruta en donde estan nuestros archivos
        Httppostaux post = new Httppostaux();

        postparameters2send.add(new BasicNameValuePair("lcNumero",lcNumero));
        postparameters2send.add(new BasicNameValuePair("lcMensaje",lcMensaje));

        JSONArray jdata= null;
        try {
            jdata = post.getserverdata(postparameters2send, URL_connect);
            int ParametrosConsultados=jdata.getInt(0);//accedemos al valor
            Log.e("ParametrosConsultados","ParametrosConsultados= "+ParametrosConsultados);//muestro por log que obtuvimos

        } catch (Exception e) {
            e.printStackTrace();
        }
        /***************************************************************/

    }
    /* Fin de clase de envio de mensajes al servidor web*/

    /*Clase asincrona para probar conexion */
    class asynconexion extends AsyncTask< String,String, String > {

        String lcServidor,lcConsulta;

        protected void onPreExecute() {

        }

        protected String doInBackground(String... params) {
            //obtnemos numero y mensaje
            lcServidor=params[0];
            lcConsulta=params[1];
            mProbarConexion(lcServidor,lcConsulta);
            return "ok";
        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido pasamos a la sig. activity o mostramos error*/
        protected void onPostExecute(String result) {
            Log.e("onPostExecute=", "" + result);

            if (glConectado==true){

                new asyncmensaje().execute(numero,mensaje);
            }
            if (glConectado==false){
                manager.mInsertarMensaje(mensaje,numero);
            }
        }
    }
    public void mProbarConexion(String lcServidor,String lcConsulta)
    {
        /***************************************************************/
            /*Creando los parametros post y enviando al servicio web       */
        /***************************************************************/
        ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
        String URL_connect= lcServidor + "/MensajesAndroid/ProbarConexion.php";//ruta en donde estan nuestros archivos
        Httppostaux loEnvioPost = new Httppostaux();

        postparameters2send.add(new BasicNameValuePair("lcConsulta",lcConsulta));
        //postparameters2send.add(new BasicNameValuePair("lcMensaje",lcMensaje));

        JSONArray jdata= null;
        try {

            jdata = loEnvioPost.getserverdata(postparameters2send, URL_connect);
            JSONObject jsonConexion = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
            int ParametrosConsultados=jsonConexion.getInt("ValorConexion");//accedemos al valor
            Log.e("ParametrosConsultados","ParametrosConsultados= "+ParametrosConsultados);//muestro por log que obtuvimos
            glConectado=true;

        } catch (Exception e) {
            Log.e("JSON  ", "ERROR");
            glConectado=false;
            e.printStackTrace();
        }

        /***************************************************************/

    }
    /*Fin de clase para probar conexion*/


    /*Funcion para recuperar el nombre del servidor*/
    public String mRecuperarNombreServidor() {
        String todo = "";
        try{
            FileInputStream fis = loContexto.openFileInput("Srv.txt");
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer = new char[READ_BLOCK_SIZE];
            String s = "";

            int charRead;
            while((charRead = isr.read(inputBuffer)) > 0){
                // Convertimos los char a String
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                s += readString;

                inputBuffer = new char[READ_BLOCK_SIZE];
            }

            // Establecemos en el EditText el texto que hemos leido
            //textBox.setText(s);
            todo = s;
            // Mostramos un Toast con el proceso completado
            Toast.makeText(loContexto, "Servidor:" + todo, Toast.LENGTH_LONG).show();

            isr.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return todo;
    }
    /*Fin de funcion de recuperar nombre de servidor*/
}
