package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Interfaz a partir de la cual construir los DAO de acceso a los datos de usuario y retos en base de datos
 * @author Miguel Cabezas Puerto
 *
 * */
public interface DaoFirebase {
    void updateProfilePic(String path, FirebaseAuth auth, Context context);
    StorageReference getPicFromStorage(String path);
    FirebaseFirestore getDatabaseInstance();
    CollectionReference getCollectionReference(FirebaseFirestore db, String path);
    FirebaseStorage getStorageInstance();
    StorageReference getStorageReference(FirebaseStorage storageInstance);
    StorageReference getPathStorageReference(StorageReference storageReference,String path);
    void updateUserProfile(UserProfileChangeRequest profileUpdates,FirebaseAuth mAuth);
    Task<Void> updateUserPassword(FirebaseUser currentUser, String nuevaContrasena);
    DocumentReference getDocumentReference(String path, String document, FirebaseFirestore db);
    void updateDocumentField(DocumentReference document, String field, Object value);
}
