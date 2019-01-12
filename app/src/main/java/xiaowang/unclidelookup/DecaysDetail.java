package xiaowang.unclidelookup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import MyUtil.MessageList;
import MyUtil.MyClickSpan;
import MyUtil.MyListView;
import DataManage.Query;
import DataManage.SonNuclide;
import DataManage.StructDecay;

import static DataManage.QueryTypeByNuclide.NAME_PARENT;
import static xiaowang.unclidelookup.ThemeActivity.flag_toxic;

public class DecaysDetail extends AppCompatActivity {

    private Vector<StructDecay> resultDecays;
    private StructDecay queryResult;
    private Vector<SonNuclide> sonNuclides;
    private static Query query = new Query(MessageList.decaysList);

    private Drawable drawable_up, drawable_down;
    private Button aButton, bpButton, bnButton, rButton;
    private TextView child, life, toxic;
    private MyListView aListView, bpListView, bnListView, rListView;
    private boolean flag_a = true, flag_bp = true, flag_bn = true, flag_r = true;

    private List<Map<String,String>> aList = new ArrayList<>();
    private List<Map<String,String>> bnList = new ArrayList<>();
    private List<Map<String,String>> rList = new ArrayList<>();
    private List<Map<String,String>> bpList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decays_detail);

        init();
        try {
            lookUp();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(DecaysDetail.this, "非放射性核素！", Toast.LENGTH_SHORT).show();
            DecaysDetail.this.finish();
        }

    }

    private void init() {
        drawable_up = getResources().getDrawable(R.drawable.top);
        drawable_down = getResources().getDrawable(R.drawable.more);
        drawable_up.setBounds(0, 0,
                drawable_up.getMinimumWidth(), drawable_up.getMinimumHeight());
        drawable_down.setBounds(0, 0,
                drawable_down.getMinimumWidth(), drawable_down.getMinimumHeight());

        aButton = findViewById(R.id.a_bt);
        bpButton = findViewById(R.id.b_positive_bt);
        bnButton = findViewById(R.id.b_negative_bt);
        rButton = findViewById(R.id.r_bt);
        child = findViewById(R.id.child_text);
        life = findViewById(R.id.life_text);
        toxic = findViewById(R.id.toxic_text);
        aListView = findViewById(R.id.a_list);
        bpListView = findViewById(R.id.b_positive_list);
        bnListView = findViewById(R.id.b_negative_list);
        rListView = findViewById(R.id.r_list);
        aButton.setOnClickListener(new MyOnclickListener());
        bpButton.setOnClickListener(new MyOnclickListener());
        bnButton.setOnClickListener(new MyOnclickListener());
        rButton.setOnClickListener(new MyOnclickListener());

        if (!flag_toxic) {
            //very_toxic = getResources().getString(R.string.very_toxic_group).trim().split("\\、");
            //high_toxic = getResources().getString(R.string.high_toxic_group).trim().split("\\、");
            //middle_toxic = getResources().getString(R.string.middle_toxic_group).trim().split("\\、");
            //low_toxic = getResources().getString(R.string.low_toxic_group).trim().split("\\、");

            ThemeActivity.very_toxic = getResources().getString(R.string.very_toxic_group);
            ThemeActivity.high_toxic = getResources().getString(R.string.high_toxic_group);
            ThemeActivity.middle_toxic = getResources().getString(R.string.middle_toxic_group);
            ThemeActivity.low_toxic = getResources().getString(R.string.low_toxic_group);

            flag_toxic = true;
        }
    }

    private void lookUp() throws Exception{
        Intent intent = getIntent();
        String symbol = intent.getStringExtra("element_symbol");
        String mass = intent.getStringExtra("element_mass");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(symbol + "-" + mass + "查询");
        try {
            resultDecays = query.findByNuclide(NAME_PARENT, symbol);
        } catch (Exception e) {
            e.printStackTrace();
            DecaysDetail.this.finish();
        }

        for (int i = 0; i < resultDecays.size(); i++) {
            if (resultDecays.elementAt(i).getParentMass().equals(mass)) {
                queryResult = resultDecays.elementAt(i);
                sonNuclides = queryResult.getSoNuclides();
            }
        }

        //String ziheNameString = "";
        String ziheHalfString = "";
        for (int i = 0; i < sonNuclides.size(); i++) {
            if (i == 0) {
                setLink(child, sonNuclides.elementAt(i).getSonName(), sonNuclides.elementAt(i).getSonMass());
                //ziheNameString = ziheNameString  + sonNuclides.elementAt(i).getSonName();
                ziheHalfString = ziheHalfString  + sonNuclides.elementAt(i).getHalfLife();
            }
            if (i >= 1) {
                child.append("|");
                setLink(child, sonNuclides.elementAt(i).getSonName(), sonNuclides.elementAt(i).getSonMass());
                //ziheNameString = ziheNameString + "|" + sonNuclides.elementAt(i).getSonName();
                ziheHalfString = ziheHalfString + "|" + sonNuclides.elementAt(i).getHalfLife();
            }
        }
        //child.setText(ziheNameString);
        child.setMovementMethod(LinkMovementMethod.getInstance());
        life.setText(ziheHalfString);

        if (sonNuclides.size() != 0) {
            Map<String, String> title = new HashMap<String, String>();
            title.put("type", "衰变方式");
            title.put("energy", "能量（KeV）");
            title.put("intensity", "发射几率");
            aList.add(title);
            bnList.add(title);
            rList.add(title);
            bpList.add(title);
            for (int i = 0; i < sonNuclides.size(); i++) {
                Vector<String> alphaEnergy = sonNuclides.elementAt(i).getAlphaEnergyVector();
                Vector<String> alphaInsity = sonNuclides.elementAt(i).getAlphaIntensityVector();
                Vector<String> betaEnergy = sonNuclides.elementAt(i).getBetaEnergyVector();
                Vector<String> betaInsity = sonNuclides.elementAt(i).getBetaIntenistyVector();
                Vector<String> gammaEnergy = sonNuclides.elementAt(i).getGammaEnergyVector();
                Vector<String> gammaInsity = sonNuclides.elementAt(i).getGammaIntensityVector();
                Vector<String> postiveBetaEnergy = sonNuclides.elementAt(i).getPostiveBetaEnergyVector();
                Vector<String> postiveBetaInsity = sonNuclides.elementAt(i).getPostiveBetaIntensityVector();
                if (alphaInsity.size() != 0 ) {
                    for (int j = 0; j < alphaEnergy.size(); j++) {
                        Map<String, String> map = new HashMap<>();
                        map.put("type", sonNuclides.elementAt(i).getDecayTypeString());
                        map.put("energy", alphaEnergy.elementAt(j));
                        map.put("intensity", alphaInsity.elementAt(j));
                        Log.e("tiaoshitest", "alphaE: " + alphaEnergy.elementAt(j));
                        Log.e("tiaoshitest", "alphaI: " + alphaInsity.elementAt(j));
                        aList.add(map);
                    }
                }
                if (betaInsity.size() != 0) {
                    for (int j = 0; j < betaEnergy.size(); j++) {
                        Map<String, String> map = new HashMap<>();
                        map.put("type", sonNuclides.elementAt(i).getDecayTypeString());
                        map.put("energy", betaEnergy.elementAt(j));
                        map.put("intensity", betaInsity.elementAt(j));
                        Log.e("tiaoshitest", "betaE: " + betaEnergy.elementAt(j));
                        Log.e("tiaoshitest", "betaI: " + betaInsity.elementAt(j));
                        bnList.add(map);
                    }
                }
                if (gammaInsity.size() != 0) {
                    for (int j = 0; j < gammaEnergy.size(); j++) {
                        Map<String, String> map = new HashMap<>();
                        map.put("type", sonNuclides.elementAt(i).getDecayTypeString());
                        map.put("energy", gammaEnergy.elementAt(j));
                        map.put("intensity", gammaInsity.elementAt(j));
                        rList.add(map);
                    }
                }
                if (postiveBetaInsity.size() != 0) {
                    for (int j = 0; j < postiveBetaEnergy.size(); j++) {
                        Map<String, String> map = new HashMap<>();
                        map.put("type", sonNuclides.elementAt(i).getDecayTypeString());
                        map.put("energy", postiveBetaEnergy.elementAt(j));
                        map.put("intensity", postiveBetaInsity.elementAt(j));
                        bpList.add(map);
                    }
                }
            }
            SimpleAdapter alpha_Adapter = new SimpleAdapter(this, aList, R.layout.simple_item_radionuclide_detail,
                    new String[]{"type", "energy", "intensity"}, new int[]{R.id.lv_item_part3, R.id.lv_item_part1, R.id.lv_item_part2});
            aListView.setAdapter(alpha_Adapter);
            SimpleAdapter belta_Adapter = new SimpleAdapter(this, bnList, R.layout.simple_item_radionuclide_detail,
                    new String[]{"type", "energy", "intensity"}, new int[]{R.id.lv_item_part3, R.id.lv_item_part1, R.id.lv_item_part2});
            bnListView.setAdapter(belta_Adapter);
            SimpleAdapter gamma_Adapter = new SimpleAdapter(this, rList, R.layout.simple_item_radionuclide_detail,
                    new String[]{"type", "energy", "intensity"}, new int[]{R.id.lv_item_part3, R.id.lv_item_part1, R.id.lv_item_part2});
            rListView.setAdapter(gamma_Adapter);
            SimpleAdapter elec_Adapter = new SimpleAdapter(this, bpList, R.layout.simple_item_radionuclide_detail,
                    new String[]{"type", "energy", "intensity"}, new int[]{R.id.lv_item_part3, R.id.lv_item_part1, R.id.lv_item_part2});
            bpListView.setAdapter(elec_Adapter);
        }

        String msg = mass+symbol;
        //Toast.makeText(DecaysDetail.this, msg, Toast.LENGTH_SHORT).show();
        if (ThemeActivity.very_toxic.contains(msg)) {
            toxic.setText("极毒组");
        } else if(ThemeActivity.high_toxic.contains(msg)) {
            toxic.setText("高毒组");
        } else if(ThemeActivity.middle_toxic.contains(msg)) {
            toxic.setText("中毒组");
        } else if(ThemeActivity.low_toxic.contains(msg)) {
            toxic.setText("低毒组");
        } else {
            toxic.setText("信息缺失");
        }
    }

    private class MyOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.a_bt:
                    if (flag_a) {
                        flag_a = false;
                        aListView.setVisibility(View.VISIBLE);
                        aButton.setCompoundDrawables(null, null, drawable_up, null);//画在右边
                    }else {
                        flag_a = true;
                        aListView.setVisibility(View.GONE);
                        aButton.setCompoundDrawables(null, null, drawable_down, null);//画在右边
                    }
                    break;
                case R.id.b_positive_bt:
                    if (flag_bp) {
                        flag_bp = false;
                        bpListView.setVisibility(View.VISIBLE);
                        bpButton.setCompoundDrawables(null, null, drawable_up, null);//画在右边
                    }else {
                        flag_bp = true;
                        bpListView.setVisibility(View.GONE);
                        bpButton.setCompoundDrawables(null, null, drawable_down, null);//画在右边
                    }
                    break;
                case R.id.b_negative_bt:
                    if (flag_bn) {
                        flag_bn = false;
                        bnListView.setVisibility(View.VISIBLE);
                        bnButton.setCompoundDrawables(null, null, drawable_up, null);//画在右边
                    }else {
                        flag_bn = true;
                        bnListView.setVisibility(View.GONE);
                        bnButton.setCompoundDrawables(null, null, drawable_down, null);//画在右边
                    }
                    break;

                case R.id.r_bt:
                    if (flag_r) {
                        flag_r = false;
                        rListView.setVisibility(View.VISIBLE);
                        rButton.setCompoundDrawables(null, null, drawable_up, null);//画在右边
                    }else {
                        flag_r = true;
                        rListView.setVisibility(View.GONE);
                        rButton.setCompoundDrawables(null, null, drawable_down, null);//画在右边
                    }
                    break;
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


    private void setLink(TextView textView, final String sonName, final String sonMass) {
        SpannableString sonNameSpannable = new SpannableString(sonName);
        MyClickSpan sonNameSpan = new MyClickSpan() {
            @Override
            public void onClick(View widget) {
                try {
                    query.findByNuclide(NAME_PARENT, sonName);
                    Intent son = new Intent();
                    son.setClass(DecaysDetail.this, DecaysDetail.class);
                    son.putExtra("element_symbol", sonName);
                    son.putExtra("element_mass", sonMass);
                    startActivity(son);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DecaysDetail.this, "非放射性核素！", Toast.LENGTH_SHORT).show();
                }
            }
        };
        sonNameSpannable.setSpan(sonNameSpan, 0, sonName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.append(sonNameSpannable);
    }

}
