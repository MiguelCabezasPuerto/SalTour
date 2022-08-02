package com.miguelcabezas.tfm.saltour.model;

/**
 * Clase modelo de información de preguntas y respuestas frecuentes
 * @author Miguel Cabezas Puerto
 *
 * */
public class FaQ {
    private String question;
    private String answer;

    public FaQ(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public FaQ(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
