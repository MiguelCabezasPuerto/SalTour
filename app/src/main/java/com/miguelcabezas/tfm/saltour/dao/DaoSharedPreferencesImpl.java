package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Clase que implementa la interfaz de gestión de datos de shared preferences del usuario logueado en la aplicación en el dispositivo
 * @author Miguel Cabezas Puerto
 *
 * */
public class DaoSharedPreferencesImpl implements DaoSharedPreferences{
    /**
     * Recupera shared preferences dada una clave
     * @param key clave de la shared preference a recuperar
     * @param context Contexto desde el que se invoca al método
     * @return Shared preference
     */
    @Override
    public SharedPreferences getChallengesCompletedSharedPreferences(String key, Context context) {
        return context.getSharedPreferences(key, 0);
    }

    /**
     * Actualiza una shared preference dado el nuevo valor y la clave de esta
     * @param key clave de la shared preference a recuperar
     * @param myPrefs Shared preference a editar
     * @param value Nuevo valor a establecer
     */
    @Override
    public void updateChallengesCompleted(SharedPreferences myPrefs, String key, Set<String> value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putStringSet(key,value);
        editor.apply();
        editor.commit();
    }
}
