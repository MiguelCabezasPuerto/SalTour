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

/**
 * Clase que implementa la interfaz de gestión de datos de usuario y retos en su interacción con base de datos
 * @author Miguel Cabezas Puerto
 *
 * */
public class DaoFirebaseImpl implements DaoFirebase{
    /**
     * Actualiza la imagen de perfil del usuario , para ello sube un nuevo archivo (reescribiendo el anterior) al repositiorio de imágenes ofrecido por Firebase
     * @param path Ruta a la imagen de perfil en el repositorio de Google
     * @param mAuth Referencia a la autenticación de usuario
     * @param context contexto desde el que se invoca al método
     */
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

    /**
     * Recupera la imagen de perfil del usuario recuperando la instancia del repositorio y dentro de ella la ruta recibida
     * @param path ruta a la imagen del repositorio
     * @return Referencia a la imagen
     */
    @Override
    public StorageReference getPicFromStorage(String path) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(path);
        return storageRef;
    }

    /**
     * Recupera una instancia de base de datos
     * @return Instancia de base de datos
     */
    @Override
    public FirebaseFirestore getDatabaseInstance() {
        return FirebaseFirestore.getInstance();
    }

    /**
     * Recupera una colección dentro de la base de datos
     * @param db Instancia de base de datos
     * @param path ruta de la colección a recuperar
     * @return Referencia a una colección
     */
    @Override
    public CollectionReference getCollectionReference(FirebaseFirestore db, String path) {
        return db.collection(path);
    }

    /**
     * Recupera una instancia de repositorio de almacenamiento
     * @return Instancia de repositorio de almacenamiento
     */
    @Override
    public FirebaseStorage getStorageInstance() {
        return FirebaseStorage.getInstance();
    }

    /**
     * Recupera una referencia a la instancia de repositorio de almacenamiento
     * @return Referencia a la instancia de repositorio de almacenamiento
     */
    @Override
    public StorageReference getStorageReference(FirebaseStorage storageInstance) {
        return storageInstance.getReference();
    }

    /**
     * Recupera una referencia a una ruta dentro del repositorio de almacenamiento
     * @return Referencia a una ruta dentro del repositorio de almacenamiento
     */
    @Override
    public StorageReference getPathStorageReference(StorageReference storageReference, String path) {
        return storageReference.child(path);
    }

    /**
     * Actualiza el perfil de usuario con los cambios recibidos
     * @param profileUpdates Cambios a actualizar en el usuario
     * @param mAuth Referencia a la sesión de autenticación el usuario
     */
    @Override
    public void updateUserProfile(UserProfileChangeRequest profileUpdates,FirebaseAuth mAuth) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.reload();
        currentUser.updateProfile(profileUpdates);
    }

    /**
     * Actualiza la contraseña del usuario
     * @param currentUser Referencia al usuario activo
     * @param nuevaContrasena Nueva contraseña
     */
    @Override
    public Task<Void> updateUserPassword(FirebaseUser currentUser, String nuevaContrasena) {
        return currentUser.updatePassword(nuevaContrasena);
    }

    /**
     * Recupera la referencia a un documento de una colección de base de datos
     * @param path Ruta a la colección
     * @param document Identificador del documento a recuperar
     * @param db Base de datos de la que recuperar la colección
     * @return Referencia a un documento de una colección de base de datos
     */
    @Override
    public DocumentReference getDocumentReference(String path, String document, FirebaseFirestore db) {
        return db.collection(path).document(document);
    }

    /**
     * Actualiza un campo de un documento de base de datos
     * @param document Identificador del documento a recuperar
     * @param field Campo a acttualizar
     * @param value Nuevo valor a establecer
     */
    @Override
    public void updateDocumentField(DocumentReference document, String field, Object value) {
        document.update(field,value);
    }

}
