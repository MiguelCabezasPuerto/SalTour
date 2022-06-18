package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class DaoSharedPreferencesImpl implements DaoSharedPreferences{
    @Override
    public SharedPreferences getChallengesCompletedSharedPreferences(String key, Context context) {
        return context.getSharedPreferences(key, 0);
    }

    @Override
    public void updateChallengesCompleted(SharedPreferences myPrefs, String key, Set<String> value) {
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putStringSet(key,value);
        editor.apply();
        editor.commit();
    }
}
