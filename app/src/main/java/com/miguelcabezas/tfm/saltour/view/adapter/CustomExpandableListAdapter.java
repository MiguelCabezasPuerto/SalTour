package com.miguelcabezas.tfm.saltour.view.adapter;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.utils.EnumRetos;
import com.miguelcabezas.tfm.saltour.utils.SalLib;

/**
 * Clase que gestiona los desplegables de retos del usuario
 * @author Miguel Cabezas Puerto
 *
 * */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    /**
     * Construye la vista de cada elemento del desplegable
     * @param listPosition posición del elemento en la lista
     * @param isLastChild Si es o no el último elemento
     * @param convertView diseño de la vista a mostrar en cada elemento
     * @param parent vista padre
     * @return Cada elemento del desplegable
     */
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*Aqui la vista de cada elemento para hacerla bonita*/
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        String[]challenge_time=expandedListText.split("#");
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        TextView expandedListTextViewTime = (TextView) convertView.findViewById(R.id.expandedListItemTime);
        expandedListTextView.setText(challenge_time[0]);
        expandedListTextViewTime.setText(SalLib.convertToHHMMSS(Long.parseLong(challenge_time[1])));
        ImageView challengeImageView = convertView.findViewById(R.id.challenge_player_imageview);

        if(challenge_time[0].contains(String.valueOf(EnumRetos.rana))){
            challengeImageView.setImageResource(R.drawable.img_rana);
        }else if(challenge_time[0].contains(String.valueOf(EnumRetos.plaza))){
            challengeImageView.setImageResource(R.drawable.img_medallon);
        }else if(challenge_time[0].contains(String.valueOf(EnumRetos.jardín))){
            challengeImageView.setImageResource(R.drawable.img_jardin);
        }else if(challenge_time[0].contains(String.valueOf(EnumRetos.callejeros))){
            challengeImageView.setImageResource(R.drawable.img_penguins);
        }

        return convertView;
    }

    /**
     * Devuelve el número de elementos dentro de cada elemento de la lista
     * @param listPosition posición del elemento a inspeccionar
     * @return Número de elementos dentro de cada elemento de la lista
     */
    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
