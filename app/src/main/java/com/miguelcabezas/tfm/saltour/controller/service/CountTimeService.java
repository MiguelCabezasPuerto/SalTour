package com.miguelcabezas.tfm.saltour.controller.service;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miguelcabezas.tfm.saltour.model.ActiveChallengeSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CountTimeService extends Service {

    private ArrayList<Map<String,String>> challenegesAndTime;
    private static String challengeName,emailUser;
    private long initTime, endTime;

    public CountTimeService(){}


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Servicio onBind...");
        return null;
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "Servicio creado...");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Servicio iniciado...");
        challengeName = intent.getStringExtra("challengeName");
        initTime = intent.getLongExtra("initTime", SystemClock.elapsedRealtime());
        emailUser = intent.getStringExtra("emailUser");
        Log.d("User",emailUser);
        Log.d("Challenge",challengeName);
        Log.d("Iniciado a las", String.valueOf(initTime));
        ActiveChallengeSingleton activeChallengeSingleton = ActiveChallengeSingleton.getInstance();
        activeChallengeSingleton.setName(challengeName);

        return START_NOT_STICKY;
    }

    /*Si es destruido quiere decir que es llamado desde el activity de parar reto sin completar y por tanto el reto ha sido empezado e interrumpido sin ser completado*/
    @Override
    public void onDestroy() {
        Log.d(TAG, "Servicio destruido...");

        endTime = SystemClock.elapsedRealtime();

        Log.d("Challenge",challengeName);
        Log.d("Inicio", String.valueOf(initTime));
        Log.d("Parada", String.valueOf(endTime));

        final long difference = endTime - initTime;

        /*Guarda el reto en BBDD en el usuario que corresponda de la forma
        * clave: challengeName
        * valor: endTime - initTime
        * Si es llamado desde el botón se queda así el valor
        * Si es llamado desde el escaner quiere decir que el reto se ha completado y al final del valor la actividad de escaner le pondrá una C*/
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dbUsers = db.collection("users");

        final DocumentReference userSelected = db.collection("users").document(emailUser);

        userSelected.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    Map<String,String> challengesAndTime = (Map<String, String>) document.get("challengesAndTime");
                    if(challengesAndTime == null || challengesAndTime.isEmpty()){
                        challengesAndTime = new HashMap<>();
                        challengesAndTime.put(challengeName, String.valueOf(difference));
                    }else if(challengesAndTime.get(challengeName) == null){
                        challengesAndTime.put(challengeName, String.valueOf(difference));
                    } else{
                        challengesAndTime.put(challengeName, String.valueOf(Long.parseLong(challengesAndTime.get(challengeName))+difference));
                    }
                    /*userSelected.update("challengesCompleted_totalTime",(((Long)document.get("totalTime"))+difference)/(((Long)document.get("challengesCompleted"))+1));*//*Esto lo hara la actividad de escaner en caso de reto completado*/
                    /*userSelected.update("challengesCompleted",((Long)document.get("challengesCompleted"))+1);*//*Esto lo hara la actividad de escaner en caso de reto completado*/
                    userSelected.update("totalTime",((Long)document.get("totalTime"))+difference);
                    userSelected.update("challengesAndTime",challengesAndTime);
                    /*La actividad de escaner deberá actualizar SharedPreferences con lo siguiente (ya que se utiliza en la barra de progreso  -> updateProgressBar(...) , en jugar para marcar con colores y en estadisticas individuales
                    * SharedPreferences myPrefs = getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                                                    SharedPreferences.Editor editor = myPrefs.edit();
                                                    editor.putStringSet("ChallenegesCompleted#"+currentUser.getEmail(),set/array de cadenas de la forma: nombreRetoCompletado#tiempoC);
                                                    editor.apply();
                                                    editor.commit();*/
                }
            }
        });

    }

}
