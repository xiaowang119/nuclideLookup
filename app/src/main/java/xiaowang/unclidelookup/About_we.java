package xiaowang.unclidelookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

public class About_we extends AppCompatActivity {
    private WebView about;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_explore);

        about = (WebView)findViewById(R.id.explore_web);

        //设置WebView属性，能够执行Javascript脚本
        about.getSettings().setJavaScriptEnabled(true);
        about.getSettings().setDefaultTextEncodingName("utf-8");
        //即asserts文件夹下有一个a.html
        about.loadUrl("file:///android_asset/about_we.html");

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.isCheckable())
        {
            item.setCheckable(true);
        }
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
