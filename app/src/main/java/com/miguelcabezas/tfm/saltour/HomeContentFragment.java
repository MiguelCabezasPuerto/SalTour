package com.miguelcabezas.tfm.saltour;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.miguelcabezas.tfm.saltour.controller.service.CountTimeService;
import com.miguelcabezas.tfm.saltour.model.ActiveChallengeSingleton;
import com.miguelcabezas.tfm.saltour.model.Challenge;
import com.miguelcabezas.tfm.saltour.utils.EnumRetos;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPump;
import com.miguelcabezas.tfm.saltour.view.adapter.AdapterChallenges;
import com.miguelcabezas.tfm.saltour.view.adapter.CarrouseelAdapter;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapterHelp;
import com.miguelcabezas.tfm.saltour.view.animation.ProgressBarAnimation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.app.Activity.RESULT_OK;


public class HomeContentFragment extends Fragment {

  private static final String TEXT = "text";
  private static final String USER = "user";
  String token = "";
  String tokenanterior = "";
  private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private GoogleMap mapa;

   private GoogleSignInClient mGoogleSignInClient;
   private GoogleSignInOptions googleSignInOptions;

  public static HomeContentFragment newInstance(String text,String user) {
    HomeContentFragment frag = new HomeContentFragment();

    Bundle args = new Bundle();
    args.putString(TEXT, text);
    args.putString(USER,user);
    frag.setArguments(args);




    return frag;
  }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 50);
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 50);
            }
        }
    }


  @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable
          Bundle savedInstanceState) {
      final FirebaseAuth mAuth;
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

      GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
      mGoogleSignInClient = GoogleSignIn.getClient(getContext(),gso);

    if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_jugar))){
       layout = inflater.inflate(R.layout.jugar_fragment, container, false);
       Button btnEscanear = layout.findViewById(R.id.boton_escanear);
       Button btnParar = layout.findViewById(R.id.boton_parar);

        if(isAServiceRunning(CountTimeService.class)){
            btnParar.setVisibility(View.VISIBLE);
            btnParar.setEnabled(true);
        }else{
            btnParar.setVisibility(View.INVISIBLE);
            btnParar.setEnabled(false);
        }
       ;
       // int id = R.layout.challenge;
        // ViewGroup layoutChallengeGroup;
       // layoutChallengeGroup = (ViewGroup) layout.findViewById(R.id.content);
        //for(int i=0;i<4;i++){
        final RecyclerView mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view_challenges);
            /*mRecyclerView.setHasFixedSize(true);*/
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);

        final ArrayList<Map<String, GeoPoint>> myDataSet = new ArrayList<>();
        final ArrayList<Challenge> challenges = new ArrayList<>();
        /*final ArrayList<String> myDataSet=new ArrayList<>();*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference challenegesRef = db.collection("challenges");
        final View finalLayout = layout;
        challenegesRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String,GeoPoint> map = new HashMap<>();
                           map.put(document.get("name").toString(), (GeoPoint) document.get("geolocation"));
                           myDataSet.add(map);
                           String lat = String.valueOf(((GeoPoint) document.get("geolocation")).getLatitude());
                           String lon = String.valueOf(((GeoPoint) document.get("geolocation")).getLongitude());
                           challenges.add(new Challenge(document.get("name").toString(),lat,lon));
                        }
                        AdapterChallenges mAdapter = new AdapterChallenges(myDataSet,getContext(),challenges,inflater,container,getActivity(), finalLayout);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });


            /*TextView textView = (TextView) recyclerView.findViewById(R.id.challenge_id);*/
            //textView.setText("Reto "+i);
           /* layoutChallengeGroup.addView(mRecyclerView);*/
       // }
       btnEscanear.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseAuth mAuth = FirebaseAuth.getInstance();
               Fragment fragment = HomeContentFragment.newInstance("QR",mAuth.getCurrentUser().getDisplayName());
               getActivity().getSupportFragmentManager()
                       .beginTransaction()
                       .setCustomAnimations(R.anim.nav_enter, R.anim.nav_exit)
                       .replace(R.id.home_content, fragment)
                       .commit();
           }
       });

       btnParar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("b_parar","Parar el tiempo del reto");
               if(isAServiceRunning(CountTimeService.class)){
                   ActiveChallengeSingleton activeChallengeSingleton = ActiveChallengeSingleton.getInstance();
                   getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                   v.setVisibility(View.INVISIBLE);
                   v.setEnabled(false);
                   FirebaseAuth auth = FirebaseAuth.getInstance();
                   if(!(auth.getCurrentUser().getEmail().equalsIgnoreCase("Invitado@testsaltour.com"))){
                       getActivity().runOnUiThread(esperarYActualizarPreferences(3000,auth.getCurrentUser().getEmail(),activeChallengeSingleton.getName()));
                   }

               }
              /* long startTime = SystemClock.elapsedRealtime();
               long endTime = SystemClock.elapsedRealtime();
               long elapsedMilliSeconds = endTime - startTime;
               double elapsedSeconds = elapsedMilliSeconds / 1000.0;*/
           }
       });

    }else if(getArguments().getString(TEXT).equalsIgnoreCase("QR")){
        layout = inflater.inflate(R.layout.qr_fragment, container, false);
        SurfaceView cameraView = (SurfaceView) layout.findViewById(R.id.camera_view);
        Log.e("QR","Calling initQR");
        initQR(cameraView);

    }else if(getArguments().getString(TEXT).equalsIgnoreCase("Map")){
        Intent intent;
        intent=new Intent(getContext(),MapsActivity.class);
        startActivityForResult(intent,1);
    } else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_perfil))){
        mAuth = FirebaseAuth.getInstance();
        if((mAuth.getCurrentUser().getEmail().equalsIgnoreCase("invitado@testsaltour.com"))){
            Toast.makeText(getContext(),getString(R.string.funcionalidades),Toast.LENGTH_LONG).show();
        }else{
            layout = inflater.inflate(R.layout.perfil_fragment, container, false);
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
        }



    }else if(getArguments().getString(TEXT).equalsIgnoreCase(getString(R.string.menu_ranking))){
        mAuth = FirebaseAuth.getInstance();
        if((mAuth.getCurrentUser().getEmail().equalsIgnoreCase("invitado@testsaltour.com"))){
            Toast.makeText(getContext(),getString(R.string.funcionalidades),Toast.LENGTH_LONG).show();
            return null;
        }
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
        mAuth = FirebaseAuth.getInstance();
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

      FirebaseAuth mAuth = FirebaseAuth.getInstance();
      if((mAuth.getCurrentUser().getEmail().equalsIgnoreCase("invitado@testsaltour.com"))){
          RollPagerView rollPagerView = layout.findViewById(R.id.roll_view_pager);
          rollPagerView.setPlayDelay(3000);
          rollPagerView.setAnimationDurtion(500);
          rollPagerView.setAdapter(new CarrouseelAdapter());
          rollPagerView.setHintView(new ColorPointHintView(getContext(), Color.RED,Color.WHITE));
          progressBar.setVisibility(View.INVISIBLE);
      }else{
          final FirebaseFirestore db = FirebaseFirestore.getInstance();
          CollectionReference challenegesRef = db.collection("challenges");
          challenegesRef.get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          long totalChallenges = task.getResult().size();
                          FirebaseAuth mAuth = FirebaseAuth.getInstance();
                          FirebaseUser currentUser = mAuth.getCurrentUser();
                          bienvenidaUsuario.setText(getString(R.string.bienvenido)+" "+currentUser.getDisplayName());
                          ultimoJuego.setText(getString(R.string.ultima_conexion)+" " + currentUser.getMetadata().getLastSignInTimestamp());
                          tProgreso.setText(getString(R.string.progreso_personal));
                          SharedPreferences myPrefs = getContext().getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                          Set<String> challengesAndTime = myPrefs.getStringSet("ChallenegesCompleted#"+currentUser.getEmail(),null);
                          long challengesCompleted = 0;
                          for(String challenge : challengesAndTime){
                              String [] partes = challenge.split("#");
                              if(partes[1].contains("C")){
                                  challengesCompleted++;
                              }
                          }

                          long percentageCompleted = calculatePercentage(challengesCompleted,totalChallenges);
                          ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, 0, percentageCompleted);
                          anim.setDuration(1000);
                          progressBar.startAnimation(anim);
                          textView.setText(String.valueOf(percentageCompleted)+getString(R.string.porcentaje_retos));

                          RollPagerView rollPagerView = layout.findViewById(R.id.roll_view_pager);
                          rollPagerView.setPlayDelay(3000);
                          rollPagerView.setAnimationDurtion(500);
                          rollPagerView.setAdapter(new CarrouseelAdapter());
                          rollPagerView.setHintView(new ColorPointHintView(getContext(), Color.RED,Color.WHITE));
                      }
                  });
      }
  }

  private long calculatePercentage(long actual,long total){
      return (actual*100)/total;
  }


  private void logout(final FirebaseAuth mAuth) {
        new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.cerrar_sesion))
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mAuth.getCurrentUser()!=null){
                            mAuth.signOut();

                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent=new Intent(getContext(),LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }else{
                                        Intent intent=new Intent(getContext(),LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }
                            });

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
                        Toast.makeText(getContext(),getString(R.string.ocho_caracteres),Toast.LENGTH_LONG).show();
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
                                                    Toast.makeText(getContext(),getString(R.string.password_actualizada),Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(getContext(),LoginActivity.class);
                                                    startActivity(intent);
                                                    mAuth.signOut();
                                                    getActivity().finish();
                                                } else {
                                                    Toast.makeText(getContext(),getString(R.string.password_no_actualizada),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getContext(),getString(R.string.error_autenticacion),Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                LinearLayout editProfilePanel = layout.findViewById(R.id.edit_profile_panel);
                editProfilePanel.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(),getString(R.string.datos_actualizados),Toast.LENGTH_LONG).show();

            }
        });
    }


    private void onClickCompartirOtros(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.te_desafio));
        startActivity(Intent.createChooser(intent, "Share with"));
    }
    private void onClickCompartirFacebook(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.te_desafio));
        intent.setPackage("com.facebook.katana");
        startActivity(intent);
    }
    private void onClickCompartirWhatsApp(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.te_desafio));
        intent.setPackage("com.whatsapp");
        startActivity(intent);
    }
    private void onClickCompartirTwitter(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.te_desafio));
        intent.setPackage("com.twitter.android");
        startActivity(intent);
    }
    private void onClickCompartirCorreo(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,"SalTour");
        emailIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.te_desafio));

        try {
            getContext().startActivity(Intent.createChooser(emailIntent, "Compartir"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),getString(R.string.error_envio),Toast.LENGTH_LONG).show();
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





    public void initQR(final SurfaceView cameraView) {

        // creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getContext())
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // creo la camara

        final CameraSource cameraSource = new CameraSource
                .Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
        Log.e("QR","Camera created");
        // listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // verificamos la version de ANdroid que sea al menos la M para mostrar
                        // el dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                101);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String emailUser= currentUser.getEmail();






                if (barcodes.size() > 0) {

                    // obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();
                    Log.e("QR","token detected "+token);
                    // verificamos que el token anterior no se igual al actual
                    // esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.e("TOKEN#####", token);

                        if (URLUtil.isValidUrl(token)) {
                            ActiveChallengeSingleton activeChallengeSingleton = ActiveChallengeSingleton.getInstance();
                            // si es una URL valida abre el navegador
                            /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(token));
                            startActivity(browserIntent);*/
                            if(token.equalsIgnoreCase("http://en.m.wikipedia.org")){ //callejeros penguins
                                Log.d("RETO","pingüinos");
                                if(activeChallengeSingleton.getName().contains("callejeros")){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(100000,emailUser,activeChallengeSingleton.getName()));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase("https://tendido1.com/wp-content/uploads/2020/12/qrmenu.jpeg")){ //rana
                                Log.d("RETO","rana");
                                if(activeChallengeSingleton.getName().contains("rana")){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(100000,emailUser,activeChallengeSingleton.getName()));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase("https://forms.gle/6151QniEnpo6SNAm7")){ //plaza
                                Log.d("RETO","plaza");
                                if(activeChallengeSingleton.getName().contains("plaza")){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(100000,emailUser,activeChallengeSingleton.getName()));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase("")){ //jardin
                                Log.d("RETO","jardin");
                                if(activeChallengeSingleton.getName().contains("jardín")){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(100000,emailUser,activeChallengeSingleton.getName()));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }
                        }





                        else {
                            ActiveChallengeSingleton activeChallengeSingleton = ActiveChallengeSingleton.getInstance();
                            // QR generados para los retos, aqui llamar a la AR adecuada en funcion del reto que sea y parar el tiempo para ese reto
                            if(token.equalsIgnoreCase(String.valueOf(EnumRetos.rana))){
                                Log.e("RETO","rana");
                                if(activeChallengeSingleton.getName().contains(token)){
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));

                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase(String.valueOf(EnumRetos.plaza))){
                                Log.e("RETO","plaza");
                                if(activeChallengeSingleton.getName().contains(token)){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase(String.valueOf(EnumRetos.jardín))){
                                Log.e("RETO","jardin");
                                if(activeChallengeSingleton.getName().contains(token)){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else if(token.equalsIgnoreCase(String.valueOf(EnumRetos.callejeros))){
                                Log.d("RETO","pingüinos");
                                if(activeChallengeSingleton.getName().contains(token)){
                                    Log.d("Reto","parado");
                                    if(isAServiceRunning(CountTimeService.class)){
                                        getActivity().stopService(new Intent(getContext(), CountTimeService.class));
                                        getActivity().runOnUiThread(esperarYActualizar(100000,emailUser,activeChallengeSingleton.getName()));
                                        getActivity().runOnUiThread(esperarYActualizar(3000,emailUser,activeChallengeSingleton.getName()));
                                    }
                                }else{
                                    Log.e("Reto","No es el reto iniciado");
                                    Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                                }
                            }else{
                                Log.e("Reto","No es el reto iniciado");
                                Toast.makeText(getActivity(),getString(R.string.reto_erroneo),Toast.LENGTH_LONG);
                            }
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                }
            }
        });

    }

    private boolean isAServiceRunning(Class<?> serviceClass) {
      ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
      for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
          if (serviceClass.getName().equals(service.service.getClassName())) {
              return true;
          }
      }
      return false;
  }

  private void updateUserData(final String emailUser, final String challengeName){
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      CollectionReference dbUsers = db.collection("users");

      final DocumentReference userSelected = db.collection("users").document(emailUser);

      /*userSelected.update("challengesCompleted_totalTime",(((Long)document.get("totalTime"))+difference)/(((Long)document.get("challengesCompleted"))+1));*//*Esto lo hara la actividad de escaner en caso de reto completado*/
      /*userSelected.update("challengesCompleted",((Long)document.get("challengesCompleted"))+1);*//*Esto lo hara la actividad de escaner en caso de reto completado*/
      userSelected.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              DocumentSnapshot document = task.getResult();
              if(document.exists()){
                  Map<String,String> challengesAndTime = (Map<String, String>) document.get("challengesAndTime");
                  if(challengesAndTime != null && !challengesAndTime.isEmpty() && challengesAndTime.get(challengeName)!=null){
                      String tiempo = challengesAndTime.get(challengeName);
                      challengesAndTime.put(challengeName,tiempo+"C");
                  }
                  Set<String>set=new HashSet<>();
                  for (Map.Entry<String, String> entry : challengesAndTime.entrySet()) {
                      Log.e(entry.getKey(), String.valueOf(entry.getValue()));
                      set.add(entry.getKey()+"#"+String.valueOf(entry.getValue()));
                  }
                  userSelected.update("challengesCompleted_totalTime",(((Long)document.get("totalTime")))/(((Long)document.get("challengesCompleted"))+1));//*Esto lo hara la actividad de escaner en caso de reto completado*/
                  userSelected.update("challengesCompleted",((Long)document.get("challengesCompleted"))+1);//*Esto lo hara la actividad de escaner en caso de reto completado*/
                  userSelected.update("challengesAndTime",challengesAndTime);
                    /*La actividad de escaner deberá actualizar SharedPreferences con lo siguiente (ya que se utiliza en la barra de progreso  -> updateProgressBar(...) , en jugar para marcar con colores y en estadisticas individuales*/
                    SharedPreferences myPrefs = getContext().getSharedPreferences("ChallenegesCompleted#"+emailUser, 0);
                                                    SharedPreferences.Editor editor = myPrefs.edit();
                                                    editor.putStringSet("ChallenegesCompleted#"+emailUser,set);
                                                    editor.apply();
                                                    editor.commit();
                  Log.d("#################","RETO GUARDADO COMPLETADO");
                  Toast.makeText(getContext(),challengeName+" COMPLETADO",Toast.LENGTH_LONG).show();
                  /*AQUI LLAMAR A AR QUE DE DATOS DEL SITIO*/
                  Log.d("#################","TO AR");
                  Intent LaunchIntent = getContext().getPackageManager().getLaunchIntentForPackage("com.DefaultCompany.CharacterTalking");
                  startActivityForResult(LaunchIntent,2);

              }
          }
      });
  }

    public Runnable esperarYActualizar(final int milisegundos, final String email, final String challengeName) {
      return new Runnable() {
          @Override
          public void run() {
              Handler handler = new Handler();
              handler.postDelayed(new Runnable() {
                  public void run() {
                      Log.e("ACCIONES","TRAS 3000ms");
                      updateUserData(email,challengeName);
                  }
              }, milisegundos);
          }
      };
    }

    public Runnable esperarYActualizarPreferences(final int milisegundos, final String email, final String challengeName) {
        return new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Log.e("ACCIONES","TRAS 3000ms");
                        updateSharedPreferences(email);
                    }
                }, milisegundos);
            }
        };
    }



    private void updateSharedPreferences(final String emailUser){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference dbUsers = db.collection("users");

        final DocumentReference userSelected = db.collection("users").document(emailUser);

        userSelected.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    Map<String,String> challengesAndTime = (Map<String, String>) document.get("challengesAndTime");
                    Set<String>set=new HashSet<>();
                    for (Map.Entry<String, String> entry : challengesAndTime.entrySet()) {
                        Log.e(entry.getKey(), String.valueOf(entry.getValue()));
                        set.add(entry.getKey()+"#"+String.valueOf(entry.getValue()));
                    }
                    SharedPreferences myPrefs = getContext().getSharedPreferences("ChallenegesCompleted#"+emailUser, 0);
                    SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putStringSet("ChallenegesCompleted#"+emailUser,set);
                    editor.apply();
                    editor.commit();
                    Toast.makeText(getContext(),getString(R.string.tiempo_detenido),Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}

