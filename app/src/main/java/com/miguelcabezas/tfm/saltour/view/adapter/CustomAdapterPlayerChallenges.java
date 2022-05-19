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

public class CustomAdapterPlayerChallenges extends BaseAdapter {

    private final Context context;
    private final ArrayList<ChallengeListDTO> challengeListDTOS;

    public CustomAdapterPlayerChallenges(Context context, ArrayList<ChallengeListDTO> challengeListDTOS) {
        this.context = context;
        this.challengeListDTOS = challengeListDTOS;
    }

    @Override
    public int getCount() {
        return challengeListDTOS.size();
    }

    @Override
    public Object getItem(int position) {
        return challengeListDTOS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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
