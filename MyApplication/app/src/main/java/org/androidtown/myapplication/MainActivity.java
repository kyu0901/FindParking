package org.androidtown.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {

    public static String Id ="User_Id";
    private String Passwd = "User_Passwd";
    private static final String LogIn_URL = "http://220.67.128.228:8080/Login.php";


    private EditText userID;
    private EditText userPassword;
    private Button Login;
    private Button Regist;

    private BackPressClose backPressClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final UserInfo userInfo = (UserInfo)getApplicationContext();

        userID = (EditText) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);
        Regist = (Button) findViewById(R.id.Regist);
        Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivityForResult(intent, 1000);

            }
        });



        Login = (Button) findViewById(R.id.Login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Id = userID.getText().toString();
                Passwd = userPassword.getText().toString();


                userInfo.setID(Id);
                UserLogin(Id, Passwd);

            }
        });

        backPressClose = new BackPressClose(this);
    }

    private void UserLogin(final String user_Id, final String user_Passwd){
        class UserLoginClass extends AsyncTask<String, Void, String>{
            ProgressDialog loading;


            final UserInfo userInfo = (UserInfo)getApplicationContext();

            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Wait", null, true, true);
            }

            protected void onPostExecute(String s){
                super.onPostExecute(s);
                if (loading != null) {
                    Log.i("Id : ",user_Id);
                    if(loading.isShowing()) {
                        loading.dismiss();
                    }
                }
                Log.i("msg : ", s);
                if(s.equalsIgnoreCase("success")){
                    String msg = "success";
                    Log.i("onPostExecute : ", msg);
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivityForResult(intent, 2000);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }
            }

            protected String doInBackground(String... params){
                HashMap<String, String> data = new HashMap<>();
                data.put("user_Id", params[0]);
                data.put("user_Passwd", params[1]);

                Login li = new Login();

                String result = li.login(user_Id,user_Passwd);

                Log.i("Connect?? :", result);

                userInfo.setHandicap(result.split(" ")[1]);
                userInfo.setCar(result.split(" ")[2]);

                Log.i("Car" , " is" +userInfo.getCar() );

                return result.split(" ")[0];

            }

        }
        UserLoginClass ulc = new UserLoginClass();
        ulc.execute(user_Id, user_Passwd);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000 && resultCode == RESULT_OK){
            Toast.makeText(MainActivity.this, "회원가입 완료", Toast.LENGTH_SHORT).show();
            userID.setText(data.getStringExtra("ID"));
            userPassword.requestFocus();
        }

        if(requestCode == 2000 && resultCode == RESULT_OK){
            Toast.makeText(MainActivity.this, "현재 위치 표시", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        backPressClose.onBackPressed();
    }
}
