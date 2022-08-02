package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Interfaz a partir de la cual construir los DAO de acceso a los datos de shared preferences del usuario logueado en la aplicación
 * @author Miguel Cabezas Puerto
 *
 * */
public interface DaoSharedPreferences {
    SharedPreferences getChallengesCompletedSharedPreferences(String key, Context context);
    void updateChallengesCompleted(SharedPreferences myPrefs, String key, Set<String> value);
}
