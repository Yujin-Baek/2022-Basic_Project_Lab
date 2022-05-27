package kr.ac.cnu.computer.buslocation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;

public class splashActivity extends AppCompatActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                startActivity(new Intent(splashActivity.this, MainActivity.class));
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 2000);
    }
}
