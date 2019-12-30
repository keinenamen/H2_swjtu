package com.swjtu.h2.process;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {
    public Context mContext;

    public static void ShowShorttoast(Context context,String text){
        Toast mToast = null;
        if (mToast == null) {
            mToast=Toast.makeText(context,null,Toast.LENGTH_SHORT);
            mToast.setText(text);
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(text);
        }
        mToast.show();
    }
    public static void ShowShorttoast(Context context,int textid){
        Toast mToast = null;
        String text = context.getResources().getString(textid);
        if (mToast == null) {
            mToast=Toast.makeText(context,null,Toast.LENGTH_SHORT);
            mToast.setText(text);
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(text);
        }
        mToast.show();
    }

    public static void ShowLongtoast(Context context,String text){
        Toast mToast = null;
        if (mToast == null) {
            mToast=Toast.makeText(context,null,Toast.LENGTH_LONG);
            mToast.setText(text);
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(text);
        }
        mToast.show();
    }

    public static void ShowLongtoast(Context context,int textid){
        Toast mToast = null;
        String text = context.getResources().getString(textid);
        if (mToast == null) {
            mToast=Toast.makeText(context,null,Toast.LENGTH_LONG);
            mToast.setText(text);
        } else {
            //如果当前Toast没有消失， 直接显示内容，不需要重新设置
            mToast.setText(text);
        }
        mToast.show();
    }

}
