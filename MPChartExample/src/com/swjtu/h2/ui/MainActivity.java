package com.swjtu.h2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.swjtu.h2.R;
import com.swjtu.h2.custom.GloableData;
import com.swjtu.h2.process.Tools;

import static com.swjtu.h2.process.ShowToast.ShowShorttoast;

public class MainActivity extends FragmentActivity {

	private View iv_final;
	private View iv_heartrate;
	private View iv_info;
	private View iv_navigation;
	private View iv_bloodpressure;
	private View iv_call;
	private long exittime;
	private Handler mHandler;
	private static final String TAG = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initView();
		setListener();
	}

	public void initView() {
		iv_final =  findViewById(R.id.iv_personal_order);
		iv_heartrate = findViewById(R.id.iv_personal_yuanbao);
		iv_info = findViewById(R.id.iv_personal_info);
		iv_navigation = findViewById(R.id.iv_personal_collect);
		iv_bloodpressure = findViewById(R.id.iv_personal_scan);
		iv_call = findViewById(R.id.iv_personal_call);
	}
	public void setListener() {
		iv_final.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(GloableData.mApp.getLogin()) {
					Intent i = new Intent(MainActivity.this, BluetoothActivity.class);
					startActivity(i);
				}else{
                    ShowShorttoast(getApplicationContext(),"您还未登录.");
					Intent i = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				}
				return;
			}
		});
		iv_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.CallPhone(MainActivity.this, "02866366110");
				return;
			}
		});
		iv_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, H2_locationActivity.class);
				startActivity(i);
				return;
			}
		});
		iv_navigation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				Uri uri = Uri.parse("http://www.baidu.com");
//				Intent intent = new Intent();
//				intent.setAction("android.intent.action.VIEW");
//				intent.setData(uri);
//				startActivity(intent);
				Intent i = new Intent(MainActivity.this, KnowledgeActivity.class);
				startActivity(i);
				return;
			}
		});
		iv_bloodpressure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(GloableData.mApp.getLogin()) {
					Intent i = new Intent(MainActivity.this, BloodPressureLine.class);
					startActivity(i);
				}else{
                    ShowShorttoast(getApplicationContext(),"您还未登录.");
					Intent i = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				}
				return;
			}
		});
		iv_heartrate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(GloableData.mApp.getLogin()) {
					Intent i = new Intent(MainActivity.this, HeartRateLine.class);
					startActivity(i);
				}else{
                    ShowShorttoast(getApplicationContext(),"您还未登录.");
				    Intent i = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(i);
					finish();
				}
				return;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			initKeyDown();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**双击退出实现*/
	public void initKeyDown() {
		long l = System.currentTimeMillis();
		if(l-exittime < 2200) {
			GloableData.mApp.setLogin(false);
			finish();
		} else {
			exittime = l;
			ShowShorttoast(getApplicationContext(),"再次点击返回桌面");
		}
	}
	public void showToastS(String context) {
		Toast toa = Toast.makeText(getApplicationContext(), context, Toast.LENGTH_SHORT);
		toa.setGravity(Gravity.CENTER, 0, 0);
		toa.show();
	}
}
