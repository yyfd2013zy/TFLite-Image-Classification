package org.tensorflow.lite.examples.objectdetection.cameraone

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class BitmapUtil {

    fun processImage(imageData: ByteArray, width: Int, height: Int): Bitmap? { // 将预览数据转换为 Bitmap
        Log.d("aaa","保存图片")
        try {
            val yuvImage = YuvImage(imageData, ImageFormat.NV21, width, height, null)
            val outputStream = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, outputStream)
            val jpegData = outputStream.toByteArray()
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inSampleSize = 1
            val bmp =BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size, options)
            //saveBitmapToLocalStorage(bmp,"/sdcard/test.jpg");
            return bmp
        } catch (ex: java.lang.Exception) {
            println("图片转换异常 " + ex.message)
            return null
        }

    }

    fun cropCenterBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        // 计算截取区域的起始坐标
        val x = (width - 2000) / 2
        val y = (height - 1000) / 2
        // 截取正中心的 1000x1000 区域
        val croppedBitmap = Bitmap.createBitmap(bitmap, x, y, 2000, 1000)
        return croppedBitmap
    }

    private fun saveBitmapToLocalStorage(bitmap: Bitmap, filePath: String) {
        val file = File(filePath)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close() // 保存成功，执行你的操作
            Log.d("aaa","保存图片完成")
        } catch (e: IOException) {
            e.printStackTrace() // 保存失败，处理异常情况
        }
    }



}