package com.miguelcabezas.tfm.saltour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {
    private EditText recuperacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        recuperacion=findViewById(R.id.editTextTextEmailAddress2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }
    public void onClickResetear(View view){

        if(recuperacion.getText().toString().isEmpty()){
            Toast.makeText(this,getString(R.string.introducir_email),Toast.LENGTH_LONG).show();
            return;
        }
        if(!validarEmail(recuperacion.getText().toString())){
            Toast.makeText(this,getString(R.string.email_no_valido),Toast.LENGTH_LONG).show();
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(recuperacion.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent returnIntent=new Intent();
                            setResult(RESULT_OK,returnIntent);
                            finish();
                        }
                    }
                });
    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}