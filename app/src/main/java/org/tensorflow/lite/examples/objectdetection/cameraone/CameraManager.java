package org.tensorflow.lite.examples.objectdetection.cameraone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * author : vinda
 * date : 2021/12/25 9:00
 * description :
 */
public class CameraManager {
    private static CameraManager mCameraManager;
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private OnCameraActionCallback mOnCameraActionCallback;
    private OnCameraPreviewDataCallback mOnCameraPreviewDataCallback;
    private Camera mCamera;
    private ToneGenerator mToneGenerator;
    private Context mContext;
    private byte[] mCameraData;//拍照返回的图像数据
    private boolean isPreviewing = false;
    private int mCurrentCameraFacing;//当前摄像头
    private int mDeviceOrientation = TOP;

    private byte[] previewBuffer;

    public static synchronized CameraManager getInstance() {
        if (mCameraManager == null) {
            mCameraManager = new CameraManager();
        }
        return mCameraManager;
    }

    /**
     * 打开Camera
     */
    public void openCamera(Context context, int cameraId, SurfaceTexture surface, int width, int height) {
        mContext = context;
        mCamera = Camera.open(cameraId);
        mCurrentCameraFacing = 0;
        startPreview(surface, width, height);
    }

    /**
     * 使用TextureView预览Camera
     *
     * @param surface
     */
    public void startPreview(SurfaceTexture surface, int width, int height) {
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }
            initCamera(width, height);
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mCamera.release();
            mCamera = null;
            mContext = null;
        }
    }

    //176 144
    //640 480
    //1920 1080
    //3840x2880
    private int previewSizeWidth = 1600;
    private int previewSizeHeight = 1200;

    private void initCamera(int width, int height) {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<Integer> supportFormats = params.getSupportedPreviewFormats();
//            params.setPictureFormat(ImageFormat.NV21);//设置拍照后存储的图片格式
            params.setPreviewFormat(ImageFormat.NV21);
            //设置PreviewSize和PictureSize
            //Size pictureSize = getOptimalSize(params.getSupportedPictureSizes(), width, height);
            params.setPictureSize(previewSizeWidth, previewSizeHeight);
            // Size previewSize = getOptimalSize(params.getSupportedPreviewSizes(), width, height);
            params.setPreviewSize(previewSizeWidth, previewSizeHeight);

            List<Integer> frameRates = params.getSupportedPreviewFrameRates();
            int nowFrameRates = params.getPreviewFrameRate();

            List<Camera.Size> supportedPreviewSizes = params.getSupportedPreviewSizes();

            //params.setPreviewFrameRate(15);
            previewBuffer = new byte[previewSizeWidth * previewSizeHeight * 12 / 8];
            mCamera.addCallbackBuffer(previewBuffer);

            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    mCameraData = bytes;
                    mCamera.addCallbackBuffer(previewBuffer);
                    if (mOnCameraPreviewDataCallback != null) {
                        mOnCameraPreviewDataCallback.cameraPreviewCallback(mCameraData);
                    }
                }
            });
            //mCamera.setDisplayOrientation(270);

//            List<String> focusModes = params.getSupportedFocusModes();
//            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
//                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//            }
            mCamera.setParameters(params);

            Camera.Size pictureSize = mCamera.getParameters().getPictureSize();
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();

            mCamera.startPreview();//开启预览

            isPreviewing = true;
        }
    }


    /**
     * 拍照
     */
    public void takePicture(OnCameraActionCallback callback) {
        mOnCameraActionCallback = callback;
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    public void setOnCameraPreviewDataCallback(OnCameraPreviewDataCallback callback) {
        mOnCameraPreviewDataCallback = callback;
    }

    /**
     * 保存照片
     */
    public String save() {
        if (mCameraData != null) {
            BitmapUtil bitmapUtil = new BitmapUtil();
            bitmapUtil.processImage(mCameraData, previewSizeWidth, previewSizeHeight);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);//data是字节数据，将其解析成位图
            Log.d("aaa", "asf");
            //保存图片到sdcard
            //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
            //图片竟然不能旋转了，故这里要旋转下
//            bitmap = BitmapUtils.rotateBitmap(bitmap, 90.0f);
//            return BitmapUtils.saveBitmap(mContext, bitmap);
        }
        return null;
    }

    public void cancel() {
        //再次进入预览
        mCamera.startPreview();
        isPreviewing = true;
    }

    public interface OnCameraActionCallback {
        void onTakePictureComplete(Bitmap bitmap);
    }

    public interface OnCameraPreviewDataCallback {
        void cameraPreviewCallback(byte[] mCameraData);
    }

    //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {

        public void onShutter() {
            // TODO Auto-generated method stub
            if (mToneGenerator == null) {
                //发出提示用户的声音
                mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC,
                        ToneGenerator.MAX_VOLUME);
            }
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2);
        }
    };

    //对jpeg图像数据的回调,最重要的一个回调
    private Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            mCameraData = data;
//            if (mOnCameraActionCallback != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
//                if (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    if (mDeviceOrientation == TOP) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 90.0f);
//                    } else if (mDeviceOrientation == RIGHT) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 180.0f);
//                    } else if (mDeviceOrientation == BOTTOM) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 270.0f);
//                    }
//                } else if (mCurrentCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                    if (mDeviceOrientation == TOP) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 270.0f);
//                    } else if (mDeviceOrientation == RIGHT) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 180.0f);
//                    } else if (mDeviceOrientation == BOTTOM) {
//                        bitmap = BitmapUtils.rotateBitmap(bitmap, 90.0f);
//                    }
//                }
//                mOnCameraActionCallback.onTakePictureComplete(bitmap);
//            }
        }
    };
}

