package com.swjtu.h2.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.swjtu.h2.R;
import com.swjtu.h2.custom.GloableData;
import com.swjtu.h2.process.DBUtils;
import com.swjtu.h2.process.Tools;
import java.util.HashMap;
import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class LoginActivity extends FragmentActivity implements View.OnClickListener{
    private Button mConfirmBtn;
    private EditText mAccountEdtxt;
    private EditText mPwdEdtxt;
    private Button mPwdForget;
    private Button btnRegister;
    private ImageButton ib_del;
    private CheckBox remeber_me;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String str = "登录失败，请稍候再试！";
            if(message.what == 1) str = "登录成功";
            Tools.dissProgDlg();
            ShowShorttoast(getApplicationContext(),str);
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //切入后台返回，程序还在当前界面
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginactivity);
//        Display mDisplay = getWindowManager().getDefaultDisplay();
//        GloableData.mApp.mWidth = mDisplay.getWidth();
//        GloableData.mApp.mHeight = mDisplay.getHeight();
        initLogin();
    }

    public void initLogin() {
        SharedPreferences sharedPre=getSharedPreferences("config", MODE_PRIVATE);
        String username=sharedPre.getString("username", "");
        String password=sharedPre.getString("password", "");
        mConfirmBtn=(Button)findViewById(R.id.btn_loginactivity_login);
        mConfirmBtn.setOnClickListener(this);
        mAccountEdtxt=(EditText)findViewById(R.id.et_loginactivity_use);
        mAccountEdtxt.setText(username);
        mPwdEdtxt=(EditText)findViewById(R.id.et_loginactivity_psd);
        mPwdEdtxt.setText(password);
        mPwdForget=(Button)findViewById(R.id.tv_loginactivity_forget);
        mPwdForget.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btn_loginactivity_reg);
        btnRegister.setOnClickListener(this);
        ib_del = (ImageButton) findViewById(R.id.ib_loginactivity_delete);
        ib_del.setOnClickListener(this);
        remeber_me = (CheckBox)findViewById(R.id.remember_me);
    }

    @Override
    public void onClick(View view) {
        final String sname = mAccountEdtxt.getText().toString().trim();
        final String spwd = mPwdEdtxt.getText().toString().trim();
        GloableData.mApp.setUuid(sname);
        GloableData.mApp.setUpwd(spwd);
        switch(view.getId()){
            case R.id.tv_loginactivity_forget:
                Intent newIntent = new Intent(LoginActivity.this,
                        PwdForgetActivity.class);
                startActivity(newIntent);
                break;
            case R.id.btn_loginactivity_login:
                if (Tools.isMobileNO(sname)) {
                    Log.d("TAG", " 启动！");
                    loginDel(sname,spwd);
                }else{
                    ShowShorttoast(getApplicationContext(),"请填写正确的手机号码.");
                }
                break;
            case R.id.btn_loginactivity_reg:
                Intent regIntent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(regIntent);
                break;
            case R.id.ib_loginactivity_delete:
                mAccountEdtxt.setText("");
                mPwdEdtxt.setText("");
            default:
                break;
        }
    }
    private void loginDel(final String sname, final String spwd){
        if(null==spwd||spwd.equals(""))
        {
            ShowShorttoast(getApplicationContext(),"密码不能为空");
        }
        else {
            Tools.showProgDlg(LoginActivity.this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("TAG", " 开始连接！");
                        HashMap<String, String> mp =
                                DBUtils.getUserInfoByName(sname,spwd);
                        Message msg = new Message();
                        if (mp == null){
                            msg.what=0;
                            Log.d("TAG", " 用户不存在！");
                        }
                        else{
                            msg.what=1;
                            Log.d("TAG", " 登录成功");
                            GloableData.mApp.setLogin(true);
                            if(remeber_me.isChecked()){
                                saveLoginInfo(getApplicationContext(),sname,spwd);
                            }else{
                                saveLoginInfo(getApplicationContext(),"","");
                            }
                            Intent regIntent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(regIntent);
                            finish();
                        }
                        handler.sendMessage(msg);
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d("TAG", " 登录不成功");
                    }
                }
            }).start();
        }
    }

    public void showToastS(String context) {
        Toast toa = Toast.makeText(getApplicationContext(), context, Toast.LENGTH_SHORT);
        toa.setGravity(Gravity.CENTER, 0, 0);

        toa.show();
    }

    /**
     * 使用SharedPreferences保存用户登录信息
     * @param context
     * @param username
     * @param password
     */
    public static void saveLoginInfo(Context context, String username, String password){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("config", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("username", username);
        editor.putString("password", password);
        //提交
        editor.commit();
    }
}
