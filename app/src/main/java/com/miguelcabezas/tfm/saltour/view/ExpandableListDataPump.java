package com.miguelcabezas.tfm.saltour.view;

import com.miguelcabezas.tfm.saltour.controller.HelpController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        HelpController helpController = new HelpController();

        List<String> question1 = new ArrayList<String>();
        question1.add(helpController.generateAnswer(0));

        List<String> question2 = new ArrayList<String>();
        question2.add(helpController.generateAnswer(1));

        List<String> question3 = new ArrayList<String>();
        question3.add(helpController.generateAnswer(2));


        List<String> question4 = new ArrayList<String>();
        question4.add(helpController.generateAnswer(3));

        List<String> question5 = new ArrayList<String>();
        question5.add(helpController.generateAnswer(4));

        List<String> question6 = new ArrayList<String>();
        question6.add(helpController.generateAnswer(5));

        expandableListDetail.put(helpController.generateQuestion(0), question1);
        expandableListDetail.put(helpController.generateQuestion(1), question2);
        expandableListDetail.put(helpController.generateQuestion(2), question3);
        expandableListDetail.put(helpController.generateQuestion(3), question4);
        expandableListDetail.put(helpController.generateQuestion(4), question5);
        expandableListDetail.put(helpController.generateQuestion(5), question6);
        return expandableListDetail;
    }
}
