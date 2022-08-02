package com.miguelcabezas.tfm.saltour.view.adapter;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.model.ChallengeListDTO;

import java.util.ArrayList;

/**
 * Clase que gestiona mostrar el listado de jugadores a modo clasificación
 * @author Miguel Cabezas Puerto
 *
 * */
public class CustomAdapterPlayerChallenges extends BaseAdapter {

    private final Context context;
    private final ArrayList<ChallengeListDTO> challengeListDTOS;

    public CustomAdapterPlayerChallenges(Context context, ArrayList<ChallengeListDTO> challengeListDTOS) {
        this.context = context;
        this.challengeListDTOS = challengeListDTOS;
    }

    /**
     * Devuelve el número de elementos del listado a mostrar
     * @return número de elementos del listado
     */
    @Override
    public int getCount() {
        return challengeListDTOS.size();
    }

    /**
     * Devuelve un elemento dentro del listado
     * @param position posición del elemento en el listado
     * @return Elemento dentro de la posición del listado
     */
    @Override
    public Object getItem(int position) {
        return challengeListDTOS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Devuelve un elemento dentro del listado y su información
     * @param position posición del elemento en el listado
     * @param convertView diseño de la vista a mostrar para cada elemento
     * @param parent Vista padre del conjunto de vistas a mostrar (cada elemento)
     * @return Vista a mostrar
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HolderView holderView;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_layout_with_cardview,parent,false);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        }else{
            holderView = (HolderView) convertView.getTag();
        }

        ChallengeListDTO challengeListDTO = challengeListDTOS.get(position);

        holderView.challengeImage.setImageResource(challengeListDTO.getIcon());
        holderView.challengeTime.setText(challengeListDTO.getTime());
        holderView.challengeName.setText(challengeListDTO.getName());

        return convertView;
    }

    /**
     * Clase que obtiene referencias de los componentes visuales para cada elemento, es decir, referencias de los EditText, TextViews, Buttons
     * @author Miguel Cabezas Puerto
     *
     * */
    public static class HolderView{
        private final ImageView challengeImage;
        private final TextView challengeName;
        private final TextView challengeTime;

        public HolderView(View view){
            challengeImage = view.findViewById(R.id.challenge_imageview);
            challengeName = view.findViewById(R.id.challenge_name);
            challengeTime = view.findViewById(R.id.challenge_time);
        }
    }
}
