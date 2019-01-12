package xiaowang.unclidelookup;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import MyUtil.MessageList;
import MyUtil.MyEditText;

public class UserDetail extends AppCompatActivity {
    private EditText personality;
    private Button back, alter;
    private MyEditText name,telephone;
    private TextView account;
    private RadioGroup gender;
    private String text_gender;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        getView();
    }

    public void getView() {
        String tmp = "";
        back = (Button)findViewById(R.id.user_back);
        alter = (Button)findViewById(R.id.alter);
        account = (TextView)findViewById(R.id.text_account);
        name = (MyEditText)findViewById(R.id.text_name);
        telephone = (MyEditText)findViewById(R.id.text_telephone);
        personality = (EditText)findViewById(R.id.personality);
        gender = (RadioGroup)findViewById(R.id.gender);
        back.setOnClickListener(new MyBackListener());
        personality.addTextChangedListener(new MyTextListener());
        name.setText(MessageList.currentUser.name);
        name.requestFocus();
        name.setSelection(name.length());
        account.setText(MessageList.currentUser.account);
        telephone.setText(MessageList.currentUser.telephone);
        if ((tmp = MessageList.currentUser.personality) != null) {
            personality.setText(tmp);
        }
        if ((tmp = MessageList.currentUser.gender) != null) {
            switch (tmp) {
                case "男": gender.check(R.id.man);break;
                case "女": gender.check(R.id.woman);break;
                default: break;
            }
        }
        gender.setOnCheckedChangeListener(new MyRadioGroupListener());
        alter.setOnClickListener(new MyAlterListener());
    }

    private class MyTextListener implements TextWatcher {
        //EditView自动判断输入长度
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
        }
        @Override
        public void afterTextChanged(Editable s)
        {
            String content = personality.getText().toString();
            int l = content.length();
            if (l > 50) {
                personality.setText(content.substring(0, 30));
                personality.setSelection(30);//EditView设置光标到最后
            }
        }
    }

    private class MyBackListener implements View.OnClickListener {
        public void onClick(View view) {
            UserDetail.this.finish();
        }
    }

    private class MyAlterListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isRight()) {
                alterDatabase();
                renewMessage();
                Toast.makeText(UserDetail.this, "信息修改成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //判断用户输入是否规范
    public Boolean isRight() {
        if (name.getText().toString().length() < 1) {
            name.setError("name is requested.");
            name.requestFocus();
            return false;
        }
        if (telephone.getText().toString().length() < 8) {
            telephone.setError("the number is too short.");
            telephone.requestFocus();
            return false;
        }
        if (telephone.getText().toString().length() > 15) {
            telephone.setError("the number is too long.");
            telephone.requestFocus();
            return false;
        }
        return true;
    }

    //将用户修改好的信息更新到用户数据库中
    public void alterDatabase() {
        SQLiteDatabase userDatabase = SQLiteDatabase.openOrCreateDatabase(LoginActivity.DATA_PATH1, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name.getText().toString());
        contentValues.put("telephone", telephone.getText().toString());
        contentValues.put("personality", personality.getText().toString());
        if (text_gender != null) {
            contentValues.put("gender", text_gender);
        }
        userDatabase.update("users", contentValues,
                "account=?", new String[]{account.getText().toString()});
    }

    //将当前用户的数据信息做出更新
    public void renewMessage() {
            MessageList.currentUser.name = name.getText().toString();
            MessageList.currentUser.telephone = telephone.getText().toString();
            MessageList.currentUser.gender = text_gender;
            MessageList.currentUser.personality = personality.getText().toString();
    }

    private class MyRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.man) {
                text_gender = "男";
            } else {
                text_gender = "女";
            }
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
