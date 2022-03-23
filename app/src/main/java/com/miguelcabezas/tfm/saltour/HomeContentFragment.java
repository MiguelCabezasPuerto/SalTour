package com.miguelcabezas.tfm.saltour;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeContentFragment extends Fragment {

  private static final String TEXT = "text";
  private static final String USER = "user";

  public static HomeContentFragment newInstance(String text,String user) {
    HomeContentFragment frag = new HomeContentFragment();

    Bundle args = new Bundle();
    args.putString(TEXT, text);
    args.putString(USER,user);
    frag.setArguments(args);

    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
          Bundle savedInstanceState) {
      FirebaseAuth mAuth;
      EditText usuario,contrasena,oldContrasena;
      final ImageView fotoperfil;
    /*En funcion de la opción de menú pulsada se cargará un layout u otro*/
      ExpandableListView expandableListView;
      ExpandableListAdapter expandableListAdapter;
      List<String> expandableListTitle;
      final HashMap<String, List<String>> expandableListDetail;
        View layout = null;

    if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_jugar))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_perfil))){
       layout = inflater.inflate(R.layout.perfil_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.reload();
        usuario=layout.findViewById(R.id.usuario);
        contrasena=layout.findViewById(R.id.contrasena2);
        oldContrasena=layout.findViewById(R.id.contrasena);
        usuario.setHint((currentUser.getDisplayName().toString()));
        fotoperfil=layout.findViewById(R.id.fotoperfil);
        /*FirebaseFirestore db = FirebaseFirestore.getInstance();*/
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference pathReference = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
        Log.d("URL",pathReference.getDownloadUrl().toString());

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Log.d("URL", downloadUrl.toString());
                Picasso.with(getContext()).load(downloadUrl).into(fotoperfil);
            }
        });

    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_ranking))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_compartir))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_ayuda))){
       layout = inflater.inflate(R.layout.ayuda_fragment, container, false);
        expandableListView = (ExpandableListView) layout.findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
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
    }
    else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_salir))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_home))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }



    /*if (getArguments() != null && layout !=null && !(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_perfil)))) {
      ((TextView) layout.findViewById(R.id.text)).setText(getArguments().getString(TEXT));
    }*/

    return layout;
  }
}

