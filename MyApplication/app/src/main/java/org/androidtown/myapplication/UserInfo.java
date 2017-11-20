package org.androidtown.myapplication;

import android.app.Application;

/**
 * Created by hyk on 2017-05-23.
 */

public class UserInfo extends Application{

    private String ID;
    private String Car;
    private String Handicap;


    public String getID(){
        return ID;
    }

    public String getCar(){
        return Car;
    }

    public String getHandicap(){
        return Handicap;
    }

    public void setID(String ID){
        this.ID = ID;
    }

    public void setCar(String Car){
        this.Car = Car;
    }

    public void setHandicap(String Handicap){
        this.Handicap = Handicap;
    }
}
