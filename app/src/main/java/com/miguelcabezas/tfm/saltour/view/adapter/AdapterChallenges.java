package com.miguelcabezas.tfm.saltour.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.model.Challenge;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class AdapterChallenges extends RecyclerView.Adapter<AdapterChallenges.ViewHolder>{
    private ArrayList<Map<String,GeoPoint>> mDataSet;
    Context context;
    ArrayList<Challenge>allChallenges;

    // Obtener referencias de los componentes visuales para cada elemento
    // Es decir, referencias de los EditText, TextViews, Buttons
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // en este ejemplo cada elemento consta solo de un título
        public CardView cardView;
        public ViewHolder(CardView tv) {
            super(tv);
            /* super(v);*/
            cardView = tv;
        }
    }

    // Este es nuestro constructor (puede variar según lo que queremos mostrar)
    public AdapterChallenges(ArrayList<Map<String,GeoPoint>> myDataSet,Context context,ArrayList<Challenge>challenges) {
        mDataSet = myDataSet;
        this.context = context;
        this.allChallenges = new ArrayList<>();
        this.allChallenges.addAll(challenges);
    }

    // El layout manager invoca este método
    // para renderizar cada elemento del RecyclerView
    @Override
    public AdapterChallenges.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // Creamos una nueva vista
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_challenge_item, parent, false);

        // Aquí podemos definir tamaños, márgenes, paddings
        // ...

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Este método reemplaza el contenido de cada view,
    // para cada elemento de la lista (nótese el argumento position)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - obtenemos un elemento del dataset según su posición
        // - reemplazamos el contenido de los views según tales datos
        TextView textView;
        ImageButton btnPlay;
        textView = holder.cardView.findViewById(R.id.t_challenge_name);
        btnPlay = holder.cardView.findViewById(R.id.b_play);
        btnPlay.setId(position);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
        Set<String> challengesAndTime = myPrefs.getStringSet("ChallenegesCompleted#"+currentUser.getEmail(),null);
        Set<String> keysChallenges = mDataSet.get(position).keySet();
        for(String challenge : challengesAndTime){
            String par[]=challenge.split("#");
            String time = par[1];
            String normalized = Normalizer.normalize(par[0], Normalizer.Form.NFD);
            String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
            String asciiLower = ascii.toLowerCase();
            String normalized2 = Normalizer.normalize(mDataSet.get(position).keySet().toArray()[0].toString(), Normalizer.Form.NFD);
            String ascii2 = normalized2.replaceAll("[^\\p{ASCII}]", "");
            String asciiLower2 = ascii2.toLowerCase();
            if(asciiLower2.contains(asciiLower)){
                if(par[1].contains("C")){
                    holder.cardView.setCardBackgroundColor(Color.GREEN);
                    btnPlay.setImageResource(R.drawable.check_icon);
                    btnPlay.setEnabled(false);
                    btnPlay.setBackgroundColor(Color.GREEN);
                    btnPlay.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                }else{
                    holder.cardView.setCardBackgroundColor(Color.YELLOW);
                    btnPlay.setBackgroundColor(Color.YELLOW);
                    btnPlay.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                }
            }
        }






        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Boton del reto", allChallenges.get(v.getId()).getName());
            }
        });
        textView.setText(mDataSet.get(position).keySet().toArray()[0].toString());
    }

    // Método que define la cantidad de elementos del RecyclerView
    // Puede ser más complejo (por ejemplo si implementamos filtros o búsquedas)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
