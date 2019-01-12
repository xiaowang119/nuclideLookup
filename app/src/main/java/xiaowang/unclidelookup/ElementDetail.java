package xiaowang.unclidelookup;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by admin on 2017/6/28.
 */

public class ElementDetail extends AppCompatActivity {

    private String[] state_info = new String[]{"元素状态","来源","密度(g/m^3)","电离能(eV)"};
    private List<Map<String,Object>> stateList =new ArrayList<Map<String, Object>>();
    private List<Map<String,Object>> abundanceList =new ArrayList<Map<String, Object>>();
    private String DATA_PATH = "/data/data/xiaowang.unclidelookup/databases/element.db";

    private ListView lv_state;
    private ListView lv_abundance;
    private ActionBar actionBar;
    private int sequence;
    private String ele_symbol;

    private  Button name, sequence_bt, know_more;
    private LinearLayout rich;
    private ImageView electric_view;
    private TextView symbol, englishName, relativeQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.element_detail);

        init();
        load_element();
        setMessage();
    }

    private void init() {
        Bundle bundle = this.getIntent().getExtras();
        sequence = bundle.getInt("sequence");
        Log.e("test","当前页面显示第"+sequence+"号元素信息");
        lv_abundance = (ListView)findViewById(R.id.element_abundance);
        lv_state = (ListView)findViewById(R.id.state_info);
        name = (Button)findViewById(R.id.element_name);
        rich = (LinearLayout)findViewById(R.id.rich);
        sequence_bt = (Button)findViewById(R.id.element_sequence);
        symbol = (TextView)findViewById(R.id.element_symbol);
        englishName = (TextView)findViewById(R.id.english_name);
        relativeQuality = (TextView)findViewById(R.id.relative_quality);
        know_more = (Button)findViewById(R.id.more);
        know_more.setOnClickListener(new MyWebListener());
        electric_view = (ImageView)findViewById(R.id.electric_view);

        actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void load_element(){
        try{
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(DATA_PATH,null);
            String[] state_data = new String[4];

            Cursor cursor = database.rawQuery("select * from element where elem_z like ?",new String[]{String.valueOf(sequence)});
            int count = cursor.getCount();
            Log.e("test","周期表查询记录数量"+count);
            cursor.moveToFirst();
            do{
                ele_symbol = cursor.getString(0);
                symbol.setText(ele_symbol);
                actionBar.setTitle(ele_symbol+"—详细信息");
                sequence_bt.setText(cursor.getString(1));
                relativeQuality.setText(cursor.getString(2));
                name.setText(cursor.getString(3));
                englishName.setText(cursor.getString(4));

                state_data[0] = cursor.getString(5);
                state_data[1] = cursor.getString(6);
                state_data[2] = cursor.getString(7);
                state_data[3] = cursor.getString(8);
                for(int i=0;i<4;i++)
                {
                    Map<String,Object> listItem=new HashMap<String,Object>();
                    listItem.put("desc",state_info[i]);
                    listItem.put("data",state_data[i]);
                    stateList.add(listItem);
                }
            }while (cursor.moveToNext());

            cursor = database.rawQuery("select * from abundance where elem_z like ?",new String[]{(String) symbol.getText()});
            count = cursor.getCount();
            if (count > 4) {
                rich.setMinimumHeight(40*count);
            }
            Log.e("test","丰度表查询记录数量"+count);
            if(count!=0)
            {
                cursor.moveToFirst();
                for(int i=0;i<count;i++)
                {
                    Map<String,Object> listItem=new HashMap<String,Object>();
                    listItem.put("desc",cursor.getString(1));
                    listItem.put("data",cursor.getString(2));
                    abundanceList.add(listItem);
                    Log.e("test","插入日志"+i);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            database.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void setMessage() {
        SimpleAdapter simpleAdapter2 = new SimpleAdapter(this, stateList,R.layout.simple_item_element_detail,new String[]{"desc","data"},new int[]{R.id.lv_part1,R.id.lv_part2});
        SimpleAdapter simpleAdapter3 = new SimpleAdapter(this, abundanceList,R.layout.simple_item_element_detail,new String[]{"desc","data"},new int[]{R.id.lv_part1,R.id.lv_part2});
        lv_state.setAdapter(simpleAdapter2);
        lv_abundance.setAdapter(simpleAdapter3);

        int id = getResId(ele_symbol.toLowerCase(), R.drawable.class);
        if (id > -1) {
            electric_view.setImageResource(id);
        }else {
            electric_view.setImageResource(R.drawable.miss);
        }
    }

    @Override
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

    private class MyWebListener implements View.OnClickListener {
        public void onClick(View view) {
            Intent more = new Intent();
            more.setClass(ElementDetail.this, More.class);
            more.putExtra("name", ele_symbol);
            startActivity(more);
        }
    }

    //通过反射的方式，实现靠字符串获取资源的功能
    public int getResId(String name, Class c) {
        try {
            Field idField = c.getDeclaredField(name);
            return idField.getInt(idField);
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
