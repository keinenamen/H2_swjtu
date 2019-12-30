package com.swjtu.h2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.swjtu.h2.R;
import com.swjtu.h2.custom.GloableData;
import com.swjtu.h2.process.DBUtils;
import com.swjtu.h2.process.Tools;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class PwdForgetActivity extends FragmentActivity {

    private EditText et_tel;
    private EditText et_psd;
    private EditText et_code;
    private Button btn_code;
    public EventHandler eh; //事件接收器
    private TimeCount mTimeCount;//计时器

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String str = "更新密码失败，请稍候再试！";
            if(message.what == 1) str = "更新成功";
            if(message.what == 2) str = "验证码错误，请稍候再试！";
            Tools.dissProgDlg();
            ShowShorttoast(getApplicationContext(),str);
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.registeractivity);
        initView();
        init();
        setListener();
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_head_title)).setText("修改密码");
        String username = GloableData.mApp.getUuid();
        et_tel = (EditText) findViewById(R.id.et_registeractivity_tel);
        et_tel.setText(username);
        et_psd= (EditText) findViewById(R.id.et_registeractivity_psd);
        et_code= (EditText) findViewById(R.id.et_registeractivity_code);
        btn_code = (Button) findViewById(R.id.btn_registeractivity_code);
        ((TextView)findViewById(R.id.btn_registeractivity_sure)).setText("更  新");
        ((TextView)findViewById(R.id.forget_pwd)).setText("更新密码");
        mTimeCount = new TimeCount(60000, 1000);
    }

    /**
     * 初始化事件接收器
     */
    private void init(){
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码成功
                        final String useName = et_tel.getText().toString().trim();
                        final String psd = et_psd.getText().toString().trim();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
                        try {
                            boolean mp =
                                    DBUtils.updateUserByName(useName,psd);
                            Message msg = new Message();
                            if (mp == false){
                                msg.what = 0;
                                Log.d("TAG", " 密码更新失败，请稍候再试！");
                            }
                            else{
                                Tools.showProgDlg(PwdForgetActivity.this);
                                GloableData.mApp.setUuid(useName);
                                msg.what = 1;
                                Log.d("TAG", " 密码更新成功");
                                Intent regIntent = new Intent(PwdForgetActivity.this,
                                        LoginActivity.class);
                                startActivity(regIntent);
                            }
                            handler.sendMessage(msg);
                        }catch (Exception e){
                            e.printStackTrace();
                            //ShowShorttoast(getApplicationContext(), "登录不成功.");
                            Log.d("TAG", " 密码更新失败");
                        }
//                            }
//                        }).start();
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){ //获取验证码成功

                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){ //返回支持发送验证码的国家列表

                    }
                }else{
                    ((Throwable)data).printStackTrace();
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    private void setListener() {
        findViewById(R.id.iv_head_left).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                finish();
            }
        });
        findViewById(R.id.tv_head_return).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//
                finish();
            }
        });
        findViewById(R.id.btn_registeractivity_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//
                final String useName = et_tel.getText().toString().trim();
                final String psd = et_psd.getText().toString().trim();

                boolean isR = Tools.isMobileNO(useName);
                if (!isR) {
                    ShowShorttoast(getApplicationContext(),"请输入正确的手机号码。");
                    return;
                }
                if (null == psd || "".equals(psd) || psd.length() > 12 || psd.length() < 6) {
                    ShowShorttoast(getApplicationContext(),"请输入正确的密码格式。");
                    return;
                }
                final String code = et_code.getText().toString().trim();
                if (null == psd || "".equals(code)) {
                    ShowShorttoast(getApplicationContext(),"请输入验证码。");
                    return;
                }else {
                    SMSSDK.submitVerificationCode("+86",useName,code);//提交验证
                    return;
                }
            }
        });

        btn_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String useName = et_tel.getText().toString().trim();
                boolean isR = Tools.isMobileNO(useName);
                if(!isR) {
                    ShowShorttoast(getApplicationContext(),"请输入正确的手机号码。");
                    return;

                }else {
                    SMSSDK.getVerificationCode("+86",useName);//获取验证码
                    mTimeCount.start();
                    return;
                }
            }
        });
    }

    public void showToastS(String context) {
        Toast toa = Toast.makeText(getApplicationContext(), context, Toast.LENGTH_SHORT);
        toa.setGravity(Gravity.CENTER, 0, 0);
        toa.show();
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            btn_code.setClickable(false);
            btn_code.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            btn_code.setClickable(true);
            btn_code.setText("获取验证码");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}
