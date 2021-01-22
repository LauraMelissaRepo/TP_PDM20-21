package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;


public class People {

    private String imgPath;
    private String namePerson;
    private String degreePerson;
    private String lat;
    private String lng;

    public People(){};

    public People(String namePerson, String degreePerson, String imgPath, String lat, String lng){
        this.namePerson = namePerson;
        this.degreePerson = degreePerson;
        this.imgPath = imgPath;
        this.lat = lat;
        this.lng = lng;
    }

    public String getNamePerson(){return this.namePerson;}

    public String getDegreePerson(){return this.degreePerson;}

    public String getImgPath(){return this.imgPath;}

    public String getLat(){return this.lat;}

    public String getLng(){return this.lng;}

}
