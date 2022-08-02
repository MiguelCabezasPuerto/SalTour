package com.miguelcabezas.tfm.saltour.controller;

import com.miguelcabezas.tfm.saltour.model.FaQ;
/**
 * Clase encargada de generar preguntas y respuestas frecuentes
 * @author Miguel Cabezas Puerto
 *
 * */
public class HelpController {

    String [] answers = {
            "An answer to q1",
            "An answer to q2",
            "An answer to q3",
            "An answer to q4",
            "An answer to q5",
            "An answer to q6"
    };
    String [] questions = {
            "A text for q1",
            "A text for q2",
            "A text for q3",
            "A text for q4",
            "A text for q5",
            "A text for q6"
    };

    /**
     * Extrae una respuesta del array
     * @param position posición en el array de la respuesta a generar
     * @return Respuesta extraída
     */
    public String generateAnswer(int position){
        if(position > 5){
            position = 0;
        }
        return  answers[position];
    }
    /**
     * Extrae una pregunta del array
     * @param position posición en el array de la pregunta a generar
     * @return Pregunta extraída
     */
    public String generateQuestion(int position){
        if(position > 5){
            position = 0;
        }
        return questions[position];
    }
}
