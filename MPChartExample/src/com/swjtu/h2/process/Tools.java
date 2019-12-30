package com.swjtu.h2.process;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.swjtu.h2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    private static Dialog Radiodialog;

    /**
     * 显示加载效果
     */
    static public void showProgDlg(Context ctx) {
        // ProgressDialog
        if(null!=Radiodialog)
            return;
        Radiodialog = new Dialog(ctx, R.style.LodingDialog);
        Radiodialog.setContentView(R.layout.dialog_process);
        Radiodialog.setCancelable(false);
        Radiodialog.show();
    }

    static public void dissProgDlg() {
        if(null != Radiodialog) {
            Radiodialog.dismiss();
            Radiodialog = null;
        }
    }
    /**
     * 判断是否是手机号
     * @param mobiles
     * @return
     */
    static public boolean isMobileNO(String mobiles) {
        String REG_MOBILE = "^1[3|4|5|7|8][0-9]{9}$";
//		Pattern p = Pattern
//				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern
                .compile(REG_MOBILE);
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    /**打电话  只进入拨号界面*/
    static public void CallPhone(Context ctx, String phonenum) {
//        if(null==phonenum || "".equals(phonenum)) {
//            showToastS("电话错误");
//            return;
//        }
        Uri uri = Uri.parse("tel:" + phonenum);
//		Intent intent = new Intent(Intent.ACTION_CALL, uri);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        ctx.startActivity(intent);
    }

//    public static void showToastS(String context) {
//        Toast toa = Toast.makeText(getApplicationContext(), context, Toast.LENGTH_SHORT);
//        toa.setGravity(Gravity.CENTER, 0, 0);
//        toa.show();
//    }

}
