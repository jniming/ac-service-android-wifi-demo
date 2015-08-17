package com.accloud.ac_service_android_wifi_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.ac_service_android_wifi_demo.view.BaseTitleLayout;
import com.accloud.ac_service_android_wifi_demo.view.DrawerMenu;


/**
 * Created by Xuri on 2015/4/3.
 */
public abstract class BaseActivity extends Activity {

    private BaseTitleLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public DrawerMenu initSlideMenu() {
        return new DrawerMenu(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    public void showToast(String toast) {
        Pop.popToast(this, toast);
    }

    public int getColor(int id) {
        return getResources().getColor(id);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @Override
    public void setContentView(int layoutResID) {
        if (layout == null) {
            layout = new BaseTitleLayout(this, layoutResID);
        }
        super.setContentView(layout);
        this.setClickListener(new View[]{layout.leftll, layout.rightBtn, layout.right2Btn});
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title))
            layout.title.setText(title);
    }

    /**
     * @param leftBtnDraw
     * @param rightBtnDraw
     */
    public void setNavBtn(int leftBtnDraw, int rightBtnDraw) {
        this.setNavBtn(leftBtnDraw, rightBtnDraw, 0);
    }

    public void setNavBtn(int leftBtnDraw, int rightBtnDraw, int right2BtnDraw) {
        if (layout != null) {
            setSingleNavBtn(layout.leftBtn, leftBtnDraw);
            setSingleNavBtn(layout.rightBtn, rightBtnDraw);
            setSingleNavBtn(layout.right2Btn, right2BtnDraw);
        }
    }

    public void setNavBtn(int leftBtnDraw, String leftVal, int rightBtnDraw, String rightVal) {
        if (layout != null) {
            setSingleNavBtn(layout.leftBtn, leftBtnDraw, leftVal);
            setSingleNavBtn(layout.rightBtn, rightBtnDraw, rightVal);
        }
    }

    /**
     * @param btn
     * @param res
     */
    private void setSingleNavBtn(TextView btn, int res) {
        if (res > 0) {
            btn.setBackgroundResource(res);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    private void setSingleNavBtn(TextView btn, int res, String value) {
        if (res > 0) {
            btn.setBackgroundResource(res);
        } else if (!TextUtils.isEmpty(value)) {
            btn.setText(value);
        } else {
            btn.setVisibility(View.GONE);
        }
    }

    /**
     * @param views
     */
    private void setClickListener(View[] views) {
        for (View v : views) {
            v.setOnClickListener(listener);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.equals(layout.leftll)) {
                HandleTitleBarEvent(TitleBar.LEFT, view);
            } else if (view.equals(layout.rightBtn)) {
                HandleTitleBarEvent(TitleBar.RIGHT, view);
            } else if (view.equals(layout.right2Btn)) {
                HandleTitleBarEvent(TitleBar.RIGHT2, view);
            } else if (view.equals(layout.title)) {
                HandleTitleBarEvent(TitleBar.TITLE, view);
            }
        }
    };

    /**
     * @param component
     * @param v
     */
    protected abstract void HandleTitleBarEvent(TitleBar component, View v);

    public enum TitleBar {
        LEFT,
        RIGHT,
        RIGHT2,
        TITLE
    }
}
