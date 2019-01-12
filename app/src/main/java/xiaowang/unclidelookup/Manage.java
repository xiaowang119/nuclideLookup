package xiaowang.unclidelookup;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import MyUtil.Model;
import MyUtil.MyAdapter;

public class Manage extends AppCompatActivity {
    private ActionBar actionBar;
    private AlertDialog.Builder sure;
    private Button delete;
    private CheckBox selectAll;
    private ListView userView;
    //private List<Map<String, String>> userList;
    private List<Model> userList;
    //private SimpleAdapter userAdapter;
    private MyAdapter userAdapter;
    SQLiteDatabase userDatabase;
    Cursor userMsg;
    //监听来源
    private Boolean isFromItem = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        init();
        loadUser();
        userAdapter = new MyAdapter(userList, this, new MyCheckListener());
        userView.setAdapter(userAdapter);
    }

    public void init() {
        actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        sure = new AlertDialog.Builder(Manage.this);
        sure.setTitle("正在删除");
        sure.setMessage("删除后将无法恢复，是否删除？");
        sure.setPositiveButton("确认", new sureDelete());
        sure.setNegativeButton("取消", null);
        delete = (Button)findViewById(R.id.delete);
        selectAll = (CheckBox)findViewById(R.id.selectAll);
        userView = (ListView)findViewById(R.id.user_list);
        selectAll.setOnCheckedChangeListener(new MySelectAllListener());
        delete.setOnClickListener(new MyDeleteListener());

    }

    public void loadUser() {
        userDatabase = SQLiteDatabase.openOrCreateDatabase(LoginActivity.DATA_PATH1, null);
        userList = new ArrayList<>();

        userMsg = userDatabase.rawQuery("select * from users", null);
        while (userMsg.moveToNext()) {
            String name = userMsg.getString(userMsg.getColumnIndex("name"));
            String account = userMsg.getString(userMsg.getColumnIndex("account"));
            //Map<String, String> userMap = new HashMap<>();
            //userMap.put("name", name);
            //userMap.put("account", account);
            //userList.add(userMap);
            Model userModel = new Model();
            userModel.setName(name);
            userModel.setAccount(account);
            userList.add(userModel);
        }

        /*userAdapter = new SimpleAdapter(this,
                userList, R.layout.user_item,
                new String[]{"name", "account"},
                new int[]{R.id.name_item, R.id.account_item});*/
    }

    public interface AllCheckListener {
        void onCheckedChanged(Boolean b);
        void setButton(Boolean b);
    }

    private class MyCheckListener implements AllCheckListener {
        public void onCheckedChanged(Boolean b) {
            //根据不同的情况对maincheckbox做处理
            if (!b && !selectAll.isChecked()) {
                return;
            } else if (!b && selectAll.isChecked()) {
                isFromItem = true;
                selectAll.setChecked(false);
            } else if (b) {
                isFromItem = true;
                selectAll.setChecked(true);
            }
        }
        @Override
        public void setButton(Boolean b) {
            if (b) {
                delete.setBackgroundColor(getResources().getColor(R.color.after));
            } else {
                delete.setBackgroundColor(getResources().getColor(R.color.before));
            }
        }
    }

    private class MySelectAllListener implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            //当监听来源为点击item改变maincbk状态时不在监听改变，防止死循环
            if (isFromItem) {
                isFromItem = false;
                return;
            }
            //改变数据
            for (Model model : userList) {
                model.setIscheck(b);
            }
            //刷新listview
            userAdapter.notifyDataSetChanged();

            //根据选择情况设置按钮颜色
            if (b) delete.setBackgroundColor(getResources().getColor(R.color.after));
            else delete.setBackgroundColor(getResources().getColor(R.color.before));
        }
    }

    private class MyDeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sure.show();
        }
    }

    private class sureDelete implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int n) {
            if (selectAll.isChecked()) {
                userDatabase.delete("users", null, null);
            }else {

                //在for循环中实用remove会出错
               /* for (Model model: userList) {
                    if (model.ischeck()) {
                        userDatabase.delete("users", "account=?", new String[]{model.getAccount()});
                        userList.remove(model);
                    }
                }*/
                Model tmp;
                Iterator <Model> modelIterator = userList.iterator();
                while (modelIterator.hasNext()) {
                    tmp = modelIterator.next();
                    if (tmp.ischeck()) {
                        userDatabase.delete("users", "account=?", new String[]{tmp.getAccount()});
                        modelIterator.remove();
                    }
                }
                userAdapter.notifyDataSetChanged();
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
