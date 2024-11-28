package org.tensorflow.lite.examples.objectdetection

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import org.tensorflow.lite.examples.objectdetection.ObjectDetectorHelper.Companion.DELEGATE_CPU
import org.tensorflow.lite.examples.objectdetection.ObjectDetectorHelper.Companion.DELEGATE_GPU
import org.tensorflow.lite.examples.objectdetection.ObjectDetectorHelper.DetectorListener
import org.tensorflow.lite.examples.objectdetection.cameraone.BitmapUtil
import org.tensorflow.lite.examples.objectdetection.cameraone.CameraDataUtil
import org.tensorflow.lite.examples.objectdetection.cameraone.CameraManager
import org.tensorflow.lite.examples.objectdetection.cameraone.CameraManager.OnCameraActionCallback
import org.tensorflow.lite.examples.objectdetection.cameraone.CameraTextureView
import org.tensorflow.lite.examples.objectdetection.draw.AiFoodRectDrawView
import org.tensorflow.lite.examples.objectdetection.draw.PlanteDrawHelperUtil
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.ArrayList
import java.util.LinkedList

/**
 * author : vinda
 * date : 2021/12/25 8:59
 * description :TextureView 实现相机预览
 */
class CameraTextureTestActivity : Activity(), View.OnClickListener, OnCameraActionCallback, DetectorListener, CameraManager.OnCameraPreviewDataCallback {
    var thisThreshold: Float = 0.5f
    var thisNumThreads: Int = 2
    var thisMaxResults: Int = 10
    var thisCurrentDelegate: Int = 0
    var thisCurrentModel: Int = 0


    private var mTextureView: CameraTextureView? = null

    private var mOverlayView: AiFoodRectDrawView? =null
    private var plantDrawHelper: PlanteDrawHelperUtil? = null //识别框绘制辅助类

    var tv_time:TextView ?= null
    var tv_threshold:TextView ?=null
    var tv_thread_num:TextView ?=null

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_textureview)
        initDetectCore()
        initView()
    }

    private fun initDetectCore() {
        objectDetectorHelper = ObjectDetectorHelper(
            context =this,
            threshold = thisThreshold,
            maxResults = thisMaxResults,
            numThreads = thisNumThreads,
            currentDelegate = DELEGATE_CPU,
            currentModel = thisCurrentModel,
            objectDetectorListener = this)
    }

    private fun initView() {
        tv_time = findViewById(R.id.tv_time)
        tv_threshold = findViewById(R.id.tv_threshold)
        tv_threshold?.text = "识别阈值:${thisThreshold}"

        tv_thread_num = findViewById(R.id.tv_thread_num)
        tv_thread_num?.text = "识别线程数量:${thisNumThreads}"

        mOverlayView = findViewById(R.id.overlay)

        plantDrawHelper = PlanteDrawHelperUtil(
            1600,
            1200,
           1600,
            1200,
           0,
            0,
            false,
            true,
            false)

        mTextureView = findViewById(R.id.texture_view)
        mTextureView?.setOnCameraPreviewDataCallback(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            else -> {}
        }
    }


    override fun onTakePictureComplete(bitmap: Bitmap) {}
    override fun onError(error: String) {}
    override fun onResults(results: MutableList<Detection>?, inferenceTime: Long, imageHeight: Int, imageWidth: Int) {
        runOnUiThread {
            tv_time?.text = String.format("%d ms", inferenceTime)

            // Pass necessary information to OverlayView for drawing on the canvas
//            mOverlayView?.setResults(
//                results ?: LinkedList<Detection>(),
//                imageHeight,
//                imageWidth
//            )
//
//            // Force a redraw
//            mOverlayView?.invalidate()
            results?.let {
                mOverlayView?.updateRect(it as ArrayList<Detection>?, plantDrawHelper)
            }

        }
    }

    override fun cameraPreviewCallback(mCameraData: ByteArray?) {
        Log.d("aaa","cameraPrecallback ${mCameraData?.size}")
        mCameraData?.let {
//            var aaa = byteArrayOf()
//            CameraDataUtil.NV212RGBorBGR(it,800,600,aaa,true)


            val bitmap =BitmapUtil().processImage(it,1600,1200)
            bitmap?.let {
                objectDetectorHelper.detect(bitmap, 0)
            }
        }



    }
}