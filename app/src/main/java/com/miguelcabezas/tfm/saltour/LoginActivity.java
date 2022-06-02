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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.context.AttributeContext;
import com.miguelcabezas.tfm.saltour.model.User;
import com.miguelcabezas.tfm.saltour.utils.SalLib;
import com.miguelcabezas.tfm.saltour.view.ExpandableListDataPumpRanking;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomExpandableListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import android.os.Bundle;


public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private EditText usuario,contrasena;
    private Button registro;
    private SignInButton btnLoginGoogle;
    private ProgressDialog progressDialog;
    private TextView olvido;
    private static final int CODIGO_PERMISOS_CAMARA = 1,
            CODIGO_PERMISOS_ALMACENAMIENTO = 2;
    private int RC_SIGN_IN = 5;
    private final int REGISTER_REQUEST_CODE = 6;
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
        btnLoginGoogle = (SignInButton)findViewById(R.id.b_entrar_google);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        progressDialog=new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        verificarYPedirPermisosDeAlmacenamiento();
        verificarYPedirPermisosDeCamara();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","Login google button pressed");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });


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
        Log.e("TAG","On activity result");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Toast.makeText(getApplicationContext(),"Instrucciones para restauración de contraseña enviadas",Toast.LENGTH_LONG).show();
        }
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
                }catch (ApiException e){
                    Toast.makeText(getApplicationContext(),"Google sign in fail",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Ocurrió un error. "+task.getException().toString(),Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode == REGISTER_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d("TAG","Registro realizado");
        }
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                                                                                    Toast.makeText(getApplicationContext(),"FAIL IMAGE",Toast.LENGTH_LONG).show();
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

                                                    SharedPreferences myPrefs = getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                                                    SharedPreferences.Editor editor = myPrefs.edit();
                                                    editor.putStringSet("ChallenegesCompleted#"+currentUser.getEmail(),set);
                                                    editor.apply();
                                                    editor.commit();

                                                    usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                SharedPreferences myPrefs = getSharedPreferences("ActiveUsers",0);
                                                                SharedPreferences.Editor editor = myPrefs.edit();
                                                                editor.putLong("ActiveUsers",task.getResult().size());
                                                                editor.apply();
                                                                editor.commit();
                                                            }
                                                        }
                                                    });
                                                    Intent intent;
                                                    intent=new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e("ERROR", "Error getting documents: ", task.getException());
                                                    Log.e("TAG","No exite en BBDD");
                                                    Intent intent;
                                                    intent=new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                        }else{
                            Log.e("Error","SignInWithCredential:failure",task.getException());
                        }
                    }
                });
    }



    public void onClickLoginInvitado(View view){
        mAuth.signInWithEmailAndPassword("Invitado@testsaltour.com","12345678")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                            intent=new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void onClickLogin(View view){
        final String usuario_text=usuario.getText().toString().trim();
        String contrasena_text=contrasena.getText().toString().trim();
        if(usuario_text.isEmpty()){
            Toast.makeText(this,getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.isEmpty()){
            Toast.makeText(this,getString(R.string.introducir_password),Toast.LENGTH_LONG).show();
            return;
        }
        if(contrasena_text.length()<8){
            Toast.makeText(this,getString(R.string.ocho_caracteres),Toast.LENGTH_LONG).show();
            return;
        }
        if(!validarEmail(usuario_text)){
            Toast.makeText(this,getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setMessage(getString(R.string.comprobando_datos));
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

                                                    SharedPreferences myPrefs = getSharedPreferences("ChallenegesCompleted#"+currentUser.getEmail(), 0);
                                                    SharedPreferences.Editor editor = myPrefs.edit();
                                                    editor.putStringSet("ChallenegesCompleted#"+currentUser.getEmail(),set);
                                                    editor.apply();
                                                    editor.commit();

                                                    usersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                SharedPreferences myPrefs = getSharedPreferences("ActiveUsers",0);
                                                                SharedPreferences.Editor editor = myPrefs.edit();
                                                                editor.putLong("ActiveUsers",task.getResult().size());
                                                                editor.apply();
                                                                editor.commit();
                                                            }
                                                        }
                                                    });
                                                    Intent intent;
                                                    intent=new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Log.e("ERROR", "Error getting documents: ", task.getException());
                                                    Intent intent;
                                                    intent=new Intent(getApplicationContext(),HomeActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                            }else{
                                AlertDialog.Builder confirmacion = new AlertDialog.Builder(LoginActivity.this);
                                confirmacion.setTitle(getString(R.string.correo_no_verificado));
                                confirmacion.setMessage(getString(R.string.recibir_verificacion));
                                confirmacion.setCancelable(false);
                                confirmacion.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(LoginActivity.this,getString(R.string.reenviada_verificacion),Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                                confirmacion.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
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
                            Toast.makeText(LoginActivity.this,getString(R.string.credenciales_incorrectas),Toast.LENGTH_LONG).show();
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
        Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
        startActivityForResult(intent,REGISTER_REQUEST_CODE);
        /*String usuario_text=usuario.getText().toString().trim();
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
                                                                            Toast.makeText(getApplicationContext(),"FAIL IMAGE",Toast.LENGTH_LONG).show();
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
                });*/
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handleReturn = super.dispatchTouchEvent(ev);
        SalLib.hideKeyBoard(ev,getApplicationContext(),getCurrentFocus(),getWindow());
        return handleReturn;
    }

}