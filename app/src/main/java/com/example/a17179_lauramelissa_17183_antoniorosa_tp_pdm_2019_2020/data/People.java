package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;


public class People {

    private String imgPath;
    private String namePerson;
    private String degreePerson;

    public People(){};

    public People(String namePerson, String degreePerson, String imgPath){
        this.namePerson = namePerson;
        this.degreePerson = degreePerson;
        this.imgPath = imgPath;
    }

    public String getNamePerson(){return this.namePerson;}

    public String getDegreePerson(){return this.degreePerson;}

    public String getImgPath(){return this.imgPath;}

}
