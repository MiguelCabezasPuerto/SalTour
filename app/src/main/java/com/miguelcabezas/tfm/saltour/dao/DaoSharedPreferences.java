package com.miguelcabezas.tfm.saltour.dao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public interface DaoSharedPreferences {
    SharedPreferences getChallengesCompletedSharedPreferences(String key, Context context);
    void updateChallengesCompleted(SharedPreferences myPrefs, String key, Set<String> value);
}
