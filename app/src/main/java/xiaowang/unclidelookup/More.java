package xiaowang.unclidelookup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;

public class More extends AppCompatActivity {
    private WebView know_more;
    private ActionBar actionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_explore);
        know_more = (WebView)findViewById(R.id.explore_web);

        //设置WebView属性，能够执行Javascript脚本
        know_more.getSettings().setJavaScriptEnabled(true);
        know_more.getSettings().setDefaultTextEncodingName("utf-8");

        Intent msg = getIntent();
        String symbol = msg.getStringExtra("name");

        //文件夹下有一个相关的详情信息html
        know_more.loadUrl("file:///android_asset/" + symbol + ".html");
        actionBar = getSupportActionBar();
        actionBar.setTitle("核处寻-" + symbol);

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
