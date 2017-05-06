package com.ronyonatan.foodlocater;

import com.orm.SugarRecord;


public class MyPlace extends SugarRecord {
    String name;
    String vicinity;
    String icon;
    String keyword;
    double distance;


    public MyPlace() {
    }

    public MyPlace(String keyword, String name, String vicinity, String icon,double distance ) {

        this.name = name;
        this.keyword = keyword;
        this.vicinity = vicinity;
        this.icon = icon;
        this.distance=distance;

    }
}
