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
            expandableListDetail.put("Retos completados("+ items.size()+")", items);
        }else{
            expandableListDetail.put("Retos completados(0)", new ArrayList<String>());
        }

        Log.e("List detail",expandableListDetail.toString());
        return expandableListDetail;
    }
}
