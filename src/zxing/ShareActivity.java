package zxing;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.accloud.ac_service_android_wifi_demo.R;
import com.accloud.ac_service_android_wifi_demo.activity.BaseActivity;
import com.accloud.ac_service_android_wifi_demo.config.Config;
import com.accloud.ac_service_android_wifi_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACException;
import com.google.zxing.WriterException;
import zxing.encoding.BitmapUtil;

/**
 * Created by Administrator on 2015/4/29.
 */
public class ShareActivity extends BaseActivity {
    private String code;
    private long deviceId;
    private ImageView img;
    private Button refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        code = getIntent().getStringExtra("shareCode");
        deviceId = getIntent().getLongExtra("deviceId", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setTitle(getString(R.string.share_title));
        setNavBtn(R.drawable.back, 0);


        img = (ImageView) findViewById(R.id.share_img);
        refresh = (Button) findViewById(R.id.share_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AC.bindMgr().getShareCode(Config.SUBMAJORDOMAIN, deviceId, 5 * 60, new PayloadCallback<String>() {
                    @Override
                    public void success(String shareCode) {
                        showToast("刷新成功");
                        setImg(shareCode);
                    }

                    @Override
                    public void error(ACException e) {
                        Pop.popToast(ShareActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                    }
                });
            }
        });
        setImg(code);
    }

    public void setImg(String code) {
        Bitmap bitmap;
        try {
            bitmap = BitmapUtil.createQRCode(code, getResources().getDimensionPixelOffset(R.dimen.share_img));

            if (bitmap != null) {
                img.setImageBitmap(bitmap);
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void HandleTitleBarEvent(TitleBar component, View v) {
        finish();
    }
}
