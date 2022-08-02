package com.miguelcabezas.tfm.saltour.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcabezas.tfm.saltour.HomeActivity;
import com.miguelcabezas.tfm.saltour.LoginActivity;
import com.miguelcabezas.tfm.saltour.R;
import com.miguelcabezas.tfm.saltour.RegisterActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Clase encargada de validar los medios de autenticación disponibles en la aplicación
 * @author Miguel Cabezas Puerto
 *
 * */
public class AuthenticatonController {
    public AuthenticatonController(){}

    /**
     * Valida que el botón pulsado para login en la aplicación sea el de entrar como invitado, iniciando el flujo de arranque de la aplicación para este caso.
     * @param mAuth Referencia a laautenticación dde usuario
     * @param context Referencia al contexto de la vista
     * @param usuario Referencia al cuadro de texto de usuario de la vista
     * @param contrasena Referencia al cuadro de texto de contraseña de la vista
     * @param activity Referencia a la actividad desde donde se invoca el método
     * @param progressDialog Referencia al cuadro de progreso de la vista
     */
    public void loginAsGuest(final FirebaseAuth mAuth, final Context context, final EditText usuario, final EditText contrasena, final Activity activity, final ProgressDialog progressDialog){
        mAuth.signInWithEmailAndPassword("Invitado@testsaltour.com","12345678")
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.reload();
                            usuario.setText("");
                            contrasena.setText("");
                            usuario.setBackgroundResource(0);
                            contrasena.setBackgroundResource(0);
                            Intent intent;
                            intent=new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.finish();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Valida que la contraseña introducida sea válida (al menos 8 caracteres) y que esta como el email introducidos existan en la base de datos de usuarios y además coincidan.
     * En caso afirmativo recupera de base de datos la información vinculada al usuario así como los retos que tiene completados y en progreso y los transfiere a las shared preferences de dicho usuario activo
     * @param mAuth Referencia a laautenticación dde usuario
     * @param context Referencia al contexto de la vista
     * @param usuario Referencia al cuadro de texto de usuario de la vista
     * @param contrasena Referencia al cuadro de texto de contraseña de la vista
     * @param activity Referencia a la actividad desde donde se invoca el método
     * @param confirmacion Referencia al cuadro de alertas de la vista
     */
    public void loginWithUserAndPassword(final EditText usuario, final EditText contrasena, final Context context, final ProgressDialog progressDialog, final FirebaseAuth mAuth, final Activity activity, final AlertDialog.Builder confirmacion){
        final String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(context,context.getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(context,context.getString(R.string.introducir_password),Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(context,context.getString(R.string.ocho_caracteres),Toast.LENGTH_LONG).show();
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(context,context.getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage(context.getString(R.string.comprobando_datos));
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(usuario_text,contrasena_text)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.reload();
                            if(currentUser.isEmailVerified()){
                                int pos = usuario_text.indexOf("@");
                                String user=usuario_text.substring(0,pos);
                                usuario.setText("");
                                contrasena.setText("");
                                usuario.setBackgroundResource(0);
                                contrasena.setBackgroundResource(0);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                final CollectionReference usersRef = db.collection("users");
                                usersRef.whereEqualTo("email", currentUser.getEmail())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    ArrayList<String> items = new ArrayList();
                                                    Map<String, String> challengesAnTime = new HashMap<>();
                                                    Set<String> set = new HashSet<>();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.e("Map",document.getData().get("challengesAndTime").toString());
                                                        ObjectMapper oMapper = new ObjectMapper();
                                                        challengesAnTime = oMapper.convertValue(document.getData().get("challengesAndTime"), Map.class);
                                                        for (Map.Entry<String, String> entry : challengesAnTime.entrySet()) {
                                                            Log.e(entry.getKey(), String.valueOf(entry.getValue()));
                                                            set.add(entry.getKey()+"#"+String.valueOf(entry.getValue()));
                                                        }
                                                    }

                                                    SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                                                    SharedPreferences.Editor editor = myPrefs.edit();
                                                    editor.putStringSet("ChallenegesCompleted#"+currentUser.getEmail(),set);
                                                    editor.apply();
                                                    editor.commit();

                                                    usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                SharedPreferences myPrefs = context.getSharedPreferences("ActiveUsers",0);
                                                                SharedPreferences.Editor editor = myPrefs.edit();
                                                                editor.putLong("ActiveUsers",task.getResult().size());
                                                                editor.apply();
                                                                editor.commit();
                                                            }
                                                        }
                                                    });
                                                    Intent intent;
                                                    intent=new Intent(context,HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                    activity.finish();
                                                } else {
                                                    Log.e("ERROR", "Error getting documents: ", task.getException());
                                                    Intent intent;
                                                    intent=new Intent(context,HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    context.startActivity(intent);
                                                    activity.finish();
                                                }
                                            }
                                        });

                            }else{
                                /*AlertDialog.Builder confirmacion = new AlertDialog.Builder(context);
                                confirmacion.setTitle(context.getString(R.string.correo_no_verificado));
                                confirmacion.setMessage(context.getString(R.string.recibir_verificacion));
                                confirmacion.setCancelable(false);
                                confirmacion.setPositiveButton(context.getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context,context.getString(R.string.reenviada_verificacion),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                confirmacion.setNegativeButton(context.getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAuth.signOut();
                                    }
                                });*/
                                confirmacion.setTitle(context.getString(R.string.correo_no_verificado));
                                confirmacion.setMessage(context.getString(R.string.recibir_verificacion));
                                confirmacion.setCancelable(false);
                                confirmacion.setPositiveButton(context.getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(context,context.getString(R.string.reenviada_verificacion),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                confirmacion.setNegativeButton(context.getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAuth.signOut();
                                    }
                                });
                                confirmacion.create();
                                confirmacion.show();
                                usuario.setText("");
                                contrasena.setText("");
                                usuario.setBackgroundResource(0);
                                contrasena.setBackgroundResource(0);
                                mAuth.signOut();
                            }


                        }else{
                            Toast.makeText(context,context.getString(R.string.credenciales_incorrectas),Toast.LENGTH_LONG).show();
                            usuario.setText("");
                            contrasena.setText("");
                            usuario.setBackgroundResource(R.drawable.borderojo);
                            contrasena.setBackgroundResource(R.drawable.borderojo);
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    /**
     * Valida la correción del método de login mediante cuenta de Google
     * @param mAuth Referencia a laautenticación dde usuario
     * @param context Referencia al contexto de la vista
     * @param data Datos de usuario
     * @param activity Referencia a la actividad desde donde se invoca el método
     */
    public void loginWWithGoogleAccount(Intent data, Context context,final FirebaseAuth mAuth,final Activity activity){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        if(task.isSuccessful()){
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken(),mAuth,context, activity);
            }catch (ApiException e){
                Toast.makeText(context,"Google sign in fail",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,"Ocurrió un error. "+task.getException().toString(),Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Valida que las credenciales de Google a partir del token sean válidas
     * En caso afirmativo recupera de base de datos la información vinculada al usuario así como los retos que tiene completados y en progreso y los transfiere a las shared preferences de dicho usuario activo
     * @param mAuth Referencia a laautenticación dde usuario
     * @param context Referencia al contexto de la vista
     * @param idToken Token de acceso a la cuenta de Google
     * @param activity Referencia a la actividad desde donde se invoca el método
     */
    private void firebaseAuthWithGoogle(String idToken, final FirebaseAuth mAuth, final Context context, final Activity activity){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.reload();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            final CollectionReference usersRef = db.collection("users");
                            usersRef.whereEqualTo("email", currentUser.getEmail())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("TAG","EXISTE EN BBDD");
                                                ArrayList<String> items = new ArrayList();
                                                Map<String, String> challengesAnTime = new HashMap<>();
                                                Set<String>set = new HashSet<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.e("Map",document.getData().get("challengesAndTime").toString());
                                                    ObjectMapper oMapper = new ObjectMapper();
                                                    challengesAnTime = oMapper.convertValue(document.getData().get("challengesAndTime"), Map.class);
                                                    for (Map.Entry<String, String> entry : challengesAnTime.entrySet()) {
                                                        Log.e(entry.getKey(), String.valueOf(entry.getValue()));
                                                        set.add(entry.getKey()+"#"+String.valueOf(entry.getValue()));
                                                    }
                                                }

                                                if(set.isEmpty()){
                                                    int posArroba=mAuth.getCurrentUser().getEmail().toString().indexOf("@");
                                                    String nombreUsuario=mAuth.getCurrentUser().getEmail().toString().substring(0,posArroba);
                                                    Log.d("EMAIL",nombreUsuario);
                                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                            .setDisplayName(nombreUsuario)
                                                            .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                                            .build();
                                                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    currentUser.reload();
                                                    currentUser.updateProfile(profileUpdates)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                                                        StorageReference storageRef = storage.getReference();
                                                                        StorageReference userImageRef = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
                                                                        Uri file =  Uri.parse("android.resource://com.miguelcabezas.tfm.saltour/" + R.drawable.profile_icon);
                                                                        UploadTask uploadTask = userImageRef.putFile(file);
                                                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception exception) {
                                                                                Toast.makeText(context,"FAIL IMAGE",Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                                                // ...

                                                                            }
                                                                        });
                                                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                        Map<String, Object> newUser = new HashMap<>();
                                                                        newUser.put("email", currentUser.getEmail());
                                                                        newUser.put("challengesCompleted", 0);
                                                                        newUser.put("totalTime", 0);
                                                                        newUser.put("challengesAndTime",new HashMap<String,String>());
                                                                        db.collection("users").document(currentUser.getEmail()).set(newUser);
                                                                        /*db.collection("users").add(newUser);*/
                                                                    }
                                                                }
                                                            });
                                                }

                                                SharedPreferences myPrefs = context.getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                                                SharedPreferences.Editor editor = myPrefs.edit();
                                                editor.putStringSet("ChallenegesCompleted#"+currentUser.getEmail(),set);
                                                editor.apply();
                                                editor.commit();

                                                usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            SharedPreferences myPrefs = context.getSharedPreferences("ActiveUsers",0);
                                                            SharedPreferences.Editor editor = myPrefs.edit();
                                                            editor.putLong("ActiveUsers",task.getResult().size());
                                                            editor.apply();
                                                            editor.commit();
                                                        }
                                                    }
                                                });
                                                Intent intent;
                                                intent=new Intent(context,HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                                activity.finish();
                                            } else {
                                                Log.e("ERROR", "Error getting documents: ", task.getException());
                                                Log.e("TAG","No exite en BBDD");
                                                Intent intent;
                                                intent=new Intent(context,HomeActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                                activity.finish();
                                            }
                                        }
                                    });

                        }else{
                            Log.e("Error","SignInWithCredential:failure",task.getException());
                        }
                    }
                });
    }

    /**
     * Valida que las credenciales introducidas sean válidas y no exista ningún usuario registrado con las mismas (mismo email)
     * En caso afirmativo graba al usuario en la base de datos con foto de perfil por defecto y ningún reto completado. A mayores remite un correo de confirmación de registro
     * @param mAuth Referencia a laautenticación dde usuario
     * @param context Referencia al contexto de la vista
     * @param usuario Referencia al cuadro de texto de usuario de la vista
     * @param contrasena Referencia al cuadro de texto de contraseña de la vista
     * @param activity Referencia a la actividad desde donde se invoca el método
     * @param progressDialog Referencia al cuadro de progreso de la vista
     */
    public void registerWithMailAndPassword(final EditText usuario, final EditText contrasena, final Context context, final FirebaseAuth mAuth, final ProgressDialog progressDialog, final Activity activity){
        String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(context,context.getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            usuario.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(context,context.getString(R.string.introducir_password),Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(context,context.getString(R.string.ocho_caracteres),Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(context,context.getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            usuario.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        progressDialog.setMessage("Realizando el registro...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(usuario_text,contrasena_text)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                int posArroba=mAuth.getCurrentUser().getEmail().toString().indexOf("@");
                                                String nombreUsuario=mAuth.getCurrentUser().getEmail().toString().substring(0,posArroba);
                                                Log.d("EMAIL",nombreUsuario);
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nombreUsuario)
                                                        .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                                        .build();
                                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                                currentUser.reload();
                                                currentUser.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                                    StorageReference storageRef = storage.getReference();
                                                                    StorageReference userImageRef = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
                                                                    Uri file =  Uri.parse("android.resource://com.miguelcabezas.tfm.saltour/" + R.drawable.profile_icon);
                                                                    UploadTask uploadTask = userImageRef.putFile(file);
                                                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception exception) {
                                                                            Toast.makeText(context,context.getString(R.string.error_imagen),Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                                            // ...

                                                                        }
                                                                    });
                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                    Map<String, Object> newUser = new HashMap<>();
                                                                    newUser.put("email", currentUser.getEmail());
                                                                    newUser.put("challengesCompleted", 0);
                                                                    newUser.put("totalTime", 0);
                                                                    newUser.put("challengesAndTime",new HashMap<String,String>());
                                                                    db.collection("users").document(currentUser.getEmail()).set(newUser);
                                                                    /*db.collection("users").add(newUser);*/
                                                                    Toast.makeText(context,context.getString(R.string.enviada_verificacion),Toast.LENGTH_LONG).show();
                                                                    Intent returnIntent=new Intent();
                                                                    activity.setResult(RESULT_OK,returnIntent);
                                                                    activity.finish();
                                                                }
                                                            }
                                                        });
                                                mAuth.signOut();
                                            }
                                        }
                                    });
                            usuario.setText("");
                            contrasena.setText("");
                            usuario.setBackgroundResource(0);
                            contrasena.setBackgroundResource(0);
                        }else{
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(context,context.getString(R.string.usuario_existente),Toast.LENGTH_LONG).show();
                                usuario.setBackgroundResource(R.drawable.borderojo);
                            }else{
                                Toast.makeText(context,context.getString(R.string.registro_no_completado),Toast.LENGTH_LONG).show();
                                usuario.setBackgroundResource(0);
                                contrasena.setBackgroundResource(0);
                            }
                            usuario.setText("");
                            contrasena.setText("");
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    /**
     * Manda los pasos para la recuperación de la contraseña olvidada
     * @param context Referencia al contexto de la vista
     * @param recuperacion Referencia al cuadro de texto dela vista donde meter el correo al que mandar la recuperación de la contraseña olvidada
     * @param activity Referencia a la actividad desde donde se invoca el método
     */
    public void recoverPassword(final EditText recuperacion, final Context context, final Activity activity){
        if(recuperacion.getText().toString().isEmpty()){
            Toast.makeText(context,context.getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            return;
        }
        if(!validarEmail(recuperacion.getText().toString())){
            Toast.makeText(context,context.getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(recuperacion.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent returnIntent=new Intent();
                            activity.setResult(RESULT_OK,returnIntent);
                            activity.finish();
                        }
                    }
                });
    }

    /**
     * Coprueba que la dirección email introducida casa con el patrón correcto determinado por las expresiones regulares provistas por Java
     * @param email Dirección de correo a validar
     * @return Validez del correo
     */
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
