package com.accloud.ac_service_android_wifi_demo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.accloud.ac_service_android_wifi_demo.R;


public class LoadingView {
	
	 private Context mContext;
	 private ViewGroup mParent;
	 private View mContainerLayout;
     public LoadingView(Context context, ViewGroup parent) {
    	 mContext = context;
    	 mParent = parent;
    	 initView();
     }
     
    
     private void initView() {
    	 LayoutInflater inflater = LayoutInflater.from(mContext);
    	 mContainerLayout = inflater.inflate(R.layout.view_loading_layout, null, true);
     }

     public void removeView() {
    	 if (mParent != null && mContainerLayout != null) {
    	    mParent.removeView(mContainerLayout);
    	    mContainerLayout = null;
    	 }
     }
     public void showView() {
    	 if (mParent != null && mContainerLayout != null) {
    		RelativeLayout.LayoutParams  layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    	    mParent.addView(mContainerLayout,layoutParams);
    	 }
     }
}
