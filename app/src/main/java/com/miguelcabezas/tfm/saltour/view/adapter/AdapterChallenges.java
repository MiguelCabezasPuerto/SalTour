package com.miguelcabezas.tfm.saltour.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcabezas.tfm.saltour.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Set;


public class AdapterChallenges extends RecyclerView.Adapter<AdapterChallenges.ViewHolder>{
    private ArrayList<String> mDataSet;
    Context context;

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
    public AdapterChallenges(ArrayList<String> myDataSet,Context context) {
        mDataSet = myDataSet;
        this.context = context;
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
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - obtenemos un elemento del dataset según su posición
        // - reemplazamos el contenido de los views según tales datos
        TextView textView;
        textView = holder.cardView.findViewById(R.id.t_challenge_name);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
        Set<String> challengesAndTime = myPrefs.getStringSet("ChallenegesCompleted#"+currentUser.getEmail(),null);
        for(String challenge : challengesAndTime){
            String par[]=challenge.split("#");
            String normalized = Normalizer.normalize(par[0], Normalizer.Form.NFD);
            String ascii = normalized.replaceAll("[^\\p{ASCII}]", "");
            String asciiLower = ascii.toLowerCase();
            String normalized2 = Normalizer.normalize(mDataSet.get(position), Normalizer.Form.NFD);
            String ascii2 = normalized2.replaceAll("[^\\p{ASCII}]", "");
            String asciiLower2 = ascii2.toLowerCase();
            if(asciiLower2.contains(asciiLower)){
                Log.e("Reto",ascii2);
                Log.e("Reto superado",ascii);
                textView.setBackgroundResource(R.color.green);
                Log.e("####","COINCUIDENCIA!");
            }
        }
        textView.setText(mDataSet.get(position));
    }

    // Método que define la cantidad de elementos del RecyclerView
    // Puede ser más complejo (por ejemplo si implementamos filtros o búsquedas)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
