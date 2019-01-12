package xiaowang.unclidelookup;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import MyUtil.DisplayUtils;
import MyUtil.MessageList;
import MyUtil.MyEditText;
import DataManage.DataBaseOperate;
import DataManage.StructDecay;

/**
 * A login screen that offers login via account/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     * 用作测试的用户信息
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "113744:123456", "113744:654321"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            showText(s);
        }
    };
    private Message msg;

    // UI references.
    private Button login, register, recoverPasswd;
    private String identity = null;
    private RadioGroup radioGroup;
    private RadioButton user, manager;
    private ProgressDialog dialog;
    private MyEditText mAccountView, mPasswordView;
    private DataBaseOperate dataBaseOperate;
    private Vector<StructDecay> listDecays;
    private List<View> excludeView;
    private ArrayList<HashMap<String, String>> searchList = new ArrayList<>();
    public SQLiteDatabase sqLiteDatabase;
    private Boolean dataFlag = false;
    private Boolean passwdFlag = true;
    public static String DATA_PATH1 = "/data/data/xiaowang.unclidelookup/databases/users.db";
    public static String DATA_PATH2 = "/data/data/xiaowang.unclidelookup/databases/data.db";
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //加载用户数据库
        loadUserData();
        //提前加载必要的数据库
        loadSqlDataFile();
        loadENSDFDatabase();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataFromEDSDFParser();
                ENSDFdataAddToList();
                MessageList.myArrayList = searchList;
                dataFlag = true;
                MessageList.isLoaded = true;
            }
        }).start();

        init();
    }

    private void init() {
        //得到各个控件对象，并添加对应的监听器
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new MyRadioGroup());
        user = (RadioButton)findViewById(R.id.user_bt);
        manager = (RadioButton)findViewById(R.id.manager_bt);
        mAccountView = (MyEditText)findViewById(R.id.account);
        mPasswordView = (MyEditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new MyEditListener());
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new MyOnclickListener());
        register = (Button)findViewById(R.id.regist_bt);
        register.setOnClickListener(new MyOnclickListener());
        recoverPasswd = (Button)findViewById(R.id.pass_recover_bt);
        recoverPasswd.setOnClickListener(new MyOnclickListener());
        excludeView = new ArrayList<>();
        excludeView.add(mAccountView);
        excludeView.add(mPasswordView);
        excludeView.add(radioGroup);
        //用于存储少量信息，可保留到下一次启动软件
        editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        pref = getSharedPreferences("data",MODE_PRIVATE);

        //判断是否已经登录过,登陆过则自动登录
        if (pref.getBoolean("login", false)) {
            mAccountView.setText(pref.getString("account", ""));
            mPasswordView.setText(pref.getString("password", ""));
            if (pref.getBoolean("isManager", false)) {
                manager.setChecked(true);
            }else {
                user.setChecked(true);
            }
            msgHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    attemptLogin();
                }
            }, 1000);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        //有登录任务正在进行，则直接返回；否则新建一个登录任务
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(account, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (show) {
        dialog = new ProgressDialog(this);
        //设置进度条风格，风格为圆形，旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置ProgressDialog 标题
        dialog.setTitle("登录");
        //设置ProgressDialog 提示信息
        dialog.setMessage("身份验证中...");
        //设置ProgressDialog 标题图标
        dialog.setIcon(R.drawable.checklist);
        //设置ProgressDialog 的进度条是否不明确
        dialog.setIndeterminate(false);
        //设置ProgressDialog 是否可以按退回按键取消
        dialog.setCancelable(false);
        //显示
        dialog.show();
        hideKeyBoard();
        } else {
            if (dialog != null) dialog.cancel();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mAccount;
        private final String mPassword;

        public UserLoginTask(String account, String password) {
            mAccount = account;
            mPassword = password;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            if (identity.equals("user")) {
                Cursor cursor = sqLiteDatabase.rawQuery("select * from users where account like ?", new String[]{mAccount});
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    editor.putString("account", cursor.getString(cursor.getColumnIndex("account")));
                    editor.putString("password", cursor.getString(cursor.getColumnIndex("password")));
                    MessageList.currentUser.name = cursor.getString(cursor.getColumnIndex("name"));
                    MessageList.currentUser.account = cursor.getString(cursor.getColumnIndex("account"));
                    MessageList.currentUser.password = cursor.getString(cursor.getColumnIndex("password"));
                    MessageList.currentUser.telephone = cursor.getString(cursor.getColumnIndex("telephone"));
                    MessageList.currentUser.gender = cursor.getString(cursor.getColumnIndex("gender"));
                    MessageList.currentUser.personality = cursor.getString(cursor.getColumnIndex("personality"));

                    if (!MessageList.currentUser.password.equals(mPassword)) {
                        showProgress(false);
                        passwdFlag = false;
                        //msg = new Message();
                        //msg.obj = "密码错误，请重新输入！";
                        //msgHandler.sendMessage(msg);
                        cursor.close();
                        return false;
                    }
                }else {
                    showProgress(false);
                    msg = new Message();
                    msg.obj = "账号不存在，请先注册！";
                    msgHandler.sendMessage(msg);
                    cursor.close();
                    return false;
                }
            } else {
                int flag = 0;
                for (String credential : DUMMY_CREDENTIALS) {
                    String[] pieces = credential.split(":");
                    if (pieces[0].equals(mAccount) && pieces[1].equals(mPassword)) {
                        // Account exists, return true if the password matches.
                        flag = 1;
                        editor.putString("account", pieces[0]);
                        editor.putString("password", pieces[1]);
                    }
                }
                if (flag == 0) {
                    showProgress(false);
                    msg = new Message();
                    msg.obj = "无此管理员！";
                    msgHandler.sendMessage(msg);
                    return false;
                }
            }
            try {

                if (pref.getBoolean("login", false)) {
                    // Simulate network access.
                    Thread.sleep(2000);
                    Intent menu = new Intent();
                    int sum = 0;
                    menu.setClass(LoginActivity.this, ThemeActivity.class);
                    while (!dataFlag) {
                        Thread.sleep(100);
                        sum++;
                        if (sum >= 25) {
                            /*showProgress(false);
                            msg = new Message();
                            msg.obj = "数据库加载失败，请重新登录！";
                            msgHandler.sendMessage(msg);
                            return false;*/
                            break;
                        }
                    }
                    startActivity(menu);
                    //只有当sdk版本为5及其以上时才支持淡入淡出
                    if(Build.VERSION.SDK_INT >= 5) {
                        LoginActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }

                    msgHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 9000);
                }else {
                    // Simulate network access.
                    Thread.sleep(2000);
                    Intent menu = new Intent();
                    int sum = 0;
                    menu.setClass(LoginActivity.this, ThemeActivity.class);
                    while (!dataFlag) {
                        Thread.sleep(100);
                        sum++;
                        if (sum >= 25) {
                            showProgress(false);
                            msg = new Message();
                            msg.obj = "数据库加载失败，请重试！";
                            msgHandler.sendMessage(msg);
                            return false;
                        }
                    }
                    startActivity(menu);
                    //只有当sdk版本为5及其以上时才支持淡入淡出
                    if (Build.VERSION.SDK_INT >= 5) {
                        LoginActivity.this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                    editor.putBoolean("login", true);
                    editor.commit();
                    finish();
                }
            } catch (InterruptedException e) {
                showProgress(false);
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                finish();
            } else {
                if (!passwdFlag) {
                    mPasswordView.setError("the password is fault !");
                    mPasswordView.requestFocus();
                }
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void loadUserData() {
        String dirPath = "/data/data/xiaowang.unclidelookup/databases";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,"users.db");
        try{
            if(!file.exists()) {
                file.createNewFile();
                InputStream is = this.getResources().openRawResource(R.raw.users);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                fos.write(buffer);
                //Toast.makeText(LoginActivity.this, "用户数据库加载完成！" , Toast.LENGTH_SHORT).show();
                is.close();
                fos.close();
            }
            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(DATA_PATH1, null);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadSqlDataFile() {
        String dirPath = "/data/data/xiaowang.unclidelookup/databases";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,"element.db");
        try{
            if(!file.exists()) {
                file.createNewFile();
                InputStream is = this.getResources().openRawResource(R.raw.element);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                fos.write(buffer);
                is.close();
                fos.close();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadENSDFDatabase() {
        String dirPath = "/data/data/xiaowang.unclidelookup/databases";
        //String s = Environment.getExternalStorageDirectory().getAbsolutePath();
        //String dirPath = s + "/DataBases";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir,"data.db");
        try{
            if(!file.exists()) {
                file.createNewFile();
                InputStream is = this.getResources().openRawResource(R.raw.data);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                fos.write(buffer);
                is.close();
                fos.close();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Log.d("test", "loadENSDFDatabase: ");
    }

    //加载衰变核素数据库
    private void loadDataFromEDSDFParser() {
        dataBaseOperate = new DataBaseOperate(DATA_PATH2, false);
        listDecays = dataBaseOperate.getAll();
        MessageList.decaysList = listDecays;
        Log.d("test", "查询数据源大小： "+listDecays.size());
    }

    //将衰变核素添加到显示列表中去
    public void ENSDFdataAddToList() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("element_symbol", "元素符号");
        hashMap.put("element_mass", "质量数");
        searchList.add(hashMap);
        for (int i = 0; i < listDecays.size(); i++) {
            HashMap<String, String> hashMap1 = new HashMap<>();
            hashMap1.put("element_symbol", listDecays.elementAt(i).getParentName());
            hashMap1.put("element_mass", listDecays.elementAt(i).getParentMass());
            searchList.add(hashMap1);
        }
    }

    private class MyEditListener implements TextView.OnEditorActionListener {
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                if (identity == null) {
                    showText("请选择账号类型！");
                    return false;
                }else {
                    attemptLogin();
                    return true;
                }
            }
            return false;
        }
    }

    private class MyRadioGroup implements RadioGroup.OnCheckedChangeListener {
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            if (checkedId == R.id.user_bt) {
                identity = "user";
                MessageList.currentUser.isManager = false;
                editor.putBoolean("isManager", false);
            } else {
                identity = "manager";
                MessageList.currentUser.isManager = true;
                editor.putBoolean("isManager", true);
            }
        }
    }

    private class MyOnclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login :
                    if (identity == null) {
                        showText("请选择账号类型！");
                    }else {
                        attemptLogin();
                    }
                    break;
                case R.id.regist_bt :
                    Intent register = new Intent();
                    register.setClass(LoginActivity.this, Register.class);
                    startActivity(register);
                    break;
                case R.id.pass_recover_bt :
                    Intent recover = new Intent();
                    recover.setClass(LoginActivity.this, Recover.class);
                    startActivity(recover);
                    break;
            }
        }
    }

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

    //在登陆时收起软键盘
    private void hideKeyBoard() {
        InputMethodManager inputMethodManager
                = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    //根据密码长度判断密码是否合法
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    //文本提示框
    private void showText(String string) {
        Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        //requestCode为请求码，判断是谁请求的，本例中若为3则是Activity1中的请求，resultCode，结果的代码，例如结果为1则刷新Activity1，结果为2不刷新，
        //data为传递回来的数据
        super.onActivityResult(requestCode, resultCode, resultData);
        //do sth
    }

    //点击空白处收起软键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev, excludeView);
        return super.dispatchTouchEvent(ev);
    }

}


