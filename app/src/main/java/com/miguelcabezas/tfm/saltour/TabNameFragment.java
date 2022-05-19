package com.miguelcabezas.tfm.saltour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcabezas.tfm.saltour.model.User;
import com.miguelcabezas.tfm.saltour.utils.SalLib;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPumpRanking;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TabNameFragment extends Fragment {
    private static final String ARG_TAB_NAME = "ARG_TAB_NAME";

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;


    public static TabNameFragment newInstance(@StringRes int tabName) {
        TabNameFragment frg = new TabNameFragment();

        Log.d("tabName", String.valueOf(tabName));

        Bundle args = new Bundle();
        args.putInt(ARG_TAB_NAME, tabName);
        frg.setArguments(args);

        return frg;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View layout = null;
        /*Aqui segun el nombre del tab desplegar un fragment u otro*/


        if( getArguments().getInt(ARG_TAB_NAME) == R.string.estadistica_individual){
            layout = inflater.inflate(R.layout.fragment_tab, container, false);
            expandableListView = (ExpandableListView) layout.findViewById(R.id.expandableListView);
            expandableListDetail = ExpandableListDataPumpRanking.getData(getContext());
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new CustomExpandableListAdapter(this.getContext(), expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });
            expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
                @Override
                public void onGroupCollapse(int groupPosition) {

                }
            });
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    return false;
                }
            });
        }else{
            layout = inflater.inflate(R.layout.fragment_tab_general, container, false);
            final TextView t_jugador1,t_jugador2,t_jugador3,t_jugador,t_activos;
            final TextView desafios_jugador_1, desafios_jugador_2, desafios_jugador_3, desafios_jugador;
            final TextView tiempo_jugador_1, tiempo_jugador_2, tiempo_jugador_3, tiempo_jugador;
            final CardView c_jugador1,c_jugador2,c_jugador3,c_jugador;
            final ImageView ver_jugador1, ver_jugador2, ver_jugador3;

            ver_jugador1 = layout.findViewById(R.id.ver_1);
            ver_jugador2 = layout.findViewById(R.id.ver_2);
            ver_jugador3 = layout.findViewById(R.id.ver_3);

            desafios_jugador_1 = layout.findViewById(R.id.num_desafios1);
            desafios_jugador_2 = layout.findViewById(R.id.num_desafios2);
            desafios_jugador_3 = layout.findViewById(R.id.num_desafios3);
            desafios_jugador = layout.findViewById(R.id.num_desafios);

            tiempo_jugador_1 = layout.findViewById(R.id.num_tiempo1);
            tiempo_jugador_2 = layout.findViewById(R.id.num_tiempo2);
            tiempo_jugador_3 = layout.findViewById(R.id.num_tiempo3);
            tiempo_jugador = layout.findViewById(R.id.num_tiempo);

            t_jugador1 = layout.findViewById(R.id.ranking_top1_text);
            t_jugador2 = layout.findViewById(R.id.ranking_top2_text);
            t_jugador3 = layout.findViewById(R.id.ranking_top3_text);
            t_jugador = layout.findViewById(R.id.ranking_jugador_text);
            c_jugador1 = layout.findViewById(R.id.ranking_top1);
            c_jugador2 = layout.findViewById(R.id.ranking_top2);
            c_jugador3 = layout.findViewById(R.id.ranking_top3);
            c_jugador = layout.findViewById(R.id.ranking_jugador);
            t_activos = layout.findViewById(R.id.ranking_jugadores_activos);
            /*Poner bonito y sacar datos de top3 jugadores, posicion del jugador y numero de jugadores activos de BBDD*/
            final ArrayList<User>users = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");
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
                                SharedPreferences myPrefs = getContext().getSharedPreferences("ActiveUsers", 0);
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
                                        t_activos.setText("Jugadores activos: "+activeUsers);
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
                                        t_activos.setText("Jugadores activos: "+activeUsers);
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
                                            t_activos.setText("Jugadores activos: "+activeUsers);
                                            c_jugador1.setCardBackgroundColor(Color.LTGRAY);
                                            break;
                                        case 2:
                                            t_jugador.setText("");
                                            c_jugador.setVisibility(View.INVISIBLE);
                                            t_jugador2.setText(ownPosition+")" +ownUser.getEmail());
                                            desafios_jugador_2.setText(ownUser.getChallengesCompleted()+" " );
                                            tiempo_jugador_2.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                            t_jugador2.setTypeface(null, Typeface.BOLD);
                                            t_activos.setText("Jugadores activos: "+activeUsers);
                                            c_jugador2.setCardBackgroundColor(Color.LTGRAY);
                                            break;
                                        case 3:
                                            t_jugador.setText("");
                                            c_jugador.setVisibility(View.INVISIBLE);
                                            t_jugador3.setText(ownPosition+")" +ownUser.getEmail());
                                            desafios_jugador_3.setText(ownUser.getChallengesCompleted()+" " );
                                            tiempo_jugador_3.setText(String.valueOf(SalLib.convertToHHMMSS((long)ownUser.getChallengesCompleted_totalTime())));
                                            t_jugador3.setTypeface(null, Typeface.BOLD);
                                            t_activos.setText("Jugadores activos: "+activeUsers);
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
                                            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("profileEmail",users.get(0).getEmail());
                                            profileIntent.putExtra("profilePosition",1);
                                            profileIntent.putExtra("profileNumChallenges",users.get(0).getChallengesCompleted());
                                            profileIntent.putExtra("hashmap", (Serializable) users.get(0).getChallengesAndTime());
                                            startActivityForResult(profileIntent,1);
                                        }
                                    });
                                    ver_jugador2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("VER JUGADOR 2", users.get(1).getEmail());
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("profileEmail",users.get(1).getEmail());
                                            profileIntent.putExtra("profilePosition",2);
                                            profileIntent.putExtra("profileNumChallenges",users.get(1).getChallengesCompleted());
                                            profileIntent.putExtra("hashmap",(Serializable) users.get(1).getChallengesAndTime());
                                            startActivityForResult(profileIntent,1);
                                        }
                                    });
                                    ver_jugador3.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.e("VER JUGADOR 3", users.get(2).getEmail());
                                            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("profileEmail",users.get(2).getEmail());
                                            profileIntent.putExtra("profilePosition",3);
                                            profileIntent.putExtra("profileNumChallenges",users.get(2).getChallengesCompleted());
                                            profileIntent.putExtra("hashmap",(Serializable) users.get(2).getChallengesAndTime());
                                            startActivityForResult(profileIntent,1);
                                        }
                                    });


                                }
                            } else {
                                Log.e("ERROR", "Error getting documents: ", task.getException());
                            }
                        }
                    });









        }


        return layout;
    }
}
