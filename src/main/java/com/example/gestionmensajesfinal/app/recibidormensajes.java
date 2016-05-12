package com.example.gestionmensajesfinal.app;

/**
 * Created by ALFREDO ALFONZO on 6/21/2014.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;


public class recibidormensajes extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        String numero = "";
        String mensaje = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];


            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                str += "SMS de " + msgs[i].getOriginatingAddress();
                str += " :";
                str += msgs[i].getMessageBody().toString();
                str += "\n";

                numero = msgs[i].getOriginatingAddress();
                mensaje = msgs[i].getMessageBody().toString();
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();

                GestionaMensajes loGestionarMensaje = new GestionaMensajes(context);
                loGestionarMensaje.mEnviarMensaje(numero, mensaje);
                //mEnviarMensaje(numero, mensaje);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("SMSRECEIVEDACTION");
                //broadcastIntent.putExtra("numero", numero);
                //broadcastIntent.putExtra("mensaje", mensaje);
                context.sendBroadcast(broadcastIntent);
            }


        }


    }

}
