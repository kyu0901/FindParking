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
 * Created by user on 2017-05-25.
 */

public class RequestCoordinate {
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;

    //주차 자리에 대한 모든 좌표
    public String RequestCoordinate(String MacAddress){
        try{
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://220.67.128.228:8080/RequestCoordinate.php?");

            //add your data
            nameValuePairs = new ArrayList<>(1);

            nameValuePairs.add(new BasicNameValuePair("MacAddress", MacAddress));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute
            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);

            System.out.println("re Coordinate : " + response);

            Log.i("msg : ", response);
            return response;

        }catch(Exception e) {
            return "Invalid";
        }
    }
}