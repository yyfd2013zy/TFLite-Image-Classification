package org.tensorflow.lite.examples.objectdetection.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.tensorflow.lite.task.vision.detector.Detection;

import java.util.ArrayList;

/**
 * 智能结算台-绘画识别框
 */
public class AiFoodRectDrawView extends View {
    private static final String TAG = "DrawView";

    private Canvas mCanvas;
    private Paint mPaint, mTextPaint, mSimilarRectPaint, mSimilarTextPaint, mSeparatorPaint;
    private PlanteDrawHelperUtil drawHelper;
    private long mCurrentTime = System.currentTimeMillis();
    private ArrayList<Detection> mPlanteRects = new ArrayList<>();//识别框

    private Float scaleFactor = 1f;


    private CalibrationListener mCalibrationListener;

    public AiFoodRectDrawView(Context context) {
        super(context);
        init();
        postInvalidate();
    }

    public AiFoodRectDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        postInvalidate();
    }

    public AiFoodRectDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(30);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(1);

        mSimilarRectPaint = new Paint();
        mSimilarRectPaint.setStyle(Paint.Style.FILL);
        mSimilarRectPaint.setColor(Color.parseColor("#50000000")); // Semi-transparent blue color

        mSimilarTextPaint = new Paint();
        mSimilarTextPaint.setTextSize(25);
        mSimilarTextPaint.setStyle(Paint.Style.FILL);
        mSimilarTextPaint.setStrokeWidth(1);
        mSimilarTextPaint.setColor(Color.WHITE);
        mSimilarTextPaint.setTextAlign(Paint.Align.CENTER);

        mSeparatorPaint = new Paint();
        mSeparatorPaint.setColor(Color.WHITE);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            //锁定画布并返回画布对象
            mCanvas = canvas;
            if (mCanvas == null) {
                return;
            }
            mPaint.setStyle(Paint.Style.STROKE);
            /************************************************识别框*****************************************************/
            if (mPlanteRects != null) {
                for (Detection result : mPlanteRects) {
                    RectF boundingBox = result.getBoundingBox();

                    float top = boundingBox.top * scaleFactor;
                    float bottom = boundingBox.bottom * scaleFactor;
                    float left = boundingBox.left * scaleFactor;
                    float right = boundingBox.right * scaleFactor;

                    Rect ftRect = new Rect((int) left, (int) top, (int) right, (int) bottom);

                    String drawText = result.getCategories().get(0).getLabel() + " " +
                            String.format("%.2f", result.getCategories().get(0).getScore());

                    mPaint.setStrokeWidth(4);
                    mPaint.setColor(Color.GREEN);
                    mTextPaint.setColor(Color.GREEN);


                    if (drawHelper != null) {
                        mCanvas.drawRect(drawHelper.adjustRect(ftRect), mPaint);
                        float drawTextX = 0f;
                        if (drawHelper.isNameTextMirror()) {//文字是否镜像显示
                            drawTextX = drawHelper.adjustRect(ftRect).right - (mPaint.measureText(drawText) * 2.5F);
                        } else {
                            drawTextX = drawHelper.adjustRect(ftRect).left;
                        }
                        mCanvas.drawText(drawText, drawTextX, drawHelper.adjustRect(ftRect).top - 10, mTextPaint);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if the touch is within any bounding box
//                for (AiFoodDetectRectInfo plante : mPlanteRects) {
//                    Rect rect = drawHelper.adjustRect(plante.rect);
//                    if (rect.contains((int) touchX, (int)touchY)) {
//                        // Touch is within this bounding box
//                        // Perform your action here, e.g., trigger an event for this bounding box
//                        // You can pass the plante object or its ID to identify which bounding box was clicked
//                        // Example: mListener.onBoundingBoxClicked(plante);
//                        Log.i(TAG, "点击 id:"+plante.name);
//                        break; // Break the loop if a bounding box is found
//                    }
//                }
                break;
            // Handle other touch events like move or release if needed
        }

        // Return true to indicate that the event was handled
        return true;
    }

    /**
     * 绘制餐具识别框
     *
     * @param rects
     * @param _drawHelper
     */
    public void updateRect(ArrayList<Detection> rects, PlanteDrawHelperUtil _drawHelper) {
        if (this.mPlanteRects == null && rects == null) {
            mCurrentTime = 0l;
            return;
        }
        if (drawHelper == null) {
            drawHelper = _drawHelper;
        }
        if (mCurrentTime == 0l) {
            mCurrentTime = System.currentTimeMillis();
        }
        this.mPlanteRects = rects;
        Log.i(TAG, "刷新餐盘识别框");
        postInvalidate();
    }

    public void setmCalibrationListener(CalibrationListener mCalibrationListener) {
        this.mCalibrationListener = mCalibrationListener;
    }

    public void reset() {
        if (mPlanteRects != null) {
            mPlanteRects.clear();
        }
        postInvalidate();
    }

    public interface CalibrationListener {

    }
}
