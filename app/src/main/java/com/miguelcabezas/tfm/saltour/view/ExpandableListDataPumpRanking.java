package com.miguelcabezas.tfm.saltour.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPumpRanking {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();



        /*Recuperar de BBDD los retos completados con su tiempo*/

        List<String> item1 = new ArrayList<String>();
        item1.add("La rana de Salamanca#47 min");
        item1.add("El jardín secreto#2h 37 min");
        item1.add("Los pingüinos callejeros#53 min");


        expandableListDetail.put("Retos completados (3)", item1);
        return expandableListDetail;
    }
}
