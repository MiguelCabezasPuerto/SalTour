package com.miguelcabezas.tfm.saltour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcabezas.tfm.saltour.controller.AuthenticatonController;
import com.miguelcabezas.tfm.saltour.utils.SalLib;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Vista que recoge las opciones de registro en la aplicación
 * @author Miguel Cabezas Puerto
 *
 * */
public class RegisterActivity extends AppCompatActivity {
    private EditText usuario,contrasena;
    private Button registro;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usuario=findViewById(R.id.editTextTextEmailAddress);
        contrasena=findViewById(R.id.editTextTextPassword);
        registro=findViewById(R.id.b_registro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        progressDialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Escucha el eveto de pulsación del botón de registro y llama al controlador para continuar el proceso
     * @param view botón de registro
     */
    public void onClickRegistro(View view){
        AuthenticatonController authenticatonController = new AuthenticatonController();
        authenticatonController.registerWithMailAndPassword(usuario,contrasena,getApplicationContext(),mAuth,progressDialog,this);
        /*String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(this,getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            usuario.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(this,getString(R.string.introducir_password),Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(this,getString(R.string.ocho_caracteres),Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(this,getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            usuario.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        progressDialog.setMessage("Realizando el registro...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(usuario_text,contrasena_text)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                                                            Toast.makeText(getApplicationContext(),getString(R.string.error_imagen),Toast.LENGTH_LONG).show();
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
                                                                    *//*db.collection("users").add(newUser);*//*
                                                                    Toast.makeText(RegisterActivity.this,getString(R.string.enviada_verificacion),Toast.LENGTH_LONG).show();
                                                                    Intent returnIntent=new Intent();
                                                                    setResult(RESULT_OK,returnIntent);
                                                                    finish();
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
                                Toast.makeText(RegisterActivity.this,getString(R.string.usuario_existente),Toast.LENGTH_LONG).show();
                                usuario.setBackgroundResource(R.drawable.borderojo);
                            }else{
                                Toast.makeText(RegisterActivity.this,getString(R.string.registro_no_completado),Toast.LENGTH_LONG).show();
                                usuario.setBackgroundResource(0);
                                contrasena.setBackgroundResource(0);
                            }
                            usuario.setText("");
                            contrasena.setText("");
                        }
                        progressDialog.dismiss();
                    }
                });*/

    }
    /*private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }*/
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handleReturn = super.dispatchTouchEvent(ev);
        SalLib.hideKeyBoard(ev,getApplicationContext(),getCurrentFocus(),getWindow());
        return handleReturn;
    }
}