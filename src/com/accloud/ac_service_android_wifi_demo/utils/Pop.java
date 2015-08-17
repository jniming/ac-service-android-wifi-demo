package com.accloud.ac_service_android_wifi_demo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.add.DeviceReadyActivity;
import zxing.CaptureActivity;

/**
 * Created by Administrator on 2015/1/15 0015.
 */
public class Pop {
    static Toast toast = null;

    public static void popToast(Context context, String title) {
        if (toast == null)
            toast = Toast.makeText(context, title, Toast.LENGTH_LONG);
        else
            toast.setText(title);
        toast.show();
    }

    public static CustomDialog popDialog(Context context, int layout) {
        return new CustomDialog(context, layout).show();
    }

    public static CustomDialog popDialog(Context context, int layout, int style) {
        return new CustomDialog(context, layout, style).show();
    }

    public static PopupWindow popWindow(final Activity activity, View parent) {
        backgroundAlpha(activity, 0.5f);
        LayoutInflater mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //弹出窗口包含的视图
        View menuView = mLayoutInflater.inflate(R.layout.popup_more, null, true);
        //创建弹出窗口，指定大小
        PopupWindow popupWindow = new PopupWindow(menuView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置窗口显示的动画效果
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        //设置弹出窗口的背景
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //设置是否允许在外点击使其消失
        popupWindow.setOutsideTouchable(true);
        //设置窗口显示的位置
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        //更新弹出窗口的状态
        popupWindow.update();
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(activity, 1f);
            }
        });
        return popupWindow;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public static void backgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    public static void popBindDialog(final Activity activity, View parent) {
        final PopupWindow popupWindow = popWindow(activity, parent);
        View view = popupWindow.getContentView();
        view.findViewById(R.id.popup_more).setVisibility(View.GONE);
        TextView add_new_device = (TextView) view.findViewById(R.id.add_new_device);
        TextView add_share_device = (TextView) view.findViewById(R.id.add_share_device);
        add_new_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DeviceReadyActivity.class);
                activity.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        add_share_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CaptureActivity.class);
                activity.startActivity(intent);
                popupWindow.dismiss();
            }
        });
    }
}


