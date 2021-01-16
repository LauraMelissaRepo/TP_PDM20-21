package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;

import android.widget.ImageView;

public class People {

    private long id;
//    private ImageView picturePerson;
    private String namePerson;
    private String degreePerson;

    public People(){};

    public People(long id, String namePerson, String degreePerson){
        this.id = id;
        this.namePerson = namePerson;
        this.degreePerson = degreePerson;
    }

    public long getId(){return id;}

    public String getNamePerson(){return namePerson;}

    public String getDegreePerson(){return degreePerson;}

}
