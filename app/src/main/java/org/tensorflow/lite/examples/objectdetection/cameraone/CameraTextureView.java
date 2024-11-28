package org.tensorflow.lite.examples.objectdetection.cameraone;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

/**
 * author : vinda
 * date : 2021/12/25 9:00
 * description :
 */
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraTextureView";
    private SurfaceTexture mSurface;
    private int mWidth;
    private int mHeight;

    private int cameraId = 0;

    private CameraManager cameraManager;

    public CameraTextureView(Context context) {
        this(context, null);
        cameraManager = new CameraManager();
    }

    public CameraTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        cameraManager = new CameraManager();
    }

    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setSurfaceTextureListener(this);
        cameraManager = new CameraManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraTextureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setSurfaceTextureListener(this);
    }

    public void setCameraId(int id){
        cameraId = id;
    }

    public void setOnCameraPreviewDataCallback(CameraManager.OnCameraPreviewDataCallback callback){
        cameraManager.setOnCameraPreviewDataCallback(callback);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = surface;
        mWidth = width;
        mHeight = height;
        //开启相机是耗时操作，此处异步处理
        new Thread(new OpenCameraRunnable()).start();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        cameraManager.stopCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurface;
    }

    private class OpenCameraRunnable implements Runnable {

        @Override
        public void run() {
            cameraManager.openCamera(getContext(),cameraId, mSurface, mWidth, mHeight);
        }
    }

    public CameraManager getCameraManager(){
        return cameraManager;
    }
}
