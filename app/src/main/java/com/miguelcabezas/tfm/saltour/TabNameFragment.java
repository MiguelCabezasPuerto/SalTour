package com.miguelcabezas.tfm.saltour;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.miguelcabezas.tfm.saltour.model.User;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPumpRanking;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
            t_jugador1 = layout.findViewById(R.id.ranking_top1);
            t_jugador2 = layout.findViewById(R.id.ranking_top2);
            t_jugador3 = layout.findViewById(R.id.ranking_top3);
            t_jugador = layout.findViewById(R.id.ranking_jugador);
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
                                int pos = 0;
                                int ownPosition = 0;
                                User ownUser = new User();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                for (QueryDocumentSnapshot document : task.getResult()) {
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
                                        users.add(user);
                                    }
                                    if(document.getData().get("email") != null && document.getData().get("email").toString().equalsIgnoreCase(currentUser.getEmail().toString())){
                                        ownPosition = pos;
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

                                Log.e("TAM USERS", String.valueOf(users.size()));
                                SharedPreferences myPrefs = getContext().getSharedPreferences("ActiveUsers", 0);
                                long activeUsers = myPrefs.getLong("ActiveUsers",100);
                                ownPosition = ownPosition +1;
                                if (users != null && !users.isEmpty()){
                                    if(users.size()==1){
                                        t_jugador1.setText("1)"+users.get(0).getEmail() + "|"+ users.get(0).getChallengesCompleted() + "|" + users.get(0).getTotalTime());
                                        t_jugador.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                        t_jugador.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText("Jugadores activos: "+activeUsers);
                                    }else if(users.size()==2){
                                        t_jugador1.setText("1)"+users.get(0).getEmail() + "|"+ users.get(0).getChallengesCompleted() + "|" + users.get(0).getTotalTime());
                                        t_jugador2.setText("2)"+users.get(1).getEmail() + "|"+ users.get(1).getChallengesCompleted() + "|" + users.get(1).getTotalTime());
                                        t_jugador.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                        t_jugador.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText("Jugadores activos: "+activeUsers);
                                    }else{
                                        t_jugador1.setText("1)"+users.get(0).getEmail() + "|"+ users.get(0).getChallengesCompleted() + "|" + users.get(0).getTotalTime());
                                        t_jugador2.setText("2)"+users.get(1).getEmail() + "|"+ users.get(1).getChallengesCompleted() + "|" + users.get(1).getTotalTime());
                                        t_jugador3.setText("3)"+users.get(2).getEmail() + "|"+ users.get(2).getChallengesCompleted() + "|" + users.get(2).getTotalTime());
                                        t_jugador.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                        t_jugador.setTypeface(null, Typeface.BOLD);
                                        t_activos.setText("Jugadores activos: "+activeUsers);
                                    }
                                    switch (ownPosition){
                                        case 1:
                                            t_jugador.setText("");
                                            t_jugador1.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                            t_jugador1.setTypeface(null, Typeface.BOLD);
                                            t_activos.setText("Jugadores activos: "+activeUsers);
                                            break;
                                        case 2:
                                            t_jugador.setText("");
                                            t_jugador2.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                            t_jugador2.setTypeface(null, Typeface.BOLD);
                                            t_activos.setText("Jugadores activos: "+activeUsers);
                                            break;
                                        case 3:
                                            t_jugador.setText("");
                                            t_jugador3.setText(ownPosition+")" +ownUser.getEmail() + "|" + ownUser.getChallengesCompleted() + "|" + ownUser.getTotalTime());
                                            t_jugador3.setTypeface(null, Typeface.BOLD);
                                            t_activos.setText("Jugadores activos: "+activeUsers);
                                            break;
                                        default:
                                            break;
                                    }


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
