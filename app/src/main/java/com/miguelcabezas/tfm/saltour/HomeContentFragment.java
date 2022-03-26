package com.miguelcabezas.tfm.saltour;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.adapter.CarrouseelAdapter;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapterHelp;
import com.miguelcabezas.tfm.saltour.view.animation.ProgressBarAnimation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


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
  public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable
          Bundle savedInstanceState) {
      FirebaseAuth mAuth;
      EditText usuario,contrasena,oldContrasena;
      TextView displayName;
      final LinearLayout editProfilePanel;
      final ImageView fotoperfil,editProfile;
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
        editProfile=layout.findViewById(R.id.edit_profile);
        displayName=layout.findViewById(R.id.displayName);
        editProfilePanel=layout.findViewById(R.id.edit_profile_panel);
        displayName.setText(currentUser.getDisplayName().toString());
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

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfilePanel.setVisibility(View.VISIBLE);
            }
        });

        onClickGuardar(layout);

    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_ranking))){
        return cargarEstadisticas(layout,inflater,container);
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
        expandableListAdapter = new CustomExpandableListAdapterHelp(this.getContext(), expandableListTitle, expandableListDetail);
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
       updateProgressBar(layout);
       /*Esto como guia para cargar un fragment desde otro fragment
            AVISO: NO SE ACTUALIZA EL MENU LATERAL, PARA ESTADISTICAS GENERALES DE VER UN PERFIL AJENO SELECCIONADO ESTARIA BIEN*/
       /*ProgressBar progressBar = layout.findViewById(R.id.progress_bar);
       progressBar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               View layoutNew = null;
               FirebaseAuth mAuth = FirebaseAuth.getInstance();
               Fragment fragment = HomeContentFragment.newInstance(getString(R.string.menu_ranking),mAuth.getCurrentUser().getDisplayName());
               getActivity().getSupportFragmentManager()
                       .beginTransaction()
                       .setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                       .replace(R.id.home_content, fragment)
                       .commit();
               *//* DrawerLayout drawerLayout;

               drawerLayout = getActivity().findViewById(R.id.drawer_layout);*//*
           }
       });*/
    }



    /*if (getArguments() != null && layout !=null && !(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_perfil)))) {
      ((TextView) layout.findViewById(R.id.text)).setText(getArguments().getString(TEXT));
    }*/

    return layout;
  }

  private View cargarEstadisticas(View layout,LayoutInflater inflater, ViewGroup container){
      layout = inflater.inflate(R.layout.estadisticas_fragment, container, false);
      setupViewPager(layout);
      setupTabLayout(layout);
      return layout;
  }

  private void updateProgressBar(final View layout){
      final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);
      final TextView textView = (TextView) layout.findViewById(R.id.text_view_progress);
      final TextView bienvenidaUsuario = layout.findViewById(R.id.t_bienvenido);
      final TextView ultimoJuego = layout.findViewById(R.id.t_ultimo_juego);
      final TextView tProgreso = layout.findViewById(R.id.t_progreso);

      final FirebaseFirestore db = FirebaseFirestore.getInstance();
      CollectionReference challenegesRef = db.collection("challenges");
      challenegesRef.get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      long totalChallenges = task.getResult().size();
                      FirebaseAuth mAuth = FirebaseAuth.getInstance();
                      FirebaseUser currentUser = mAuth.getCurrentUser();
                      bienvenidaUsuario.setText("Hola "+currentUser.getDisplayName());
                      ultimoJuego.setText("Última conexión: 15/03/2022");
                      tProgreso.setText("Progreso personal");
                      SharedPreferences myPrefs = getContext().getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                      Set<String> challengesAndTime = myPrefs.getStringSet("ChallenegesCompleted#"+currentUser.getEmail(),null);
                      long challengesCompleted = challengesAndTime.size();
                      long percentageCompleted = calculatePercentage(challengesCompleted,totalChallenges);
                      ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, percentageCompleted);
                      anim.setDuration(1000);
                      progressBar.startAnimation(anim);
                      textView.setText(String.valueOf(percentageCompleted)+"% de los retos");

                      RollPagerView rollPagerView = layout.findViewById(R.id.roll_view_pager);
                      rollPagerView.setPlayDelay(3000);
                      rollPagerView.setAnimationDurtion(500);
                      rollPagerView.setAdapter(new CarrouseelAdapter());
                      rollPagerView.setHintView(new ColorPointHintView(getContext(), Color.RED,Color.WHITE));
                  }
              });


  }

  private long calculatePercentage(long actual,long total){
      return (actual*100)/total;
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


    private void onClickGuardar(final View layout) {
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
                        TextView displayName = layout.findViewById(R.id.displayName);
                        displayName.setText(nuevoUsuario);
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
                LinearLayout editProfilePanel = layout.findViewById(R.id.edit_profile_panel);
                editProfilePanel.setVisibility(View.INVISIBLE);
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

