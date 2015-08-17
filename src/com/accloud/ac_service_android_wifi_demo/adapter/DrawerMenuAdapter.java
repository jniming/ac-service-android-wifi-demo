package com.accloud.ac_service_android_wifi_demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.config.ConstantCache;


/**
 * Created by Xuri on 2014/11/7.
 */
public class DrawerMenuAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private int[] menuItems = {R.string.menu_device_manager, R.string.gujian_update, R.string.other_acount, R.string.menu_update, R.string.menu_about_us};
    private int[] menuItemIds = {R.drawable.personal_device, R.drawable.personal_system, R.drawable.other_account, R.drawable.wangguan_update, R.drawable.personal_about};

    public DrawerMenuAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int i) {
        return menuItems[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.adapter_slide_menu, null);
        TextView text = (TextView) view.findViewById(R.id.text_slide_menu);
        ImageView icon = (ImageView) view.findViewById(R.id.icon_slide_menu);
        ImageView update = (ImageView) view.findViewById(R.id.icon_update);
        icon.setImageResource(menuItemIds[i]);
        text.setText(context.getString(menuItems[i]));
        if (ConstantCache.hasUpdate && i == 1 && ConstantCache.deviceUpdateInfo.size() > 0)
            update.setVisibility(View.VISIBLE);
        return view;
    }
}
