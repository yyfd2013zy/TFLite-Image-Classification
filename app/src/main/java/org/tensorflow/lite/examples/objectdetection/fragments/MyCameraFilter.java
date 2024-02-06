package org.tensorflow.lite.examples.objectdetection.fragments;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.impl.Identifier;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@SuppressLint("UnsafeExperimentalUsageError")
class MyCameraFilter implements CameraFilter {
    @NonNull
    @Override
    public List<CameraInfo> filter(@NonNull List<CameraInfo> cameraInfos) {
        return null;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public Identifier getIdentifier() {
        return CameraFilter.super.getIdentifier();
    }

//    @SuppressLint("RestrictedApi")
//    @NonNull
//    @Override
//    public LinkedHashSet<Camera> filter(@NonNull LinkedHashSet<Camera> cameras) {
//        Log.i("", "cameras size: " + cameras.size());
//        Iterator<Camera> cameraIterator = cameras.iterator();
//        Camera camera = null;
//        while (cameraIterator.hasNext()) {
//            camera = cameraIterator.next();
//            String getImplementationType = camera.getCameraInfo().getImplementationType();
//            Log.i("", "getImplementationType: " + getImplementationType);
//        }
//        LinkedHashSet linkedHashSet = new LinkedHashSet<>();
//        linkedHashSet.add(camera); // 最后一个camera
//        return linkedHashSet;
//    }

//    @NonNull
//    @Override
//    public List<CameraInfo> filter(@NonNull List<CameraInfo> cameraInfos) {
//        return null;
//    }
}