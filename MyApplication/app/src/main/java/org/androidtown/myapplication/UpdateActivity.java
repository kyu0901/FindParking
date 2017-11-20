package org.androidtown.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private TextView userID;
    private EditText userPassword;
    private EditText PasswordConfirm;
    private RadioGroup CarKind;
    private RadioGroup CheckDisability;
    private Button FinUpdate;
    private Button Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        final UserInfo userInfo = (UserInfo)getApplicationContext();

        userID = (TextView) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);
        PasswordConfirm = (EditText) findViewById(R.id.PasswordConfirm);
        CarKind = (RadioGroup) findViewById(R.id.CarKind);
        CheckDisability = (RadioGroup) findViewById(R.id.CheckDisability);
        FinUpdate = (Button) findViewById(R.id.FinUpdate);
        Cancel = (Button) findViewById(R.id.Cancel);

        userID.setText(userInfo.getID());
        Log.i("Update", "Id is "+userInfo.getID());

        //장애 유무
        if (userInfo.getHandicap().equalsIgnoreCase("0")){
            CheckDisability.check(R.id.Ability);
        } else if (userInfo.getHandicap().equalsIgnoreCase("1")){
            CheckDisability.check(R.id.Disability);
        }

        //자동차 종류
        if (userInfo.getCar().equalsIgnoreCase("SmallCar")){
            CarKind.check(R.id.SmallCar);
        } else if(userInfo.getCar().equalsIgnoreCase("MidSizeCar")){
            CarKind.check(R.id.MidCar);
        }

        Log.i("Update", " before Click FinUpdate");
        FinUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (userPassword.getText().toString().length() == 0) {
                    Toast.makeText(UpdateActivity.this, "비밀번호를 입력하시오", Toast.LENGTH_SHORT).show();
                    userPassword.requestFocus();
                    return;
                }

                if(PasswordConfirm.getText().toString().length() == 0){
                    Toast.makeText(UpdateActivity.this, "비밀번호 확인을 입력하시오", Toast.LENGTH_SHORT).show();
                    PasswordConfirm.requestFocus();
                    return;
                }

                if(!userPassword.getText().toString().equals(PasswordConfirm.getText().toString())){
                    Toast.makeText(UpdateActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    userPassword.setText("");
                    PasswordConfirm.setText("");
                    userPassword.requestFocus();
                    return;
                }

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }}
