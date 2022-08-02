package com.miguelcabezas.tfm.saltour.controller.events;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.miguelcabezas.tfm.saltour.HomeActivity;
import com.miguelcabezas.tfm.saltour.LoginActivity;
import com.miguelcabezas.tfm.saltour.R;

/**
 * Clase encargada de gestionar las notificaciones recibidas de los servicios de Google y mostrarlas como emergentes por pantalla
* @author Miguel Cabezas Puerto
*
 * */
public class FirebaseMessageReceiver extends FirebaseMessagingService {


    /**
     * Extrae el tñitulo y cuerpo del mensaje recibido en la notificación
     * @param remoteMessage Mensaje recibido de los servicios de Google
     */
    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }


        if (remoteMessage.getNotification() != null) {

            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }


    /**
     * Elabora el diseño personaliado para mostrar la notifación
     * @param title título de la notificación
     *  @param message cuerpo del mensaje recibido de los servicios de Google
     * @return Diseño de la notificación
     */
    private RemoteViews getCustomDesign(String title,
                                        String message) {
        RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.icon,
                R.mipmap.ic_launcher_round);
        return remoteViews;
    }

    /**
     * Muestra la notificación
     * @param title título de la notificación
     *  @param message cuerpo del mensaje recibido de los servicios de Google
     */
    public void showNotification(String title,
                                 String message) {

        Intent intent
                = new Intent(this, LoginActivity.class);

        String channel_id = "notification_channel";

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000,
                        1000, 1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);


        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContent(
                    getCustomDesign(title, message));
        }
        else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher_round);
        }

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }
}
