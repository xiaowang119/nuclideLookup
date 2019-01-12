package MyUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import xiaowang.unclidelookup.Manage;
import xiaowang.unclidelookup.R;
import java.util.List;

/**
 * 为用户列表特别制定的Adapter
 */
public class MyAdapter extends BaseAdapter {
    private List<Model> data;
    private Context context;
    private Manage.AllCheckListener allCheckListener;

    public MyAdapter(List<Model> data, Context context, Manage.AllCheckListener allCheckListener) {
        this.data = data;
        this.context = context;
        this.allCheckListener = allCheckListener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.user_item, null);
            hd.nameView = (TextView) view.findViewById(R.id.name_item);
            hd.accountView = (TextView) view.findViewById(R.id.account_item);
            hd.checkBox = (CheckBox) view.findViewById(R.id.single_check);
            view.setTag(hd);
        }
        Model mModel = data.get(i);
        hd = (ViewHoder) view.getTag();
        hd.nameView.setText(mModel.getName());
        hd.accountView.setText(mModel.getAccount());

        Log.e("myadapter", mModel.getName() + "------" + mModel.ischeck());
        final ViewHoder hdFinal = hd;
        hd.checkBox.setChecked(mModel.ischeck());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                } else {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                    allCheckListener.setButton(true);
                }
                //监听每个item，若所有checkbox都为选中状态则更改main的全选checkbox状态
                Boolean flag_tmp = true;
                for (Model model : data) {
                    if (!model.ischeck()) {
                        allCheckListener.onCheckedChanged(false);
                        flag_tmp = false;
                    }
                }
                allCheckListener.onCheckedChanged(flag_tmp);

                for (Model model : data) {
                    if (model.ischeck()) {
                        return;
                    }
                }
                allCheckListener.setButton(false);
            }
        });
        return view;
    }

    class ViewHoder {
        TextView nameView;
        TextView accountView;
        CheckBox checkBox;
    }
}