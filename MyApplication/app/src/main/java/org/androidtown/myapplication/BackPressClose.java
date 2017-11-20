package org.androidtown.myapplication;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by hyk on 2017-05-21.
 */

public class BackPressClose {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressClose(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();

            activity.moveTaskToBack(true);  //백그라운드로 이동
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid()); //프로세스 종료
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "뒤로버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
