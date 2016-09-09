package com.ykizilkaya.t24.models;

import java.util.ArrayList;

public class NewsCats {

    /** categories of news **/
    public ArrayList<NewsCat> data;

    public ArrayList<NewsCat> getData() {
        return data;
    }

    public class NewsCat {

        private String id, name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

}
