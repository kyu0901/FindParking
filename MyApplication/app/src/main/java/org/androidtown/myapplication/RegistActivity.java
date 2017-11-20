package org.androidtown.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

public class RegistActivity extends AppCompatActivity {

    private EditText userID;
    private EditText userPassword;
    private EditText PasswordConfirm;
    private RadioGroup CarKind;
    private RadioGroup CheckDisability;
    private Button FinRegist;
    private Button Cancel;

    private String ID;
    private String Password;
    private String Car;
    private String Handicap;

    private static final String REGISTER_URL = "http://220.67.128.228:8080/join.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        userID = (EditText) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);
        PasswordConfirm = (EditText) findViewById(R.id.PasswordConfirm);
        CarKind = (RadioGroup) findViewById(R.id.CarKind);
        CheckDisability = (RadioGroup) findViewById(R.id.CheckDisability);
        FinRegist = (Button) findViewById(R.id.FinRegist);
        Cancel = (Button) findViewById(R.id.Cancel);

        CarKind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton ck = (RadioButton)findViewById(i);
                if(ck.getText().toString() == "Small")
                    Car = "SmallCar";
                else
                    Car = "MidSizeCar";
            }
        });

        CheckDisability.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton cd = (RadioButton)findViewById(i);
                if(cd.getText().toString() == "Disability")
                    Handicap = "1";
                else
                    Handicap = "0";
            }
        });

        FinRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userID.getText().toString().length() == 0){
                    Toast.makeText(RegistActivity.this, "ID를 입력하시오", Toast.LENGTH_SHORT).show();
                    userID.requestFocus();
                    return;
               }

                if (userPassword.getText().toString().length() == 0) {
                    Toast.makeText(RegistActivity.this, "비밀번호를 입력하시오", Toast.LENGTH_SHORT).show();
                    userPassword.requestFocus();
                    return;
                }

                if(PasswordConfirm.getText().toString().length() == 0){
                    Toast.makeText(RegistActivity.this, "비밀번호 확인을 입력하시오", Toast.LENGTH_SHORT).show();
                    PasswordConfirm.requestFocus();
                    return;
                }

                if(!userPassword.getText().toString().equals(PasswordConfirm.getText().toString())){
                    Toast.makeText(RegistActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    userPassword.setText("");
                    PasswordConfirm.setText("");
                    userPassword.requestFocus();
                    return;
                }

                if(CarKind.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegistActivity.this, "차 종류를 선택하시오", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(CheckDisability.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegistActivity.this, "장애 유무를 체크하시오", Toast.LENGTH_LONG).show();
                    return;
                }

                ID = userID.getText().toString();
                Password = userPassword.getText().toString();

                register(ID, Password, Handicap, Car );

                Intent result = new Intent();
                result.putExtra("ID", userID.getText().toString());

                setResult(RESULT_OK, result);
                finish();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void register(String Id, String Passwd, String Handicap, String CarType){
        class RegisterUser extends AsyncTask<String ,Void, String>{
            ProgressDialog loading;
            RegistRequest rr = new RegistRequest();


            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(RegistActivity.this, "Wait", null, true, true);
            }

            protected void onPostExecute(String s){
                super.onPostExecute(s);
                if (loading != null) {
                    if(loading.isShowing()) {
                        loading.dismiss();
                    }
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            protected String doInBackground(String... params){
                HashMap<String ,String>data = new HashMap<String, String>();
                data.put("Id", params[0]);
                data.put("Passwd", params[1]);
                data.put("Handicap", params[2]);
                data.put("CarType", params[3]);

                String result = rr.sendPostRequest(REGISTER_URL,data);

                return result;
            }
        }

        RegisterUser ru = new RegisterUser();
        ru.execute(Id, Passwd, Handicap, CarType);

    }
}
