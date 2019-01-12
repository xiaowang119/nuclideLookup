package xiaowang.unclidelookup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import MyUtil.DisplayUtils;
import MyUtil.MyEditText;

public class Recover extends AppCompatActivity {
    private Button sure;
    private EditText account, telephone;
    private TextView show;
    List<View> excludeView;
    SQLiteDatabase userDatabase = SQLiteDatabase.openOrCreateDatabase(LoginActivity.DATA_PATH1, null);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        init();
    }

    public void init() {
        sure = (Button)findViewById(R.id.recover_sure);
        show = (TextView)findViewById(R.id.password_view);
        account = (EditText)findViewById(R.id.find_account);
        telephone = (EditText)findViewById(R.id.find_telephone);
        telephone.setOnEditorActionListener(new MyEditListener());
        sure.setOnClickListener(new MyRecoverListener());
        excludeView = new ArrayList<>();
        excludeView.add(account);
        excludeView.add(telephone);
    }

    private void recover() {
        if (!sure.getText().toString().equals("") && !telephone.getText().toString().equals("")) {
            Cursor cursor = userDatabase.rawQuery("select * from users where account like ?",
                    new String[]{account.getText().toString()} );
            if (cursor.getCount() < 1) {
                Toast.makeText(Recover.this, "无此账号！", Toast.LENGTH_SHORT).show();
            }else {
                cursor.moveToFirst();
                if (cursor.getString(cursor.getColumnIndex("telephone")).equals(telephone.getText().toString())) {
                    show.setText(cursor.getString(cursor.getColumnIndex("password")));
                    show.setVisibility(View.VISIBLE);
                }else {
                    Toast.makeText(Recover.this, "信息错误！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class MyRecoverListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            recover();
        }
    }

    private class MyEditListener implements TextView.OnEditorActionListener {
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                recover();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev, excludeView);
        return super.dispatchTouchEvent(ev);
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
