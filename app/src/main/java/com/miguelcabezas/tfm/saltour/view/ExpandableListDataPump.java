package com.miguelcabezas.tfm.saltour.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> question1 = new ArrayList<String>();
        question1.add("An answer to q1");

        List<String> question2 = new ArrayList<String>();
        question2.add("An answer to q2");

        List<String> question3 = new ArrayList<String>();
        question3.add("An answer to q3");


        List<String> question4 = new ArrayList<String>();
        question4.add("An answer to q4");

        List<String> question5 = new ArrayList<String>();
        question5.add("An answer to q5");

        List<String> question6 = new ArrayList<String>();
        question6.add("An answer to q6");

        expandableListDetail.put("A text for q1", question1);
        expandableListDetail.put("A text for q2", question2);
        expandableListDetail.put("A text for q3", question3);
        expandableListDetail.put("A text for q4", question4);
        expandableListDetail.put("A text for q5", question5);
        expandableListDetail.put("A text for q6", question6);
        return expandableListDetail;
    }
}
