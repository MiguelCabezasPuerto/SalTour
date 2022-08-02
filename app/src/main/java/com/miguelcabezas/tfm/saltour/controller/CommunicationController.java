package com.miguelcabezas.tfm.saltour.controller;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * Clase encargada de las comunicaciones hacia el exterior de la aplicación
 * @author Miguel Cabezas Puerto
 *
 * */
public class CommunicationController {
    public CommunicationController(){}

    /**
     * Manda un correo electrónico a los destinatarios indicados con el cuerpo y asunto recibidos
     * @param target Destinatarios del correo
     * @param context Referencia al contexto de la vista
     * @param subject Asunto del correo
     * @param body Cuerpo del correo
     * @param view Referencia a la vista desde donde se invoca el método
     */
    public void sendMail(String target, String subject, String body, View view, Context context){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{target});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,body);

        try {
            view.getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast msg= Toast.makeText(context,"Sin cliente de correo",Toast.LENGTH_LONG);
            msg.setGravity(Gravity.CENTER, 0, 0);
            msg.show();
        }
    }
}
