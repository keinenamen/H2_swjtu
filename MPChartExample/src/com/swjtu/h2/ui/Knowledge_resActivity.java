package com.swjtu.h2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.swjtu.h2.R;
import com.swjtu.h2.custom.Machine_h2;
import com.swjtu.h2.process.DBUtils;
import com.swjtu.h2.process.Tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class Knowledge_resActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = "Knowledge_resActivity";

    private TextView tv_return;
    private ImageView imageView;
    private ArrayList<Machine_h2> values = new ArrayList<Machine_h2>();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String[] strs = ((String)message.obj).split("[;]");
            String[] tstr = new String[5];
            for(int i=0;i<strs.length;i++){
                tstr[i]=strs[i].replace("\\t", "\t")
                               .replace("\\n", "\n");
            }
            Arrays.sort(tstr);
            if(message.what==0){
                Log.d("TAG", " 数据不存在！");
            }
            else{
                Log.d("TAG", " 数据库错误！");
            }
            TextView name = (TextView) findViewById(R.id.tv_knowres);
            TextView adress = (TextView) findViewById(R.id.tv_knowcontent);
            TextView from = (TextView) findViewById(R.id.tv_from);
            TextView date = (TextView)findViewById(R.id.tv_date);

            name.setText(" "+tstr[0].split("[:]")[1]+"."+tstr[1].split("[:]")[1]);
            adress.setText(tstr[2].split("[:]")[1]);
            from.setText("来源："+tstr[3].split("[:]")[1]);
            date.setText("更新："+tstr[4].split("[:]")[1]);
            Tools.dissProgDlg();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.knowledge_res);
        ((TextView)findViewById(R.id.tv_head_title)).setText("高血压知识");
        Intent intent = getIntent();
        final String pos = intent.getStringExtra("key");
        Tools.showProgDlg(Knowledge_resActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> mp =
                            DBUtils.getInfoByName_res(Integer.parseInt(pos));
                    Message msg = new Message();
                    if (values == null) {
                        msg.what = 0;
                        Log.d("TAG", " 数据不存在！");
                        return;
                    } else {
                        String ss = new String();
                        for (String key : mp.keySet()) {
                            ss = ss +key +":" + mp.get(key) + ";";
                        }
                        msg.what = 1;
                        msg.obj = ss;
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_head_left) {
            finish();
        }else if(v.getId() == R.id.tv_head_return) {
            finish();
        }
    }

}
