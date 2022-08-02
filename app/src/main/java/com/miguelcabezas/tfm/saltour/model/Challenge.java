package com.miguelcabezas.tfm.saltour.model;

/**
 * Clase modelo de transferencia de datos con base de datos, contenedora de la información del reto
 * @author Miguel Cabezas Puerto
 *
 * */
public class Challenge {
    String name;
    String latitude;
    String longitude;

    public Challenge(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
