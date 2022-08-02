package com.miguelcabezas.tfm.saltour.model;

/**
 * Clase modelo del reto activo en cada momento
 * @author Miguel Cabezas Puerto
 *
 * */
public class ActiveChallengeSingleton {
    private String name;
    private static ActiveChallengeSingleton INSTANCE = null;
    private ActiveChallengeSingleton() {};

    public static ActiveChallengeSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ActiveChallengeSingleton();
        }
        return(INSTANCE);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
