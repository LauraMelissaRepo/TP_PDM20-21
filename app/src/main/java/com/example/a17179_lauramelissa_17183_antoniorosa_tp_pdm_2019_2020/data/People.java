package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;

import android.widget.ImageView;

public class People {

    private long id;
    private String imgPath;
    private String namePerson;
    private String degreePerson;

    public People(){};

    public People(long id, String namePerson, String degreePerson, String imgPath){
        this.id = id;
        this.namePerson = namePerson;
        this.degreePerson = degreePerson;
        this.imgPath = imgPath;
    }

    public long getId(){return id;}

    public String getNamePerson(){return this.namePerson;}

    public String getDegreePerson(){return this.degreePerson;}

    public String getImgPath(){return this.imgPath;}

}
