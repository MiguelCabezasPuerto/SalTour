package com.miguelcabezas.tfm.saltour;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.$Gson$Preconditions;
import com.miguelcabezas.tfm.saltour.model.ChallengeListDTO;
import com.miguelcabezas.tfm.saltour.utils.EnumRetos;
import com.miguelcabezas.tfm.saltour.utils.SalLib;
import com.miguelcabezas.tfm.saltour.view.adapter.CustomAdapterPlayerChallenges;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    String email;
    int position;
    int totalChallenges;
    Map<Object, Object> challengesAndTime;
    TextView t_nickname,t_position,t_challenges;
    CircleImageView iv_profilePhoto;

    private ArrayList<ChallengeListDTO> challengeListDTOS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_profile_fragment);
        email = getIntent().getStringExtra("profileEmail");
        position = getIntent().getIntExtra("profilePosition",0);
        totalChallenges = getIntent().getIntExtra("profileNumChallenges",0);

        challengesAndTime = (Map<Object, Object>) getIntent().getSerializableExtra("hashmap");

        challengeListDTOS = (ArrayList<ChallengeListDTO>) SalLib.convertMapToArray(challengesAndTime);

        for(ChallengeListDTO challengeListDTO:challengeListDTOS){
            if(challengeListDTO.getName().contains(String.valueOf(EnumRetos.callejeros))){
                challengeListDTO.setIcon(R.drawable.img_penguins);
            }else if(challengeListDTO.getName().contains(String.valueOf(EnumRetos.jardín))){
                challengeListDTO.setIcon(R.drawable.img_jardin);
            }else if(challengeListDTO.getName().contains(String.valueOf(EnumRetos.plaza))){
                challengeListDTO.setIcon(R.drawable.img_medallon);
            }else if(challengeListDTO.getName().contains(String.valueOf(EnumRetos.rana))){
                challengeListDTO.setIcon(R.drawable.img_rana);
            }
        }


        ListView listView = findViewById(R.id.custom_listview);
        CustomAdapterPlayerChallenges customAdapterPlayerChallenges = new CustomAdapterPlayerChallenges(getApplicationContext(),challengeListDTOS);
        listView.setAdapter(customAdapterPlayerChallenges);


        t_nickname = findViewById(R.id.name);
        t_position = findViewById(R.id.location);
        t_challenges = findViewById(R.id.designation);
        iv_profilePhoto = findViewById(R.id.profile);


        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/"+email+".jpg");

        try {
            final File localFile = File.createTempFile(email,"jpg");

            storageRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmapImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            iv_profilePhoto.setImageBitmap(bitmapImage);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_imagen),Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        t_nickname.setText(email);
        t_position.setText(position+" "+ getString(R.string.position));
        t_challenges.setText(totalChallenges+" "+ getString(R.string.retos_completados2));
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        setResult(Activity.RESULT_OK);
        finish();
    }
}