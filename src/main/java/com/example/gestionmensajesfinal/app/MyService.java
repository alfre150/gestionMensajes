package com.example.gestionmensajesfinal.app;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private Timer mTimer  = null;
    private static final long UPDATE_INTERVAL = 5000;

	@Override
	public IBinder onBind(Intent arg0)
    {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Servicio en Ejecucion", Toast.LENGTH_SHORT).show();

        /* ************************************************************************* */
        /* Codigo que se ejecuta mientras el servicio se encuentre iniciado */
        /* *********************************************************************** */

		/* ************************************************************************ */
        /* Fin de codigo que se ejecuta mientras el servicio se encuentre iniciado */
        /* *********************************************************************** */
        return START_STICKY;
	}

    @Override
    public void onCreate(){
        super.onCreate();
        this.mTimer = new Timer();
        this.mTimer.scheduleAtFixedRate(
                new TimerTask(){
                    @Override
                    public void run() {
                        ejecutarTarea();
                    }
                }
                , 0, 1000 * 60);
    }

    private void ejecutarTarea(){
        Thread t = new Thread(new Runnable() {
            public void run() {
                /*NotifyManager notify = new NotifyManager();
                notify.playNotification(getApplicationContext(),
                        HolaMundoActivity.class, "Tienes una notificación"
                        , "Notificación", R.drawable.img_notify); */



            }
        });
        t.start();
    }


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Toast.makeText(this, "Servicio destruido", Toast.LENGTH_SHORT).show();
	}

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
        //String URL_connect= IP_SERVIDOR + "/MensajesAndroid/RecibirMensaje.php";//ruta en donde estan nuestros archivos
        String URL_connect= "/MensajesAndroid/RecibirMensaje.php";//ruta en donde estan nuestros archivos
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
}
