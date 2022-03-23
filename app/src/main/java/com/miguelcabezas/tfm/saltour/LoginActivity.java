package com.miguelcabezas.tfm.saltour;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.regex.Pattern;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText usuario,contrasena;
    private Button registro;
    private ProgressDialog progressDialog;
    private TextView olvido;
    private static final int CODIGO_PERMISOS_CAMARA = 1,
            CODIGO_PERMISOS_ALMACENAMIENTO = 2;
    // Banderas que indicarán si tenemos permisos
    private boolean tienePermisoCamara = false,
            tienePermisoAlmacenamiento = false;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usuario=findViewById(R.id.editTextTextEmailAddress);
        contrasena=findViewById(R.id.editTextTextPassword);
        registro=findViewById(R.id.b_registro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        progressDialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        verificarYPedirPermisosDeAlmacenamiento();
        verificarYPedirPermisosDeCamara();
    }
    public void updateUI(FirebaseUser account){

        if(account != null){
            startActivity(new Intent(this,HomeActivity.class));
        }
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void onClickForgotPassword(View view){
        Intent intent=new Intent(getApplicationContext(),ForgotPassword.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"Instrucciones para restauración de contraseña enviadas",Toast.LENGTH_LONG).show();
        }
    }

    public void onClickLogin(View view){
        final String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(this,"Se debe introducir un email",Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(this,"Se debe introducir contraseña",Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(this,"La contraseña debe contener al menos 8 caracteres",Toast.LENGTH_LONG).show();
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(this,"Email no válido",Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage("Comprobando datos...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(usuario_text,contrasena_text)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference userImageRef = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
                                Uri file =  Uri.parse("android.resource://com.miguelcabezas.tfm.saltour/" + R.drawable.profile_icon);
                                UploadTask uploadTask = userImageRef.putFile(file);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getApplicationContext(),"FAIL IMAGE",Toast.LENGTH_LONG).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                        // ...
                                        Toast.makeText(getApplicationContext(),"SUCCESS IMAGE",Toast.LENGTH_LONG).show();
                                    }
                                });
                                Intent intent;
                                intent=new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                AlertDialog.Builder confirmacion = new AlertDialog.Builder(LoginActivity.this);
                                confirmacion.setTitle("Correo no verificado");
                                confirmacion.setMessage("¿Desea recibir de nuevo el email de verificación?");
                                confirmacion.setCancelable(false);
                                confirmacion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(LoginActivity.this,"Reenviado correo de verificación",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                confirmacion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mAuth.signOut();
                                    }
                                });
                                confirmacion.create();
                                confirmacion.show();;
                                usuario.setText("");
                                contrasena.setText("");
                                usuario.setBackgroundResource(0);
                                contrasena.setBackgroundResource(0);
                                mAuth.signOut();
                            }


                        }else{
                            Toast.makeText(LoginActivity.this,"Usuario y/o contraseña incorrecto",Toast.LENGTH_LONG).show();
                            usuario.setText("");
                            contrasena.setText("");
                            usuario.setBackgroundResource(R.drawable.borderojo);
                            contrasena.setBackgroundResource(R.drawable.borderojo);
                        }
                        progressDialog.dismiss();
                    }
                });
    }
    public void onClickRegistro(View view){
        String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(this,"Se debe introducir un email",Toast.LENGTH_LONG).show();
            usuario.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(this,"Se debe introducir contraseña",Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(this,"La contraseña debe contener al menos 8 caracteres",Toast.LENGTH_LONG).show();
            contrasena.setBackgroundResource(R.drawable.borderojo);
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(this,"Email no válido",Toast.LENGTH_LONG).show();
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
                                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                                currentUser.reload();
                                                currentUser.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(LoginActivity.this,"Enviado correo de verificación",Toast.LENGTH_LONG).show();
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
                                Toast.makeText(LoginActivity.this,"Usuario ya existente",Toast.LENGTH_LONG).show();
                                usuario.setBackgroundResource(R.drawable.borderojo);
                            }else{
                                Toast.makeText(LoginActivity.this,"No se pudo completar el registro",Toast.LENGTH_LONG).show();
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
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    private void verificarYPedirPermisosDeCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoDeCamaraConcedido();
        } else {
            // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, CODIGO_PERMISOS_CAMARA);
        }
    }
    private void verificarYPedirPermisosDeAlmacenamiento() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {
            // En caso de que haya dado permisos ponemos la bandera en true
            // y llamar al método
            permisoDeAlmacenamientoConcedido();
        } else {
            // Si no, entonces pedimos permisos. Ahora mira onRequestPermissionsResult
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODIGO_PERMISOS_ALMACENAMIENTO);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeCamaraConcedido();
                } else {
                    permisoDeCamaraDenegado();
                }
                break;

            case CODIGO_PERMISOS_ALMACENAMIENTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoDeAlmacenamientoConcedido();
                } else {
                    permisoDeAlmacenamientoDenegado();
                }
                break;

        }
    }
    private void permisoDeAlmacenamientoConcedido() {
        tienePermisoAlmacenamiento = true;
    }

    private void permisoDeAlmacenamientoDenegado() {

        Log.d("DENEGADO","ALMACENAMIENTO");
    }

    private void permisoDeCamaraConcedido() {
        tienePermisoCamara = true;
    }

    private void permisoDeCamaraDenegado() {
        Log.d("DENEGADO","CÁMARA");
    }
}