package xiaowang.unclidelookup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置为无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置为全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        //设置布局文件
        setContentView(R.layout.activity_welcome);

        //设置启动界面的动画效果
        ImageView welcome = (ImageView)findViewById(R.id.welcome);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(2000);
        welcome.startAnimation(alphaAnimation);
        //用CountDownTimer类进行倒计时，4000代表开始4秒后调用finish()
        new CountDownTimer(2500, 500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                //进行页面跳转
                Intent login = new Intent();
                login.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(login);
                WelcomeActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }.start();
    }

}
