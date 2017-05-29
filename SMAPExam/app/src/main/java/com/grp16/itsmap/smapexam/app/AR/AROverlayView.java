package com.grp16.itsmap.smapexam.app.AR;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.grp16.itsmap.smapexam.app.ARCameraInteraction;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.LocationHelper;
import com.grp16.itsmap.smapexam.util.PoiListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AROverlayView extends View implements PoiListener{
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<POI> arPoints;
    private ARCameraInteraction arCameraInteraction;
    private Context contextLocal;
    private List<DrawObj> threadResults;
    Timer timer;
    int Interval = 30;
    int TimerInterval = 1000 * Interval;

    public AROverlayView(Context context, ARCameraInteraction arCameraInteraction) {
        super(context);
        this.contextLocal = context;
        this.arCameraInteraction = arCameraInteraction;
        this.currentLocation = arCameraInteraction.getLocation();
        this.arPoints = arCameraInteraction.getPoiList();
        this.arCameraInteraction.addListener(this);


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ((Activity) contextLocal).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runTimer();
                    }
                });
            }
        }, 0, TimerInterval);
    }
    @Override
    public void finalize() {
        arCameraInteraction.removeListener(this);
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        runInvalidate();
    }

    @Override
    public void dataReady(List<POI> data) {
        arPoints = data;
        this.currentLocation = arCameraInteraction.getLocation();
    }

    private void runInvalidate() {
        this.invalidate();
    }

    private void runTimer() {
        currentLocation = arCameraInteraction.getLocation();
        runInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        ReentrantLock lock = new ReentrantLock();
        threadResults = new ArrayList<>();
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < arPoints.size(); i ++) {
            Runnable worker = new WorkerThread(width, height, arPoints.get(i), threadResults, lock);
            executor.execute(worker);
        }
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        canvas.save();
        canvas.rotate(getOrientation(), width/2, height/2);

        for (DrawObj obj : threadResults){
            if (AppUtil.getPoiColorMapping().containsKey(obj.type.get(0))) {
                paint.setColor(AppUtil.getPoiColorMapping().get(obj.type.get(0)));
            }else {
                paint.setColor(AppUtil.getPoiColorMapping().get("other"));
            }

            canvas.drawCircle(obj.x, obj.y, radius, paint);
            canvas.drawText(obj.name, obj.x - (30 * obj.name.length() / 2), obj.y - 80, paint);
        }
        canvas.restore();
    }


    private int getOrientation(){
        int degrees = 0;
        switch (arCameraInteraction.getOrientation()) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = -90;
                break;
            case Surface.ROTATION_180:
                degrees = 0; //180
                break;
            case Surface.ROTATION_270:
                degrees = 90; //270
                break;
        }
        return degrees;
    }

    public class WorkerThread implements Runnable {

        private int width;
        private int height;
        private POI poi;
        private List<DrawObj> drawObjList;
        private ReentrantLock lock;

        public WorkerThread(int width, int height, POI poi, List<DrawObj> drawObjList, ReentrantLock lock){
            this.width = width;
            this.height = height;
            this.poi = poi;
            this.drawObjList = drawObjList;
            this.lock = lock;
        }

        @Override
        public void run() {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude());
            float[] pointInECEF = LocationHelper.WSG84toECEF(poi.latitude, poi.longitude, poi.altitude);
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                DrawObj drawObj = new DrawObj();
                drawObj.x = (0.5f + cameraCoordinateVector[0] / cameraCoordinateVector[3]) * width;
                drawObj.y = (0.5f - cameraCoordinateVector[1] / cameraCoordinateVector[3]) * height;

                drawObj.name = poi.name;
                drawObj.type = poi.type;

                lock.lock();
                try {
                    drawObjList.add(drawObj);
                }catch (Exception e){
                    Log.d(WorkerThread.class.toString(), "Not able to add obj to list");
                }
                finally {
                    lock.unlock();
                }
            }
        }
    }

    private class DrawObj{
        public float x;
        public float y;
        public String name;
        public List<String> type;
    }
}
