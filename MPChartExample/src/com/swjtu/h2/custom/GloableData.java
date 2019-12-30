package com.swjtu.h2.custom;

import android.app.Application;
import android.content.Context;

import cn.smssdk.SMSSDK;


public class GloableData extends Application {

	private final static String TAG = "GloableData";
	public static GloableData mApp;
	public static Context sMainContext;
	private String uuid = new String();
	private String upwd = new String();

	public boolean isLogin = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
		sMainContext = getApplicationContext();
		SMSSDK.initSDK(this, "27557a137bfdc", "2fdf8b2bcba681193072d20e1ca32d94");
	}

	public void setLogin(boolean ulog) {
		this.isLogin = ulog;
       // Log.d("TAG", uuid);
	}
	public boolean getLogin() {
        //Log.d("TAG", this.uuid);
        return this.isLogin;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
		// Log.d("TAG", uuid);
	}
	public String getUuid() {
		//Log.d("TAG", this.uuid);
		return this.uuid;
	}

	public void setUpwd(String upwd) {
		this.upwd = upwd;
		//Log.d("TAG", upwd);
	}
	public String getUpwd() {
		return this.upwd;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
