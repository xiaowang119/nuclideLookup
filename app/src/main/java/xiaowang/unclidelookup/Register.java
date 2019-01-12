package xiaowang.unclidelookup;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    private EditText account, password1, password2, telephone, name;
    private Button register;
    private Boolean isExist = false;
    private List<TextView> textList;
    private String ac, pa1, pa2, te, na;
    SQLiteDatabase userDatabase = SQLiteDatabase.openOrCreateDatabase(LoginActivity.DATA_PATH1, null);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        account = (EditText)findViewById(R.id.re_account);
        password1 = (EditText)findViewById(R.id.re_password1);
        password2 = (EditText)findViewById(R.id.re_password2);
        telephone = (EditText)findViewById(R.id.re_telephone);
        name = (EditText)findViewById(R.id.re_name);
        telephone.setOnEditorActionListener(new MyEditListener());
        register = (Button) findViewById(R.id.re_sure);
        account.addTextChangedListener(new MyAccountListener());
        register.setOnClickListener(new MyRegisterListener());
    }

    private class MyRegisterListener implements View.OnClickListener {
        public void onClick(View view) {
            getValue();
            if (inputIsRight() && !isExist) {
                writeToDatabase();
            }
        }
    }

    public void getValue() {
        textList = new ArrayList<>();
        textList.add(account);
        textList.add(password1);
        textList.add(password2);
        textList.add(name);
        textList.add(telephone);
        ac = account.getText().toString();
        pa1 = password1.getText().toString();
        pa2 = password2.getText().toString();
        te = telephone.getText().toString();
        na = name.getText().toString();
    }

    public boolean inputIsRight() {
        int index = 0;
        for (TextView text : textList) {
            if (text.getText().toString().equals("")) {
                text.setError("请输入完整信息！");
                text.requestFocus();
                return false;
            }
            if (index++ < 3 && text.getText().toString().length() < 6) {
                text.setError("输入内容太短！");
                text.requestFocus();
                return false;
            }
        }
        if (!pa1.equals(pa2)) {
            password2.setError("两次密码不一致！");
            password2.requestFocus();
            return false;
        }
        return true;
    }

    public void writeToDatabase() {
            userDatabase.execSQL("insert into users " +
                            "(account, password, telephone, name) values (?, ?, ?, ?)",
                    new String[]{ac, pa1, te, na});
            Toast.makeText(Register.this, "注册成功，请登录!", Toast.LENGTH_SHORT).show();
            finish();
    }

    public class MyAccountListener implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 5) {
                Cursor cursor = userDatabase.rawQuery("select * from users where account like ?", new String[]{s.toString()} );
                if (cursor.getCount() > 0) {
                    isExist = true;
                    account.setError("此账号已存在！");
                    account.requestFocus();
                }else {
                    isExist = false;
                }
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class MyEditListener implements TextView.OnEditorActionListener {
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                getValue();
                if (inputIsRight() || !isExist) {
                    writeToDatabase();
                }
                return true;
            }
            return false;
        }
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
