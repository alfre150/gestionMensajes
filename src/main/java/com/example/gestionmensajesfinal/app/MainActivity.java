package com.example.gestionmensajesfinal.app;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public String IP_SERVIDOR;
    public boolean glConectado;
    private ProgressDialog pDialog;
    Httppostaux EnvioPost = new Httppostaux();
    public DataBaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DataBaseManager manager = new DataBaseManager(this);
        manager = new DataBaseManager(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SMSRECEIVEDACTION");

        mActualizarLista();
    }

    /*Clase para recibir el mensaje en este contexto*/
    IntentFilter intentFilter;
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            //TextView SMSes = (TextView) findViewById(R.id.editText2);
            //SMSes.setText("");

            //SMSes.setText(intent.getExtras().getString("mensaje") + "del "+intent.getExtras().getString("numero"));

            /*Aqui se envia el mensaje al servidor
            if (glConectado==true){
                new asyncmensaje().execute(intent.getExtras().getString("numero"),intent.getExtras().getString("mensaje"));
            }
            if (glConectado==false){
                manager.mInsertarMensaje(intent.getExtras().getString("mensaje"),intent.getExtras().getString("numero"));
                mActualizarLista();
            }*/
            //mEnviarMensaje(intent.getExtras().getString("numero"),intent.getExtras().getString("mensaje"));
            mActualizarLista();
        }
    };
    @Override protected void onResume() {
       registerReceiver(intentReceiver, intentFilter);
       super.onResume();
       mActualizarLista();
    }
    @Override protected void onPause() {
        unregisterReceiver(intentReceiver);
        super.onPause();

    }
    /* Fin de clases para obtener el mensaje en el Main Activity*/

    /* Metodo del boton */
    public void mConectarServidor(View view)
    {
        TextView txtServidor = (TextView) findViewById(R.id.editText);
        IP_SERVIDOR = txtServidor.getText().toString();

        new asynconexion().execute(IP_SERVIDOR,"Cualquier_cosa");

        /*if (glConectado==true){
            TextView txtEstado = (TextView) findViewById(R.id.textView3);
            txtEstado.setText("Conectado");
            Toast.makeText(this, "Se ha conectado satisfactoriamente", Toast.LENGTH_LONG).show();
        }else{
            TextView txtEstado = (TextView) findViewById(R.id.textView3);
            txtEstado.setText("No Conectado");
            Toast.makeText(this, "No se ha podido encontrar el servidor", Toast.LENGTH_LONG).show();
        }*/

        mGrabarNombreServidor(IP_SERVIDOR);
    }
    /* Fin del metodo del boton*/

    /*Funcion para enviar mensaje a base de datos web o local segun conexion*/
    public void mEnviarMensaje(String numero,String mensaje){
        if (glConectado==true){
            new asyncmensaje().execute(numero,mensaje);
        }
        if (glConectado==false){
            manager.mInsertarMensaje(mensaje,numero);
            mActualizarLista();
        }

    }
    /* Fin de Funcion para enviar mensaje a base de datos web o local segun conexion*/

    public static String mObtenerServidor(){

        return "algo";
    }

    /*Clase asincrona de envio de mensajes*/
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
    /* Fin de clase de envio de mensajes*/


    /*Clase asincrona para probar conexion */
    class asynconexion extends AsyncTask< String,String, String > {

        String lcServidor,lcConsulta;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Conectando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
            pDialog.dismiss();//ocultamos progess dialog.
            Log.e("onPostExecute=", "" + result);

            if (glConectado==true){
                TextView txtEstado = (TextView) findViewById(R.id.textView3);
                txtEstado.setText("Conectado");
                //Toast.makeText(this, "Se ha conectado satisfactoriamente", Toast.LENGTH_LONG).show();
            }
            if (glConectado==false){
                TextView txtEstado = (TextView) findViewById(R.id.textView3);
                txtEstado.setText("No Conectado");
                //Toast.makeText(this, "No se ha podido encontrar el servidor", Toast.LENGTH_LONG).show();
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

    }
    /*Fin de clase para probar conexion*/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void mActualizarLista()
    {
        final ListView listView = (ListView) findViewById(R.id.listView);

        Cursor cursor = manager.mDevolverListaMensajes();
        //startManagingCursor(cursor);

        String[] from = new String[]{manager.CN_PHONE,manager.CN_MESSAGE};
        int[] to = new int[]{android.R.id.text1,android.R.id.text2};

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,android.R.layout.two_line_list_item, cursor, from, to,0);

        listView.setAdapter(cursorAdapter);

        //listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {});
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //Object ElementoLista = parent.getItemAtPosition(position);
                Cursor curOpcionSeleccionada = (Cursor) listView.getItemAtPosition(position);

                String identificador = curOpcionSeleccionada.getString(0);
                String mensaje = curOpcionSeleccionada.getString(1);
                String numero = curOpcionSeleccionada.getString(2);
                //Toast.makeText(MainActivity.this, "ID  was clicked." + position + mensaje+numero, Toast.LENGTH_LONG).show();

                mEnviarMensaje(numero,mensaje);

                manager.mEliminarMensaje(identificador);

                mActualizarLista();
            }
        });
    }

    /*Funcion para guardar el nombre del servidor en un archivo de texto*/
    public void mGrabarNombreServidor(String lcNombreServidor) {
        //String nomarchivo = "SRV";
        //String contenido = lcNombreServidor;
        String str = lcNombreServidor;
        try{
            FileOutputStream fos = openFileOutput("Srv.txt", MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos);

            // Escribimos el String en el archivo
            osw.write(str);
            osw.flush();
            osw.close();

            // Mostramos que se ha guardado
            Toast.makeText(getBaseContext(), "Guardado", Toast.LENGTH_SHORT).show();

            //textBox.setText("");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
    /*Funcion para guardar el nombre del servidor en un archivo de texto*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
