package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class DaoFirebaseImpl implements DaoFirebase{
    @Override
    public void updateProfilePic(String path, final FirebaseAuth mAuth, final Context context) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("images/"+ currentUser.getEmail().toString()+".jpg");
        Uri file =  Uri.fromFile(new File(path));
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
                Toast.makeText(context,"SUCCESS IMAGE",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public StorageReference getPicFromStorage(String path) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(path);
        return storageRef;
    }

    @Override
    public FirebaseFirestore getDatabaseInstance() {
        return FirebaseFirestore.getInstance();
    }

    @Override
    public CollectionReference getCollectionReference(FirebaseFirestore db, String path) {
        return db.collection(path);
    }

    @Override
    public FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    @Override
    public StorageReference getStorageReference(FirebaseStorage storageInstance) {
        return storageInstance.getReference();
    }

    @Override
    public StorageReference getPathStorageReference(StorageReference storageReference, String path) {
        return storageReference.child(path);
    }

    @Override
    public void updateUserProfile(UserProfileChangeRequest profileUpdates,FirebaseAuth mAuth) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.reload();
        currentUser.updateProfile(profileUpdates);
    }

    @Override
    public Task<Void> updateUserPassword(FirebaseUser currentUser, String nuevaContrasena) {
        return currentUser.updatePassword(nuevaContrasena);
    }

    @Override
    public DocumentReference getDocumentReference(String path, String document, FirebaseFirestore db) {
        return db.collection(path).document(document);
    }

    @Override
    public void updateDocumentField(DocumentReference document, String field, Object value) {
        document.update(field,value);
    }

}
