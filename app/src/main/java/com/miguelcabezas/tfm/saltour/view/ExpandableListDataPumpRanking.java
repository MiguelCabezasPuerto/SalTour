package com.miguelcabezas.tfm.saltour.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcabezas.tfm.saltour.model.User;
import com.miguelcabezas.tfm.saltour.utils.EnumRetos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class ExpandableListDataPumpRanking {
    public static HashMap<String, List<String>> getData(Context context) {
        final HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        User user = new User();
        /*Recuperar de BBDD los retos completados con su tiempo*/
        SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
        Set<String> challengesAndTime = myPrefs.getStringSet("ChallenegesCompleted#"+currentUser.getEmail(),null);
        if(challengesAndTime!=null && !challengesAndTime.isEmpty()){
            List<String> items = new ArrayList<>(challengesAndTime);
            List<String>itemsFinal = new ArrayList<>();
            for(String item : items){
                String[]claves = item.split("#");
                String clave = claves[0];
                Log.e("clave", clave);
                if(clave.equalsIgnoreCase(String.valueOf(EnumRetos.JARDIN))){
                    itemsFinal.add("El jardín secreto#"+claves[1]);
                }else if(clave.equalsIgnoreCase(String.valueOf(EnumRetos.PENGUIN))){
                    itemsFinal.add("Los pingüinos callejeros#"+claves[1]);
                }else if(clave.equalsIgnoreCase(String.valueOf(EnumRetos.PLAZA))){
                    itemsFinal.add("Los medallones de la plaza Mayor#"+claves[1]);
                }else if(clave.equalsIgnoreCase(String.valueOf(EnumRetos.RANA))){
                    itemsFinal.add("La rana de Salamanca#"+claves[1]);
                }else{
                    itemsFinal.add(item+"#"+claves[1]);
                }
            }
            expandableListDetail.put("Retos completados("+ items.size()+")", itemsFinal);
        }else{
            expandableListDetail.put("Retos completados(0)", new ArrayList<String>());
        }

        Log.e("List detail",expandableListDetail.toString());
        return expandableListDetail;
    }
}