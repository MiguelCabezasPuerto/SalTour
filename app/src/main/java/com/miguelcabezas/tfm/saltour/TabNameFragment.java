package com.miguelcabezas.tfm.saltour;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPumpRanking;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View layout = null;
        /*Aqui segun el nombre del tab desplegar un fragment u otro*/


        if( getArguments().getInt(ARG_TAB_NAME) == R.string.estadistica_individual){
            layout = inflater.inflate(R.layout.fragment_tab, container, false);
            expandableListView = (ExpandableListView) layout.findViewById(R.id.expandableListView);
            expandableListDetail = ExpandableListDataPumpRanking.getData();
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
            /*Poner bonito y sacar datos de top3 jugadores, posicion del jugador y numero de jugadores activos de BBDD*/
            layout = inflater.inflate(R.layout.fragment_tab_general, container, false);
            TextView t_jugador1,t_jugador2,t_jugador3,t_jugador,t_activos;
            t_jugador1 = layout.findViewById(R.id.ranking_top1);
            t_jugador2 = layout.findViewById(R.id.ranking_top2);
            t_jugador3 = layout.findViewById(R.id.ranking_top3);
            t_jugador = layout.findViewById(R.id.ranking_jugador);
            t_activos = layout.findViewById(R.id.ranking_jugadores_activos);

            t_jugador1.setText("1) illoJuan | 2 retos | 1h 36 min");
            t_jugador2.setText("2) mcp | 2 retos | 1h 56 min");
            t_jugador3.setText("3) carktree | 1 retos | 35 min");
            t_jugador.setText("57) mcp | 1 retos | 1h 05 min");
            t_jugador.setTypeface(null, Typeface.BOLD);
            t_activos.setText("Jugadores activos: 103");
        }


        return layout;
    }
}
