package com.miguelcabezas.tfm.saltour.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.miguelcabezas.tfm.saltour.model.ChallengeListDTO;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Clase librería de funcionalidades auxiliares
 * @author Miguel Cabezas Puerto
 *
 * */
public class SalLib {

    /**
     * Borra el último caracter de una cadena
     * @param stringToCut cadena a tratar
     * @return Cadena sin el ultimo caracter
     */
    public static String removeLastCharacter(String stringToCut){
        return (stringToCut == null) ? null : stringToCut.replaceAll(".$", "");
    }
    /**
     * Convierte una cadena a Long
     * @param stringToParse cadena a tratar
     * @return Long de la cadena
     */
    public static Long parseStringToLongSpecial(String stringToParse){
        String numberPart =  removeLastCharacter(stringToParse);
        return Long.parseLong(numberPart);
    }
    /**
     * Obtiene el ultimo caracter de una cadena
     * @param stringToInspect cadena a tratar
     * @return Ultimo caracter de una cadena en formato String
     */
    public static String getLastCharacter(String stringToInspect){
        return stringToInspect.substring(stringToInspect.length() - 1);
    }
    /**
     * Convierte un mapa de valores en un array de valores
     * @param mapToConvert mapa a tratar
     * @return Listado de valores del mapa
     */
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

    /**
     * Convierte un tiempo en formato timeMillis en su respectiva hora, minuto y segundo
     * @param timeToConvert tiempo a tratar
     * @return Tiempo en formato horas, minutos y segundos
     */
    public static String convertToHHMMSS(Long timeToConvert){
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeToConvert),
                TimeUnit.MILLISECONDS.toMinutes(timeToConvert) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeToConvert)),
                TimeUnit.MILLISECONDS.toSeconds(timeToConvert) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToConvert)));
    }

    /**
     * Oculta el teclado al pulsar cualquier otra parte de la pantalla
     * @param ev Evento de toque
     * @param context Contexto desde donde se invoca el método
     * @param currentFocus Vista que se desea ocultar
     * @param window Ventana de la vista a ocultar
     */
    public static void hideKeyBoard(MotionEvent ev, Context context,View currentFocus, Window window){
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if(currentFocus instanceof EditText){
            View innerView = currentFocus; //getCurrentFocus();

            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    !getLocationOnScreen((EditText) innerView).contains(x, y)) {

                InputMethodManager input = (InputMethodManager)
                        context.getSystemService(Context.INPUT_METHOD_SERVICE);
                input.hideSoftInputFromWindow(window.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }
    private static Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left = location[0];
        mRect.top = location[1];
        mRect.right = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }
}
