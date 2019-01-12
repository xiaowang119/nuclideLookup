package xiaowang.unclidelookup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import MyFragment.MyHome;
import MyFragment.MySearch;
import MyFragment.MyTable;
import MyUtil.MessageList;
import DataManage.SonNuclide;
import DataManage.StructDecay;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ThemeActivity extends AppCompatActivity {


    private FragmentTransaction mTransaction;
    /**
     * 四个Fragments，用于底部菜单栏的选择显示
     */
    Fragment searchFragment, tableFragment, homeFragment;
    //为每个页面创建一个标识符
    public static final int VIEW_SEARCH_INDEX = 0;
    public static final int VIEW_TABLE_INDEX = 1;
    public static final int VIEW_HOME_INDEX = 2;
    private int temp_position_index = -1;

    private ImageView mHHead;
    private ListView result;
    SharedPreferences.Editor editor;
    private LinearLayout showBottom;
    public static String very_toxic, high_toxic, middle_toxic, low_toxic;
    public static boolean flag_toxic = false;
    private int view_flag = -1;
    private TextView userName, telephone;
    private Button user, manage;
    private EditText year, month, day, hour, second, mSecond, searchInput;
    private Handler myHandler = new Handler();

    private static SimpleAdapter searchAdapter;
    private Filter searchFilter;
    private Boolean flag_search = false;
    private Boolean flag_home = false;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        initView();
        myHandler.postDelayed(tableProgress, 200);
        editor = getSharedPreferences("data",MODE_PRIVATE).edit();
    }



    //对元素周期表进行初始化的线程
    Runnable tableProgress = new Runnable() {
        @Override
        public void run() {
            Button startTable;
            startTable = findViewById(R.id.startTable);
            startTable.setOnClickListener(new MyOnclickListener());
        }
    };

    //对搜索页面进行初始化的线程
    Runnable searchProgress = new Runnable() {
        @Override
        public void run() {
            Button reSet, submit;

            getDecaysMessage();
            reSet = findViewById(R.id.reSet);
            submit = findViewById(R.id.submit);
            searchInput = findViewById(R.id.search_input);
            year = findViewById(R.id.year);
            month = findViewById(R.id.month);
            day = findViewById(R.id.day);
            hour = findViewById(R.id.hour);
            second = findViewById(R.id.second);
            mSecond = findViewById(R.id.msecond);
            result = findViewById(R.id.result_list);
            showBottom = findViewById(R.id.show_bottom);
            reSet.setOnClickListener(new MyOnclickListener());
            searchInput.addTextChangedListener(new MySearchListener());
            result.setAdapter(searchAdapter);
            result.setOnItemClickListener(new MyListListener());
            submit.setOnClickListener(new MyOnclickListener());
        }
    };

    //对用户页面进行初始化的线程
    Runnable homeProgress = new Runnable() {
        @Override
        public void run() {
            Button explore, loginOut, about, tool;

            mHHead = findViewById(R.id.head);
            user = findViewById(R.id.user_detail);
            explore = findViewById(R.id.explore);
            explore.setOnClickListener(new MyOnclickListener());
            loginOut = findViewById(R.id.back_login);
            loginOut.setOnClickListener(new MyOnclickListener());
            about = findViewById(R.id.about);
            about.setOnClickListener(new MyOnclickListener());
            manage = findViewById(R.id.manager_tool);
            tool = findViewById(R.id.tool);
            userName = findViewById(R.id.user_name);
            telephone = findViewById(R.id.telephone);
            tool.setOnClickListener(new MyOnclickListener());
            setMessage();
        }
    };

    //获得用户的相关信息并进行相应的设置
    private void setMessage() {
        //设置圆形头像和用户信息
        if (MessageList.currentUser.isManager) {
            user.setVisibility(View.GONE);
            manage.setVisibility(View.VISIBLE);
            manage.setOnClickListener(new MyOnclickListener());
            userName.setText("管理员");
            telephone.setText("183****6931");
            Glide.with(ThemeActivity.this).load(R.drawable.manger)
                    .bitmapTransform(new CropCircleTransformation(ThemeActivity.this))
                    .into(mHHead);
        }else {
            manage.setVisibility(View.GONE);
            user.setVisibility(View.VISIBLE);
            user.setOnClickListener(new MyOnclickListener());
            userName.setText(MessageList.currentUser.name);
            String tele_text = MessageList.currentUser.telephone;
            String tele_show = tele_text.substring(0,3) + "****" + tele_text.substring(tele_text.length()-4);
            telephone.setText(tele_show);
            if ("女".equals(MessageList.currentUser.gender)) {
                Glide.with(ThemeActivity.this).load(R.drawable.head_woman)
                        .bitmapTransform(new CropCircleTransformation(ThemeActivity.this))
                        .into(mHHead);
            } else if ("男".equals(MessageList.currentUser.gender)){
                Glide.with(ThemeActivity.this).load(R.drawable.head_man)
                        .bitmapTransform(new CropCircleTransformation(ThemeActivity.this))
                        .into(mHHead);
            } else {
                Glide.with(ThemeActivity.this).load(R.drawable.unknow)
                        .bitmapTransform(new CropCircleTransformation(ThemeActivity.this))
                        .into(mHHead);
            }
        }
    }

    //对界面进行初始化
    public void initView() {
        view_flag = 2;
        searchFragment = MySearch.getNewInstance();
        tableFragment = MyTable.getNewInstance();
        homeFragment = MyHome.getNewInstance();
        //将元素周期表作为默认页面进行显示
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.id_fragment_content, tableFragment);
        mTransaction.commit();
    }

    //定义当底部菜单栏被点击时所发生的动作
    public void switchView(View view) {
        switch (view.getId()) {
            case R.id.search:
                if (MessageList.isLoaded) {
                    if (temp_position_index != VIEW_SEARCH_INDEX) {
                        //显示
                        view_flag = 1;
                        mTransaction = getSupportFragmentManager().beginTransaction();
                        mTransaction.replace(R.id.id_fragment_content, searchFragment);
                        mTransaction.commit();
                        //searchHandler.post(searchProgress);
                        if (!flag_search) {
                            myHandler.post(searchProgress);
                            flag_search = true;
                        }
                    }
                    temp_position_index = VIEW_SEARCH_INDEX;
                    break;
                }else {
                    Toast.makeText(ThemeActivity.this, "数据加载中，请稍等！", Toast.LENGTH_SHORT).show();
                }
            case R.id.table:
                if (temp_position_index != VIEW_TABLE_INDEX) {
                    //显示
                    view_flag = 2;
                    mTransaction = getSupportFragmentManager().beginTransaction();
                    mTransaction.replace(R.id.id_fragment_content, tableFragment);
                    mTransaction.commit();
                }
                temp_position_index = VIEW_TABLE_INDEX;
                break;
            case R.id.home:
                if (temp_position_index != VIEW_HOME_INDEX) {
                    //显示
                    view_flag = 3;
                    mTransaction = getSupportFragmentManager().beginTransaction();
                    mTransaction.replace(R.id.id_fragment_content, homeFragment);
                    mTransaction.commit();
                    //homeHandler.post(homeProgress);
                    if (!flag_home) {
                        myHandler.post(homeProgress);
                        flag_home = true;
                    }
                }
                temp_position_index = VIEW_HOME_INDEX;
                break;
        }
    }

    //获取放射性核素信息
    public void getDecaysMessage() {
        ArrayList<HashMap<String, String>> searchList  = MessageList.myArrayList;
        //String s = "链表长度为：" + searchList.size();
        //Toast.makeText(ThemeActivity.this, s, Toast.LENGTH_SHORT).show();
        searchAdapter = new SimpleAdapter(ThemeActivity.this, searchList, R.layout.simple_decays_element,
                new String[]{"element_symbol", "element_mass"}, new int[]{R.id.lv_part1, R.id.lv_part2});
        searchFilter = searchAdapter.getFilter();
    }

    //对搜索框中的信息进行监听处理
    private class MySearchListener implements TextWatcher {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                showBottom.setVisibility(View.VISIBLE);
                result.setVisibility(View.GONE);
            } else if (s.length() == 1) {
                String regex = "^[A-Za-z0-9]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(s.toString());
                if (!matcher.matches()) {
                    StructDecay element = findByChineseName(s.toString());
                    if (element != null) {
                        String name = element.getParentName();
                        searchFilter.filter(name);
                        showBottom.setVisibility(View.GONE);
                        result.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ThemeActivity.this, "无此放射性核素！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("myTag", s.toString());
                    searchFilter.filter(s);
                    showBottom.setVisibility(View.GONE);
                    result.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d("myTag", s.toString());
                searchFilter.filter(s);
                showBottom.setVisibility(View.GONE);
                result.setVisibility(View.VISIBLE);
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        public void afterTextChanged(Editable s) {
        }
    }

    //对搜索结果的核素列表的点击事件进行监听处理
    private class MyListListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Intent intent = new Intent(ThemeActivity.this, DecaysDetail.class);
            @SuppressWarnings("unchecked")
            HashMap<String, String> hashMap = (HashMap<String, String>) parent.getItemAtPosition(position);
            intent.putExtra("element_symbol", hashMap.get("element_symbol"));
            intent.putExtra("element_mass", hashMap.get("element_mass"));
            startActivity(intent);
        }
    }

    //对各个按钮的点击事件进行监听处理
    private class MyOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.startTable :
                    Intent startTable = new Intent();
                    startTable.setClass(ThemeActivity.this, TableOfElements.class);
                    startActivity(startTable);
                    break;
                case R.id.reSet :
                    setEmpty();
                    break;
                case R.id.explore :
                    Intent explore = new Intent();
                    explore.setClass(ThemeActivity.this, Explore.class);
                    startActivity(explore);
                    break;
                case R.id.re_sure :
                    break;
                case R.id.about :
                    Intent about = new Intent();
                    about.setClass(ThemeActivity.this, About_we.class);
                    startActivity(about);
                    break;
                case R.id.user_detail :
                    Intent user = new Intent();
                    user.setClass(ThemeActivity.this, UserDetail.class);
                    startActivity(user);
                    break;
                case R.id.manager_tool :
                    Intent manage = new Intent();
                    manage.setClass(ThemeActivity.this, Manage.class);
                    startActivity(manage);
                    break;
                case R.id.tool :
                    Intent tools = new Intent();
                    tools.setClass(ThemeActivity.this, Tools.class);
                    startActivity(tools);
                    break;
                case R.id.back_login :
                    Intent out = new Intent();
                    out.setClass(ThemeActivity.this, LoginActivity.class);
                    editor.putBoolean("login", false);
                    editor.commit();
                    startActivity(out);
                    ThemeActivity.this.finish();
                    break;
                case R.id.submit :
                    intoHalfDecay();
                    break;
            }
        }
    }

    //重置的实现
    private void setEmpty() {
        searchInput.setText("");
        year.setText("");
        month.setText("");
        day.setText("");
        hour.setText("");
        second.setText("");
        mSecond.setText("");
    }

    //通过核素中文名进行查询的方法
    public StructDecay findByChineseName(String chineseName) {
        for (int i = 0; i < MessageList.decaysList.size(); i++) {
            StructDecay element = MessageList.decaysList.elementAt(i);
            if (element.getParentChineseName().equals(chineseName))
                return element;
        }
        return null;
    }

    //通过半衰期查询的方法
    public StructDecay findByHalfLife(String life) {
        String ziheHalfString = "";
        Vector<SonNuclide> sonNuclides;
        for (int i = 0; i < MessageList.decaysList.size(); i++) {
            sonNuclides = MessageList.decaysList.elementAt(i).getSoNuclides();
            for (int j = 0; j < sonNuclides.size(); j++) {
                ziheHalfString = sonNuclides.elementAt(j).getHalfLife();
                if (ziheHalfString.equalsIgnoreCase(life))
                    return MessageList.decaysList.elementAt(i);
            }
        }
        return null;
    }

    //为半衰期查到的核素打开一个详情页面
    private void intoHalfDecay() {
        String tmp, life = "";
        int count = 0;
        if ((tmp = year.getText().toString()).length() > 0) {
            count++;
            life = tmp + " Y";
        }
        if ((tmp = month.getText().toString()).length() > 0) {
            count++;
            life = tmp + " M";
        }
        if ((tmp = day.getText().toString()).length() > 0) {
            count++;
            life = tmp + " D";
        }
        if ((tmp = hour.getText().toString()).length() > 0) {
            count++;
            life = tmp + " H";
        }
        if ((tmp = second.getText().toString()).length() > 0) {
            count++;
            life = tmp + " S";
        }
        if ((tmp = mSecond.getText().toString()).length() > 0) {
            count++;
            life = tmp + " MS";
        }
        if (count != 1) {
            Toast.makeText(ThemeActivity.this, "请选择一个单位填写！", Toast.LENGTH_SHORT).show();
        } else {
            StructDecay element = findByHalfLife(life);
            if (element != null) {
                String symbol = element.getParentName();
                String mass = element.getParentMass();
                Intent intent = new Intent(ThemeActivity.this, DecaysDetail.class);
                intent.putExtra("element_symbol", symbol);
                intent.putExtra("element_mass", mass);
                startActivity(intent);
            }else {
                Toast.makeText(ThemeActivity.this, "无符合条件的核素！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //重写onKeyDown()方法，实现返回键退出后台运行
    //点击返回键返回桌面而不是退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //在用户回归页面时对页面信息进行重设
        switch (view_flag) {
            case 1: setEmpty();break;
            case 3: setMessage();break;
            default: break;
        }
    }


}
