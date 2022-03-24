package com.miguelcabezas.tfm.saltour;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
  private ViewPager2 viewPager;
    private TabLayout tabLayout;

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

        onClickGuardar(layout);


    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_ranking))){
       layout = inflater.inflate(R.layout.estadisticas_fragment, container, false);
        setupViewPager(layout);
        setupTabLayout(layout);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_compartir))){
       layout = inflater.inflate(R.layout.compartir_fragment, container, false);
       ImageView btn_whatsapp,btn_facebook,btn_gmail,btn_twitter,btn_compartir_otros;
       btn_whatsapp = layout.findViewById(R.id.btn_whatsapp);
       btn_facebook = layout.findViewById(R.id.btn_facebook);
       btn_twitter = layout.findViewById(R.id.btn_twitter);
       btn_gmail = layout.findViewById(R.id.btn_gmail);
       btn_compartir_otros = layout.findViewById(R.id.btn_compartir_otros);

       btn_whatsapp.setOnClickListener(new View.OnClickListener(){

           @Override
           public void onClick(View v) {
               onClickCompartirWhatsApp();
           }
       });
        btn_facebook.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onClickCompartirFacebook();
            }
        });
        btn_twitter.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onClickCompartirTwitter();
            }
        });
        btn_gmail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onClickCompartirCorreo();
            }
        });
        btn_compartir_otros.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onClickCompartirOtros();
            }
        });


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
        mAuth = FirebaseAuth.getInstance();
        logout(mAuth);
    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_home))){
       layout = inflater.inflate(R.layout.home_fragment, container, false);
    }



    /*if (getArguments() != null && layout !=null && !(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_perfil)))) {
      ((TextView) layout.findViewById(R.id.text)).setText(getArguments().getString(TEXT));
    }*/

    return layout;
  }





  private void logout(final FirebaseAuth mAuth) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("¿Realmente desea cerrar sesión?")
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mAuth.getCurrentUser()!=null){
                            mAuth.signOut();
                            Intent intent=new Intent(getContext(),LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }).show();
    }


    private void onClickGuardar(View layout) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Button btnGuardar = layout.findViewById(R.id.b_guardar_perfil);
        final EditText e_user = layout.findViewById(R.id.usuario);
        final EditText e_old_passwd = layout.findViewById(R.id.contrasena);
        final EditText e_new_passwd = layout.findViewById(R.id.contrasena2);
        btnGuardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String nuevoUsuario=e_user.getText().toString();
                final String nuevaContrasena=e_new_passwd.getText().toString();
                if(nuevoUsuario!=null && !nuevoUsuario.isEmpty()){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nuevoUsuario)
                                .build();
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        currentUser.reload();
                        currentUser.updateProfile(profileUpdates);

                }
                if(nuevaContrasena!=null && !nuevaContrasena.isEmpty()){
                    if(nuevaContrasena.length()<8){
                        Toast.makeText(getContext(),"La nueva contraseña debe contener al menos 8 caracteres",Toast.LENGTH_LONG).show();
                        e_new_passwd.setBackgroundResource(R.drawable.borderojo);
                        return;
                    }
                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), e_old_passwd.getText().toString());
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updatePassword(nuevaContrasena).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getContext(),"Contraseña actualizada, por seguridad, vuelva a entrar",Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(getContext(),LoginActivity.class);
                                                    startActivity(intent);
                                                    mAuth.signOut();
                                                    getActivity().finish();
                                                } else {
                                                    Toast.makeText(getContext(),"Error, contraseña no actualizada",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getContext(),"Error de autenticación",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                    Toast.makeText(getContext(),"Datos actualizados",Toast.LENGTH_LONG).show();

            }
        });
    }


    private void onClickCompartirOtros(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Te desafío a conocer mejor que yo Salamanca en: http://ladespensadelahuertacharra.c1.biz/");
        startActivity(Intent.createChooser(intent, "Share with"));
    }
    private void onClickCompartirFacebook(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Te desafío a conocer mejor que yo Salamanca en: http://ladespensadelahuertacharra.c1.biz/");
        intent.setPackage("com.facebook.katana");
        startActivity(intent);
    }
    private void onClickCompartirWhatsApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Te desafío a conocer mejor que yo Salamanca en: http://ladespensadelahuertacharra.c1.biz//");
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }
    private void onClickCompartirTwitter(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Te desafío a conocer mejor que yo Salamanca en: http://ladespensadelahuertacharra.c1.biz/");
        intent.setPackage("com.twitter.android");
        startActivity(intent);
    }
    private void onClickCompartirCorreo(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"SalTour");
        emailIntent.putExtra(Intent.EXTRA_TEXT,"Te desafío a conocer mejor que yo Salamanca en: http://ladespensadelahuertacharra.c1.biz/");

        try {
            getContext().startActivity(Intent.createChooser(emailIntent, "Compartir"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),"Error en el envío",Toast.LENGTH_LONG).show();
        }
    }


    private void setupViewPager(View layout) {
        viewPager = layout.findViewById(R.id.viewpager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        viewPager.addItemDecoration(dividerItemDecoration);

        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager(),getLifecycle()));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }
    private void setupTabLayout(View layout) {
        tabLayout = layout.findViewById(R.id.tabs);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText(ViewPagerAdapter.Tab.byPosition(position).title);
                        tab.setIcon(ViewPagerAdapter.Tab.byPosition(position).icon);
                    }
                })
                .attach();

    }

}

