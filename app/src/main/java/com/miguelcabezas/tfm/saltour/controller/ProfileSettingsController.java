package com.miguelcabezas.tfm.saltour.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcabezas.tfm.saltour.LoginActivity;
import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.dao.DaoFirebase;
import com.miguelcabezas.tfm.saltour.dao.DaoFirebaseImpl;
import com.miguelcabezas.tfm.saltour.dao.DaoSharedPreferences;
import com.miguelcabezas.tfm.saltour.dao.DaoSharedPreferencesImpl;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Clase encargada de manejar los datos del usuario así como sus retos
 * @author Miguel Cabezas Puerto
 *
 * */
public class ProfileSettingsController {
    public ProfileSettingsController(){}
    /**
     * Actualiza la imagen de perfil del usuario llamando al DAO de base de datos
     * @param path Ruta a la imagen de perfil en el repositorio de Google
     * @param mAuth Referencia a la autenticación de usuario
     * @param context contexto desde el que se invoca al método
     */
    public void updatePorfilePic(String path, final FirebaseAuth mAuth, final Context context) {
        DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        daoFirebase.updateProfilePic(path,mAuth,context);
    }
    /**
     * Recupera la imagen de perfil del usuario llamando al DAO de base de datos
     * @param email Dirección de correo del usuario del que se quiere recuperar la foto de perfil
     * @return Referencia a la imagen recuperada
     */
    public StorageReference getProfilePic(String email){
        String path = "images/"+email+".jpg";
        DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        return
                daoFirebase.getPicFromStorage(path);
    }
    /**
     * Establece la foto de perfil del usuario y la transifere a la vista
     * @param currentUser Usuario activo
     * @param context contexto desde el que se invoca al método
     * @param fotoperfil Vista sobre la que transferir la imagen
     */
    public void setProfilePic(FirebaseUser currentUser, final Context context, final ImageView fotoperfil){
        DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        FirebaseStorage storage = daoFirebase.getStorageInstance();
        StorageReference storageRef = daoFirebase.getStorageReference(storage);
        /*final StorageReference pathReference = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");*/
        final StorageReference pathReference = daoFirebase.getPathStorageReference(storageRef,"images/"+ currentUser.getEmail().toString()+".jpg");
        Log.d("URL",pathReference.getDownloadUrl().toString());

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Log.d("URL", downloadUrl.toString());
                Picasso.with(context).load(downloadUrl).into(fotoperfil);
            }
        });
    }

    /**
     * Actualiza la contraseña del usuario llamando al DAO de base de datos
     * @param nuevaContrasena Nueva contraseña a establecer
     * @param antiguaContrasena Contraseña a cambiar
     * @param mAuth Referencia a la autenticación de usuario
     * @param context contexto desde el que se invoca al método
     * @param activity actividad desde la que se invoca al método
     */
    public void updateUserPassword(final String nuevaContrasena, String antiguaContrasena, final FirebaseAuth mAuth, final Context context, final Activity activity){
        if(nuevaContrasena!=null && !nuevaContrasena.isEmpty()){
            final FirebaseUser currentUser = mAuth.getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(currentUser.getEmail(), antiguaContrasena);
            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
                                daoFirebase.updateUserPassword(currentUser,nuevaContrasena).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context,context.getString(R.string.password_actualizada),Toast.LENGTH_LONG).show();
                                            Intent intent=new Intent(context, LoginActivity.class);
                                            activity.startActivity(intent);
                                            mAuth.signOut();
                                            activity.finish();
                                        } else {
                                            Toast.makeText(context,context.getString(R.string.password_no_actualizada),Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(context,context.getString(R.string.error_autenticacion),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    /**
     * Actualiza el nickname del usuario llamando al DAO de base de datos
     * @param nuevoUsuario Nuevo nickname a establecer
     * @param mAuth Referencia a la autenticación de usuario
     */
    public void updateUserName(String nuevoUsuario, FirebaseAuth mAuth){
        if(nuevoUsuario!=null && !nuevoUsuario.isEmpty()){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nuevoUsuario)
                    .build();
            DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
            daoFirebase.updateUserProfile(profileUpdates,mAuth);
        }
    }

    /**
     * Actualiza las shared preferences del usuario llamando al DAO
     * @param emailUser Email del usuario a actualizar
     * @param context contexto desde el que se invoca al método
     */
    public void updateSharedPreferences(final String emailUser, final Context context){
        DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        FirebaseFirestore db = daoFirebase.getDatabaseInstance();
        CollectionReference dbUsers = daoFirebase.getCollectionReference(db,"users");

        /*final DocumentReference userSelected = db.collection("users").document(emailUser);*/
        final DocumentReference userSelected = daoFirebase.getDocumentReference("users",emailUser,db);

        userSelected.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    Map<String,String> challengesAndTime = (Map<String, String>) document.get("challengesAndTime");
                    Set<String> set=new HashSet<>();
                    for (Map.Entry<String, String> entry : challengesAndTime.entrySet()) {
                        Log.e(entry.getKey(), String.valueOf(entry.getValue()));
                        set.add(entry.getKey()+"#"+String.valueOf(entry.getValue()));
                    }
                    /*SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+emailUser, 0);*/
                    DaoSharedPreferencesImpl daoSharedPreferences = new DaoSharedPreferencesImpl();
                    SharedPreferences myPrefs = daoSharedPreferences.getChallengesCompletedSharedPreferences("ChallenegesCompleted#"+emailUser,context);
                    /*SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putStringSet("ChallenegesCompleted#"+emailUser,set);
                    editor.apply();
                    editor.commit();*/
                    daoSharedPreferences.updateChallengesCompleted(myPrefs, "ChallenegesCompleted#"+emailUser, set);
                    Toast.makeText(context,context.getString(R.string.tiempo_detenido),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Actualiza los retos completados del usuario en base de datos una vez termina uno llamando al DAO. Posteriormente lanza la acción para mostrar la realidad aumentada
     * @param emailUser Email del usuario a actualizar
     * @param challengeName Nombre del reto a actualizar
     * @param context contexto desde el que se invoca al método
     * @param activity actividad desde la que se invoca al método
     */
    public void updateUserChallengesAndProcessAR(final String emailUser,final String challengeName, final Context context, final Activity activity){
        final DaoFirebaseImpl daoFirebase = new DaoFirebaseImpl();
        FirebaseFirestore db = daoFirebase.getDatabaseInstance();
        CollectionReference dbUsers = daoFirebase.getCollectionReference(db,"users");

        final DocumentReference userSelected = daoFirebase.getDocumentReference("users",emailUser,db);

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
                    /*Revisar totalTime*/
                    /*userSelected.update("challengesCompleted_totalTime",(((Long)document.get("totalTime")))/(((Long)document.get("challengesCompleted"))+1));
                    userSelected.update("challengesCompleted",((Long)document.get("challengesCompleted"))+1);
                    userSelected.update("challengesAndTime",challengesAndTime);*/
                    daoFirebase.updateDocumentField(userSelected,"challengesCompleted_totalTime",(((Long)document.get("totalTime")))/(((Long)document.get("challengesCompleted"))+1));
                    daoFirebase.updateDocumentField(userSelected,"challengesCompleted",((Long)document.get("challengesCompleted"))+1);
                    daoFirebase.updateDocumentField(userSelected,"challengesAndTime",challengesAndTime);

                    /*La actividad de escaner deberá actualizar SharedPreferences con lo siguiente (ya que se utiliza en la barra de progreso  -> updateProgressBar(...) , en jugar para marcar con colores y en estadisticas individuales*/
                    /*SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+emailUser, 0);*/
                    DaoSharedPreferencesImpl daoSharedPreferences = new DaoSharedPreferencesImpl();
                    SharedPreferences myPrefs = daoSharedPreferences.getChallengesCompletedSharedPreferences("ChallenegesCompleted#"+emailUser,context);
                    daoSharedPreferences.updateChallengesCompleted(myPrefs,"ChallenegesCompleted#"+emailUser,set);
                    /*SharedPreferences.Editor editor = myPrefs.edit();
                    editor.putStringSet("ChallenegesCompleted#"+emailUser,set);
                    editor.apply();
                    editor.commit();*/
                    Log.d("#################","RETO GUARDADO COMPLETADO");
                    Toast.makeText(context,challengeName+" COMPLETADO",Toast.LENGTH_LONG).show();


                    /*AQUI LLAMAR A AR QUE DE DATOS DEL SITIO*/
                    Log.e("#################","TO AR");
                    Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage("com.DefaultCompany.CharacterTalking");
                    LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivityForResult(LaunchIntent,2);
                }
            }
        });
    }
}
