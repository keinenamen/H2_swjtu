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
import com.swjtu.h2.process.Tools;

import java.io.File;
import java.util.ArrayList;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class H2_locationActivity extends FragmentActivity implements OnClickListener,AdapterView.OnItemClickListener {
    private static final String TAG = "H2_locationActivity";

    private ListView lv_h2machine;
    private ImageView imageView;
    private TextView tv_return;
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
        setContentView(R.layout.h2_location);
        ((TextView)findViewById(R.id.tv_head_title)).setText("氢机位置");
        lv_h2machine = (ListView) findViewById(R.id.lv_h2machine);
        Tools.showProgDlg(H2_locationActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    values =
                            DBUtils.getUserInfoByName(1);
                    Message msg = new Message();
                    if (values == null) {
                        msg.what = 0;
                        Log.d("TAG", " 数据不存在！");
                        return;
                    } else {
                        msg.what = 1;
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
            TextView name = (TextView) view.findViewById(R.id.tv_machine_name);
            TextView adress = (TextView) view.findViewById(R.id.tv_machine_address);
            TextView state = (TextView) view.findViewById(R.id.tv_machine_state);

            name.setText(machine_h2.getName());
            adress.setText(machine_h2.getAdr());
            state.setText(machine_h2.getSta());

            return view;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Machine_h2 item = values.get(position);
        setUpGaodeAppByMine(item.getAdr(),item);
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
        MachineAdapter adapter = new MachineAdapter(this,R.layout.machinelocation,values);
        lv_h2machine.setAdapter(adapter);
        Tools.dissProgDlg();
        lv_h2machine.setOnItemClickListener(H2_locationActivity.this);
    }

    /**
     * 我的位置BY高德
     */
    void setUpGaodeAppByMine(String mstring,Machine_h2 item){
        try {
            if(isInstallByread("com.baidu.BaiduMap")){
                Log.d("MapTest", "be installed target applica");
                Intent intent = Intent.getIntent("intent://map/direction?origin=我的位置&destination="+mstring+"&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
                startActivity(intent);
            }else if(isInstallByread("com.autonavi.minimap")){
                Intent intent = new Intent("android.intent.action.VIEW",
                        android.net.Uri.parse("androidamap://route?sourceApplication=softname&sname=我的位置"+"&dname="+mstring+ "&dev=0&m=0&t=1"));
                startActivity(intent);
            }else{
                //内部集成高德地图
                Intent i = new Intent(H2_locationActivity.this, com.amap.navi.demo.activity.IndexActivity.class);
                i.putExtra("key",item.getAdr());
                startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }
}
