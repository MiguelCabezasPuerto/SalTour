package com.miguelcabezas.tfm.saltour.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcabezas.tfm.saltour.ProfileActivity;
import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.dao.DaoFirebaseImpl;
import com.miguelcabezas.tfm.saltour.model.User;
import com.miguelcabezas.tfm.saltour.utils.SalLib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase encargada de recuperar las puntuaciones de los usuarios y establecer una clasificación
 * @author Miguel Cabezas Puerto
 *
 * */
public class RankingController {
    /**
     * Recupera de forma ordenada ascendente el campo el documento asociado a la relación entre el número de retos completados y el tiempo total empleado en todos los retos.
     * Posteriormente establece el nickname de cada jugador en las tres primeras posiciones y la posición propia del jugador de tal forma que si este ocupa una de estas tres primeras posiciones, lo cuadra en una de ellas marcándolo con un distintivo
     * @param context contexto desde el que se invoca al método
     * @param activity actividad desde la que se invoca al método
     * @param t_jugador1 referencia al cuadro de texto del nombre del jugador en primera posición
     * @param t_jugador2 referencia al cuadro de texto del nombre del jugador en segunda posición
     * @param t_jugador3 referencia al cuadro de texto del nombre del jugador en tercera posición
     * @param t_jugador referencia al cuadro de texto del nombre del propio jugador
     * @param desafios_jugador_1 referencia al cuadro de texto del número de desafíos completados del jugador en primera posición
     * @param desafios_jugador_2 referencia al cuadro de texto del número de desafíos completadose del jugador en segunda posición
     * @param desafios_jugador_3 referencia al cuadro de texto del número de desafíos completados del jugador en tercera posición
     * @param desafios_jugador referencia al cuadro de texto del número de desafíos completados del propio jugador
     * @param tiempo_jugador_1 referencia al cuadro de texto del tiempo empleado del jugador en primera posición
     * @param tiempo_jugador_2 referencia al cuadro de texto del tiempo empleado del jugador en segunda posición
     * @param tiempo_jugador_3 referencia al cuadro de texto del tiempo empleado del jugador en tercera posición
     * @param tiempo_jugador referencia al cuadro de texto del tiempo empleado  del propio jugador
     * @param t_activos referencia al cuadro de texto del número de jugadores activos en la aplicación
     * @param c_jugador1 referencia al cardview del jugador en primera posición
     * @param c_jugador2 referencia al cardview  del jugador en segunda posición
     * @param c_jugador3 referencia al cardview del jugador en tercera posición
     * @param c_jugador referencia al cardview del propio jugador
     *  @param ver_jugador1 referencia al botón para ver el perfil del jugador en primera posición
     *  @param ver_jugador2 referencia al botón para ver el perfil del jugador en segunda posición
     *  @param ver_jugador3 referencia al botón para ver el perfil del jugador en tercera posición
     */
    public void processRanking(final Context context, final Activity activity, final TextView t_jugador1, final TextView t_jugador2, final TextView t_jugador3, final TextView t_jugador, final TextView desafios_jugador_1, final TextView desafios_jugador_2, final TextView desafios_jugador_3, final TextView desafios_jugador,
                               final TextView tiempo_jugador_1, final TextView tiempo_jugador_2, final TextView tiempo_jugador_3, final TextView tiempo_jugador, final TextView t_activos, final CardView c_jugador1, final CardView c_jugador2, final CardView c_jugador3, final CardView c_jugador,
                               final ImageView ver_jugador1, final ImageView ver_jugador2, final ImageView ver_jugador3){
        final ArrayList<User> users = new ArrayList<>();
        DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        FirebaseFirestore db = daoFirebase.getDatabaseInstance();
        CollectionReference usersRef = daoFirebase.getCollectionReference(db,"users");
        usersRef.orderBy("challengesCompleted_totalTime")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean tengoRetosCompletados = false;
                            int pos = 0;
                            int ownPosition = 0;
                            User ownUser = new User();
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("challengesCompleted_totalTime") != null){
                                    if(pos <=2){
                                        User user = new User();
                                        if(document.getData().get("email") != null){
                                            user.setEmail(document.getData().get("email").toString());
                                        }
                                        if(document.getData().get("challengesCompleted") != null){
                                            user.setChallengesCompleted(Integer.parseInt(document.getData().get("challengesCompleted").toString()));
                                        }
                                        if(document.getData().get("totalTime") != null){
                                            user.setTotalTime(Long.parseLong(document.getData().get("totalTime").toString()));
                                        }
                                    /*if(document.getData().get("challengesTime") != null){
                                        ArrayList<Long> newList = new ArrayList<Long>((Integer) document.getData().get("challengesTime"));
                                        user.setChallengesTime(newList);
                                    }*/
                                        if(document.getData().get("challengesCompleted_totalTime") != null){
                                            user.setChallengesCompleted_totalTime(Float.parseFloat(document.getData().get("challengesCompleted_totalTime").toString()));
                                        }
                                        if(document.getData().get("challengesAndTime") !=null ){
                                            Map<String,Long> challengesAndTime = new HashMap<>();
                                            Map<String,String> hashMap = (Map<String,String>) document.getData().get("challengesAndTime");
                                            for (Map.Entry<String,String> entry : hashMap.entrySet()) {
                                                Log.e("ITEM","clave=" + entry.getKey() + ", valor=" + entry.getValue());
                                                if(SalLib.getLastCharacter(entry.getValue()).equalsIgnoreCase("C")){
                                                    challengesAndTime.put(entry.getKey(), SalLib.parseStringToLongSpecial(entry.getValue()));
                                                }
                                            }
                                            user.setChallengesAndTime(challengesAndTime);
                                        }
                                        users.add(user);
                                    }
                                    if(document.getData().get("email") != null && document.getData().get("email").toString().equalsIgnoreCase(currentUser.getEmail().toString())){
                                        ownPosition = pos;
                                        tengoRetosCompletados = true;
                                        ownUser.setEmail(document.getData().get("email").toString());
                                        if(document.getData().get("challengesCompleted") != null){
                                            ownUser.setChallengesCompleted(Integer.parseInt(document.getData().get("challengesCompleted").toString()));
                                        }
                                        if(document.getData().get("totalTime") != null){
                                            ownUser.setTotalTime(Long.parseLong(document.getData().get("totalTime").toString()));
                                        }
                                        if(document.getData().get("challengesCompleted_totalTime") != null){
                                            ownUser.setChallengesCompleted_totalTime(Float.parseFloat(document.getData().get("challengesCompleted_totalTime").toString()));
                                        }

                                    }
                                    pos++;
                                }
                            }

                            Log.e("TAM USERS", String.valueOf(users.size()));
                            SharedPreferences myPrefs = context.getSharedPreferences("ActiveUsers", 0);
                            long activeUsers = myPrefs.getLong("ActiveUsers",100);
                            if(tengoRetosCompletados){
                                ownPosition = ownPosition +1;
                            }





                            if (users != null && !users.isEmpty()){
                                if(users.size()==1){

                                    t_jugador1.setText("1)"+users.get(0).getEmail());

                                    t_jugador.setText(ownPosition+")" +ownUser.getEmail());
                                    desafios_jugador.setText(ownUser.getChallengesCompleted()+" " );
                                    tiempo_jugador.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));

                                    desafios_jugador_1.setText(users.get(0).getChallengesCompleted()+" " );
                                    tiempo_jugador_1.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(0).getChallengesCompleted_totalTime())));

                                    t_jugador.setTypeface(null, Typeface.BOLD);
                                    t_activos.setText("Jugadores activos: "+activeUsers);
                                    c_jugador2.setVisibility(View.INVISIBLE);
                                    c_jugador3.setVisibility(View.INVISIBLE);
                                }else if(users.size()==2){
                                    t_jugador1.setText("1)"+users.get(0).getEmail());
                                    desafios_jugador_1.setText( users.get(0).getChallengesCompleted()+" "  );
                                    tiempo_jugador_1.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(0).getChallengesCompleted_totalTime())));
                                    t_jugador2.setText("2)"+users.get(1).getEmail());
                                    desafios_jugador_2.setText( users.get(1).getChallengesCompleted()+" "  );
                                    tiempo_jugador_2.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(1).getChallengesCompleted_totalTime())));
                                    t_jugador.setText(ownPosition+")" +ownUser.getEmail());
                                    desafios_jugador.setText(ownUser.getChallengesCompleted()+" " );
                                    tiempo_jugador.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                    t_jugador.setTypeface(null, Typeface.BOLD);
                                    t_activos.setText(context.getString(R.string.jugadores_activos)+" "+activeUsers);
                                    c_jugador3.setVisibility(View.INVISIBLE);
                                }else{
                                    t_jugador1.setText("1)"+users.get(0).getEmail());
                                    desafios_jugador_1.setText(users.get(0).getChallengesCompleted()+" " );
                                    tiempo_jugador_1.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(0).getChallengesCompleted_totalTime())));
                                    t_jugador2.setText("2)"+users.get(1).getEmail());
                                    desafios_jugador_2.setText( users.get(1).getChallengesCompleted()+" "  );
                                    tiempo_jugador_2.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(1).getChallengesCompleted_totalTime())));
                                    t_jugador3.setText("3)"+users.get(2).getEmail());
                                    desafios_jugador_3.setText( users.get(2).getChallengesCompleted()+" "  );
                                    tiempo_jugador_3.setText(String.valueOf(SalLib.convertToHHMMSS((long)users.get(2).getChallengesCompleted_totalTime())));
                                    t_jugador.setText(ownPosition+")" +ownUser.getEmail());
                                    desafios_jugador.setText(ownUser.getChallengesCompleted()+" " );
                                    tiempo_jugador.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                    t_jugador.setTypeface(null, Typeface.BOLD);
                                    t_activos.setText(context.getString(R.string.jugadores_activos)+" "+activeUsers);
                                    c_jugador.setCardBackgroundColor(Color.LTGRAY);
                                }
                                switch (ownPosition){
                                    case 0:
                                        c_jugador.setVisibility(View.INVISIBLE);
                                        break;
                                    case 1:
                                        t_jugador.setText("");
                                        c_jugador.setVisibility(View.INVISIBLE);
                                        t_jugador1.setText(ownPosition+")" +ownUser.getEmail());
                                        desafios_jugador_1.setText(ownUser.getChallengesCompleted()+" " );
                                        tiempo_jugador_1.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                        t_jugador1.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText(context.getString(R.string.jugadores_activos)+" "+activeUsers);
                                        c_jugador1.setCardBackgroundColor(Color.LTGRAY);
                                        break;
                                    case 2:
                                        t_jugador.setText("");
                                        c_jugador.setVisibility(View.INVISIBLE);
                                        t_jugador2.setText(ownPosition+")" +ownUser.getEmail());
                                        desafios_jugador_2.setText(ownUser.getChallengesCompleted()+" " );
                                        tiempo_jugador_2.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                        t_jugador2.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText(context.getString(R.string.jugadores_activos)+" "+activeUsers);
                                        c_jugador2.setCardBackgroundColor(Color.LTGRAY);
                                        break;
                                    case 3:
                                        t_jugador.setText("");
                                        c_jugador.setVisibility(View.INVISIBLE);
                                        t_jugador3.setText(ownPosition+")" +ownUser.getEmail());
                                        desafios_jugador_3.setText(ownUser.getChallengesCompleted()+" " );
                                        tiempo_jugador_3.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                        t_jugador3.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText(context.getString(R.string.jugadores_activos)+" "+activeUsers);
                                        c_jugador3.setCardBackgroundColor(Color.LTGRAY);
                                        break;
                                    default:
                                        break;
                                }

                                /*Llamar a un fragment pasando el email, retos, posicion y retos completados y los despliegue*/
                                ver_jugador1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("VER JUGADOR 1", users.get(0).getEmail());
                                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                                        profileIntent.putExtra("profileEmail",users.get(0).getEmail());
                                        profileIntent.putExtra("profilePosition",1);
                                        profileIntent.putExtra("profileNumChallenges",users.get(0).getChallengesCompleted());
                                        profileIntent.putExtra("hashmap", (Serializable) users.get(0).getChallengesAndTime());
                                        profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivityForResult(profileIntent,1);
                                    }
                                });
                                ver_jugador2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("VER JUGADOR 2", users.get(1).getEmail());
                                        Intent profileIntent = new Intent(context, ProfileActivity.class);
                                        profileIntent.putExtra("profileEmail",users.get(1).getEmail());
                                        profileIntent.putExtra("profilePosition",2);
                                        profileIntent.putExtra("profileNumChallenges",users.get(1).getChallengesCompleted());
                                        profileIntent.putExtra("hashmap",(Serializable) users.get(1).getChallengesAndTime());
                                        profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivityForResult(profileIntent,1);
                                    }
                                });
                                ver_jugador3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.e("VER JUGADOR 3", users.get(2).getEmail());
                                        Intent profileIntent = new Intent(context,ProfileActivity.class);
                                        profileIntent.putExtra("profileEmail",users.get(2).getEmail());
                                        profileIntent.putExtra("profilePosition",3);
                                        profileIntent.putExtra("profileNumChallenges",users.get(2).getChallengesCompleted());
                                        profileIntent.putExtra("hashmap",(Serializable) users.get(2).getChallengesAndTime());
                                        profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivityForResult(profileIntent,1);
                                    }
                                });


                            }
                        } else {
                            Log.e("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
