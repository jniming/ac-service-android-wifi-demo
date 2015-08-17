package com.accloud.ac_service_android_wifi_demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.accloud.ac_service_android_wifi_demo.R;


/**
 * Created by Administrator on 2015/5/16.
 */
public class CusEditText extends LinearLayout {
    private TextView text;
    private EditText edit;
    private TextView line;
    private RelativeLayout hint;
    private TextView hint_text;

    public CusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_edit_text, this, true);
        text = (TextView) this.findViewById(R.id.view_edit_text);
        edit = (EditText) this.findViewById(R.id.view_edit);
        line = (TextView) this.findViewById(R.id.view_edit_line);
        hint = (RelativeLayout) this.findViewById(R.id.view_edit_hint);
        hint_text = (TextView) this.findViewById(R.id.view_edit_hint_text);
        edit.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    line.setBackgroundColor(getContext().getResources().getColor(R.color.theme));
                else
                    line.setBackgroundColor(getContext().getResources().getColor(R.color.login_line));
            }
        });
    }

    public void setText(String showText) {
        text.setText(showText);
    }

    public void setInputType(int inputType){
        edit.setInputType(inputType);
    }

    public String getText() {
        return edit.getText().toString();
    }

    public void showHint() {
        line.setBackgroundColor(getContext().getResources().getColor(R.color.login_hint));
        hint.setVisibility(View.VISIBLE);
    }

    public void hideHint() {
        line.setBackgroundColor(getContext().getResources().getColor(R.color.login_line));
        hint.setVisibility(View.INVISIBLE);
    }

    public void setHintText(String hintText) {
        hint_text.setText(hintText);
    }
}
