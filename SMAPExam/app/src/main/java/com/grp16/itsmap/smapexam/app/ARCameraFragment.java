package com.grp16.itsmap.smapexam.app;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.app.AR.ARCamera;
import com.grp16.itsmap.smapexam.app.AR.AROverlayView;

import static android.content.Context.SENSOR_SERVICE;

/*
 * This class is from Github, and has been refactored to extend Fragment
 * https://github.com/dat-ng/ar-location-based-android
 *
 * The original file can be found here: https://github.com/dat-ng/ar-location-based-android/blob/master/app/src/main/java/ng/dat/ar/ARActivity.java
 */
public class ARCameraFragment extends Fragment implements SensorEventListener{
    private ARCameraInteraction activity;
    private AROverlayView arOverlayView;
    private Camera camera;
    private ARCamera arCamera;
    private FrameLayout cameraContainerLayout;
    private SurfaceView surfaceView;
    private Context context;
    private SensorManager sensorManager;

    public ARCameraFragment() {
        // Required empty public constructor
    }

    public static ARCameraFragment newInstance() {
        return new ARCameraFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arcamera, null);

        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        cameraContainerLayout = (FrameLayout) view.findViewById(R.id.activity_ar);
        surfaceView = (SurfaceView) view.findViewById(R.id.surface_view);
        arOverlayView = new AROverlayView(context, activity);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
        initARCameraView();
        initAROverlayView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof ARCameraInteraction) {
            activity = (ARCameraInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ARCameraInteraction");
        }
    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    private void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    private void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(context, surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open();
                camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex){
                Toast.makeText(context, R.string.CameraNotFound, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            arCamera.setCamera(null);
            camera.release();
            camera = null;
        }
    }

    // Delay changed to Game instead of fastest
    private void registerSensors() {
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrixFromVector = new float[16];
            float[] projectionMatrix = new float[16];
            float[] rotatedProjectionMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);

            if (arCamera != null) {
                projectionMatrix = arCamera.getProjectionMatrix();
            }

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }
}
