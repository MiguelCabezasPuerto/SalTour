package com.miguelcabezas.tfm.saltour.model;

import java.util.ArrayList;
import java.util.Map;


/**
 * Clase modelo de información de datos del usuario, personales y de sus retos
 * @author Miguel Cabezas Puerto
 *
 * */
public class User {
    private String email;
    private int challengesCompleted;
    long totalTime;
    Map<String,Long> challengesAndTime;
    float challengesCompleted_totalTime;

    public User(String email, int challengesCompleted, long totalTime, Map<String,Long> challengesAndTime,float challengesCompleted_totalTime) {
        this.email = email;
        this.challengesCompleted = challengesCompleted;
        this.totalTime = totalTime;
        this.challengesAndTime = challengesAndTime;
        this.challengesCompleted_totalTime = challengesCompleted_totalTime;
    }
    public User(){}

    public float getChallengesCompleted_totalTime() {
        return challengesCompleted_totalTime;
    }

    public void setChallengesCompleted_totalTime(float challengesCompleted_totalTime) {
        this.challengesCompleted_totalTime = challengesCompleted_totalTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getChallengesCompleted() {
        return challengesCompleted;
    }

    public void setChallengesCompleted(int challengesCompleted) {
        this.challengesCompleted = challengesCompleted;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public Map<String, Long> getChallengesAndTime() {
        return challengesAndTime;
    }

    public void setChallengesAndTime(Map<String, Long> challengesAndTime) {
        this.challengesAndTime = challengesAndTime;
    }
}
