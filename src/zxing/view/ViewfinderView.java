/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.accloud.ac_service_android_wifi_demo.R;
import zxing.camera.CameraManager;


public final class ViewfinderView extends View {
    /**
     * Log TAG
     */
    private static final String TAG = "ViewfinderView";

    /**
     * Log switch
     */
    private static final boolean LOG = false;

    /**
     * 扫描线的动画效果的alpha值数组
     */
    private static final int[] SCANNER_ALPHA = {
            0, 20, 40, 60, 80, 100, 120, 140, 160, 180,
            200, 180, 160, 140, 120, 100, 80, 60, 40, 20};

    /**
     * 扫描线的动画效果的时间间隔
     */
    private static final long ANIMATION_DELAY = 60L;

    /**
     * Point size
     */
    private static final int POINT_SIZE = 0;

    /**
     * 十字架的长度的一半
     */
    private final int mCrossHalf;

    /**
     * 画十字架的画笔的宽度
     */
    private final int mCrossStroke;

    /**
     * CameraManager
     */
    private CameraManager mCameraManager;

    /**
     * 画聚焦框的Paint
     */
    private final Paint mPaint;

    /**
     * 画聚焦提示语的Paint
     */
    private final Paint mTextPaint;

    /**
     * Framing Rectangle
     */
    private Rect mFrameRect = new Rect();

    /**
     * Mask color
     */
    private final int mMaskColor;

    /**
     * Laser color
     */
    private final int mLaserColor;

    /**
     * Frame Rectangle border
     */
    private final Drawable mLaserBorder;

    /**
     * 扫描提示语：请对准想要识别的二维码
     */
    private final String mLaserTip;

    /**
     * Laser line current alpha
     */
    private int mScannerAlpha;

    /**
     * 红色的聚焦十字
     */
    private Path mCrossPath = new Path();

    /**
     * 设备是否支持竖屏
     */
    private final boolean mSupportCameraPortrait;

    /**
     * This constructor is used when the class is built from an XML resource.
     *
     * @param context Context
     * @param attrs   AttributeSet
     */
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStyle(Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);

        Resources resources = getResources();
        mMaskColor = resources.getColor(R.color.viewfinder_mask);
        mLaserColor = resources.getColor(R.color.viewfinder_laser);
        mLaserBorder = resources.getDrawable(R.drawable.scan_border3x);
        mLaserTip = resources.getString(R.string.barcode_laser_tip);
        mCrossHalf = resources.getDimensionPixelSize(R.dimen.barcode_cross_length) / 2;
        mCrossStroke = resources.getDimensionPixelSize(R.dimen.barcode_cross_width);
        mScannerAlpha = 0;

        mSupportCameraPortrait = CameraManager.supportCameraPortrait();
    }

    /**
     * 设置CameraManager
     *
     * @param cameraManager CameraManager
     */
    public void setCameraManager(CameraManager cameraManager) {
        mCameraManager = cameraManager;
    }

    //CHECKSTYLE:OFF
    @Override
    public void onDraw(Canvas canvas) {
        if (LOG) {
            Log.d(TAG, "onDraw()");
        }
        if (mCameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }

        final Rect frame = mFrameRect;
        if (frame.isEmpty()) {
            final Rect framingRect = mCameraManager.getFramingRect();
            if (framingRect == null) {
                return;
            } else {
//                if (mSupportCameraPortrait) {
//                    frame.set(framingRect.top, framingRect.left, framingRect.bottom, framingRect.right);
//                } else {
                frame.set(framingRect);
//                }
            }
        }

        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        final int min = Math.min(width, height);

        // Draw the exterior (i.e. outside the framing rect) darkened
        canvas.drawColor(mMaskColor);
        canvas.save();
        canvas.clipRect(frame.left + 2, frame.top + 2, frame.right - 2, frame.bottom - 2);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        canvas.restore();
        mPaint.setStrokeWidth(min / 240.0f);
        mPaint.setColor(Color.BLACK);
        // canvas.clipRect(0, 0, width, height);
        canvas.drawRect(frame, mPaint);

        // Draw a red "laser scanner" line through the middle to show decoding is active
        mPaint.setColor(mLaserColor);
        mPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
        mPaint.setStrokeWidth(mCrossStroke);
        mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
        if (mCrossPath.isEmpty()) {
            int halfWidth = width / 2;
//            int halfHeight = height / 2;
            int halfHeight = (frame.bottom - frame.top) / 2 + frame.top;
            mCrossPath.moveTo(halfWidth - mCrossHalf, halfHeight);
            mCrossPath.lineTo(halfWidth + mCrossHalf, halfHeight);
            mCrossPath.moveTo(halfWidth, halfHeight - mCrossHalf);
            mCrossPath.lineTo(halfWidth, halfHeight + mCrossHalf);
        }
        canvas.drawPath(mCrossPath, mPaint);

        mLaserBorder.setBounds(frame.left - POINT_SIZE, frame.top - POINT_SIZE,
                frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
        mLaserBorder.draw(canvas);
        if (LOG) {
            final int w = frame.width();
            final int h = frame.height();
            Log.d(TAG, String.format("width=%d, height=%d, w=%d, h=%d, frame=%s",
                    width, height, w, h, frame.toShortString()));
        }

        mTextPaint.setTextSize(10.0f * min / 240.0f);
        float strWidth = mTextPaint.measureText(mLaserTip);
        canvas.drawText(mLaserTip, 0, mLaserTip.length(), (width - strWidth) / 2,
                frame.bottom + 16 + min / 24.0f, mTextPaint);

        // Request another update at the animation interval, but only repaint
        // the laser line, not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY,
                frame.left - POINT_SIZE,
                frame.top - POINT_SIZE,
                frame.right + POINT_SIZE,
                frame.bottom + POINT_SIZE);
    }
    //CHECKSTYLE:ON

    /**
     * invalidate itself
     */
    public void drawViewfinder() {
        invalidate();
    }
}
