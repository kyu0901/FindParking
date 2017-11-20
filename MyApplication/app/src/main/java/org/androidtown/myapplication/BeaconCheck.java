package org.androidtown.myapplication;

import android.util.Log;

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

/**
 * Created by hyk on 2017-05-15.
 */

public class BeaconCheck {
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    public String BeaconCheck(String MacAddress){
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://220.67.128.228:8080/IsBeacon.php");

            //add your data
            nameValuePairs = new ArrayList<>(2);

            nameValuePairs.add(new BasicNameValuePair("MacAddress", MacAddress));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("Response : " + response);

            Log.i("msg : ", response);


            return response;

        }catch(Exception e) {
            return "Error";


        }
    }


}
