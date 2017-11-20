package org.androidtown.myapplication;


import android.app.Activity;

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

//import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by hyk on 2017-05-10.
 */

public class Login {
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    Activity act;

    public String login(String userId, String userPasswd){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://220.67.128.228:8080/Login.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("Id",userId));  // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("Passwd",userPasswd));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response=httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            System.out.println("Response : " + response);

            /*act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("runnable", response);
                }
            });*/

            return response;

        }catch(Exception e){
           return "Error";
        }
    }

}
