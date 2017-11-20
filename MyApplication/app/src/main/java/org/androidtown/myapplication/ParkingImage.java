package org.androidtown.myapplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class ParkingImage {

    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    public String ParkingImage(String MacAddress, String Handicap){
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://220.67.128.228:8080/ParkingLot_info.php?");

            //add your data
            nameValuePairs = new ArrayList<>(2);

            nameValuePairs.add(new BasicNameValuePair("MacAddress", MacAddress));
            nameValuePairs.add(new BasicNameValuePair("Handicap", Handicap));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);

            return response;

        }catch(Exception e) {
            return "Error";


        }
    }

}
