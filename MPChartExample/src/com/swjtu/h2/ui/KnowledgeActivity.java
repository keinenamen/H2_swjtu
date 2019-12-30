package com.swjtu.h2.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.swjtu.h2.R;
import com.swjtu.h2.custom.Machine_h2;
import com.swjtu.h2.process.DBUtils;

import java.util.ArrayList;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class KnowledgeActivity extends FragmentActivity implements OnClickListener,AdapterView.OnItemClickListener {
    private static final String TAG = "KnowledgeActivity";

    private ListView lv_h2machine;
    private TextView tv_return;
    private ImageView imageView;
    private ArrayList<Machine_h2> values = new ArrayList<Machine_h2>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
        if(message.what==0){
            Log.d("TAG", " 数据不存在！");
        }
        else{
            Log.d("TAG", " 数据库错误！");
        }
        data_chart();
        return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.knowledge);
        ((TextView)findViewById(R.id.tv_head_title)).setText("血压健康");
        lv_h2machine = (ListView) findViewById(R.id.lv_knowledge);
        //Tools.showProgDlg(KnowledgeActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    values =
                            DBUtils.getInfoByName();
                    Message msg = new Message();
                    if (values == null) {
                        msg.what = 0;
                        Log.d("TAG", " 数据不存在！");
                        //ShowShorttoast(getApplicationContext(),"NO");
                        return;
                    } else {
                        msg.what = 1;
                        //ShowShorttoast(getApplicationContext(),values.toString());
                    }
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("TAG", "查询失败！");
                }
            }
        }).start();

        imageView = (ImageView) findViewById(R.id.iv_head_left);
        imageView.setOnClickListener(this);
        tv_return = (TextView)findViewById(R.id.tv_head_return);
        tv_return.setOnClickListener(this);
    }

    class MachineAdapter extends ArrayAdapter<Machine_h2> {
        private int mResourceId;

        public MachineAdapter(Context context, int textViewResourceId,
                              ArrayList<Machine_h2> values) {
            super(context,textViewResourceId, values);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Machine_h2 machine_h2 = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(mResourceId, null);
            TextView name = (TextView) view.findViewById(R.id.tv_knowledge_name);

            name.setText(machine_h2.getName()+"."+machine_h2.getAdr());

            return view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Machine_h2 item = values.get(position);
        Intent i = new Intent(KnowledgeActivity.this, Knowledge_resActivity.class);
        i.putExtra("key",item.getName());
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_head_left) {
            finish();
        }else if(v.getId() == R.id.tv_head_return) {
            finish();
        }
    }

    public void data_chart(){
        //ShowShorttoast(getApplicationContext(),values.toString());
        MachineAdapter adapter = new MachineAdapter(this,R.layout.knowledge_location,values);
        lv_h2machine.setAdapter(adapter);
        //Tools.dissProgDlg();
        lv_h2machine.setOnItemClickListener(KnowledgeActivity.this);
    }
}
