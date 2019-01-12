package xiaowang.unclidelookup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

public class Explore extends AppCompatActivity {
    private WebView explore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_explore);
        explore = (WebView)findViewById(R.id.explore_web);

        //设置WebView属性，能够执行Javascript脚本
        explore.getSettings().setJavaScriptEnabled(true);
        explore.getSettings().setDefaultTextEncodingName("utf-8");
        //即asserts文件夹下有一个a.html
        explore.loadUrl("file:///android_asset/friendship_links.html");
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
