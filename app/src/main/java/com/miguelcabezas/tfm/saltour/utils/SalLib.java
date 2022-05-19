package com.miguelcabezas.tfm.saltour.utils;

import com.miguelcabezas.tfm.saltour.model.ChallengeListDTO;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SalLib {

    public static String removeLastCharacter(String stringToCut){
        return (stringToCut == null) ? null : stringToCut.replaceAll(".$", "");
    }
    public static Long parseStringToLongSpecial(String stringToParse){
        String numberPart =  removeLastCharacter(stringToParse);
        return Long.parseLong(numberPart);
    }
    public static String getLastCharacter(String stringToInspect){
        return stringToInspect.substring(stringToInspect.length() - 1);
    }
    public static ArrayList<?>convertMapToArray(Map<?,?> mapToConvert){
        ArrayList<Object>arrayToReturn = new ArrayList<>();

        for (Map.Entry<?, ?> entry : mapToConvert.entrySet()) {
            if(entry.getValue().getClass().equals(Long.class)){
                ChallengeListDTO challengeListDTO = new ChallengeListDTO(entry.getKey().toString(),convertToHHMMSS((Long)entry.getValue()));
                arrayToReturn.add(challengeListDTO);
            }
        }

        return arrayToReturn;
    }

    public static String convertToHHMMSS(Long timeToConvert){
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeToConvert),
                TimeUnit.MILLISECONDS.toMinutes(timeToConvert) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToConvert)),
                TimeUnit.MILLISECONDS.toSeconds(timeToConvert) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToConvert)));
    }
}
