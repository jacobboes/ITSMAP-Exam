package com.grp16.itsmap.smapexam.app.AR;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import com.grp16.itsmap.smapexam.app.ARCameraInteraction;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.network.Database;
import com.grp16.itsmap.smapexam.network.DatabaseListener;
import com.grp16.itsmap.smapexam.util.AppUtil;
import com.grp16.itsmap.smapexam.util.LocationHelper;
import com.grp16.itsmap.smapexam.util.PoiListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

//https://github.com/dat-ng/ar-location-based-android
public class AROverlayView extends View implements PoiListener, DatabaseListener{
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<POI> arPoints;
    private ARCameraInteraction arCameraInteraction;
    private Context contextLocal;
    List<DrawObj> threadResults = new ArrayList<>();
    private Paint paint;
    ReentrantLock lock = new ReentrantLock();
    private Database database = Database.getInstance();
    private int width;
    private int height;
    private Timer calcPointsTimer;
    private Timer updatePosTimer;

    public AROverlayView(Context context, ARCameraInteraction arCameraInteraction) {
        super(context);
        this.contextLocal = context;
        this.arCameraInteraction = arCameraInteraction;
        this.currentLocation = arCameraInteraction.getLocation();
        this.arPoints = arCameraInteraction.getPoiList();
        this.arCameraInteraction.addListener(this);
        database.addListener(this);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        updatePosTimer = new Timer();
        updatePosTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runTimer();
            }
        }, 0, 30 * 1000);


        calcPointsTimer = new Timer();
        calcPointsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                    List<DrawObj> localDrawObj = new ArrayList<>();
                    for (POI poi : arPoints) {
                        if (currentLocation == null) return;

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

                            localDrawObj.add(drawObj);
                        }

                    lock.lock();
                    try {
                        threadResults.clear();
                        threadResults.addAll(localDrawObj);
                    } catch (Exception e) {
                        Log.e("Error adding obj", e.getMessage().toString());
                    } finally {
                        lock.unlock();
                    }
                }
                ((Activity) contextLocal).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runInvalidate();
                    }
                });
            }
        }, 0, 35);
    }
    @Override
    public void finalize() {
        calcPointsTimer.cancel();
        updatePosTimer.cancel();
        arCameraInteraction.removeListener(this);
        database.removeListener(this);
        try {
            super.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int radius = 30;
        width = canvas.getWidth();
        height = canvas.getHeight();

        canvas.save();
        canvas.rotate(getOrientation(), width/2, height/2);

        lock.lock();
        List<DrawObj> localResult = new ArrayList<>();
        try {
            localResult.addAll(threadResults);
        }catch (Exception e){
            Log.e("localResult", "Failed to copy list");
        }
        finally {
            lock.unlock();
        }

        for (DrawObj obj : localResult){
            paint.setColor(Color.DKGRAY);
            for (AppUtil.PoiTypeMapping typeMapping : AppUtil.PoiTypeMapping.values()) {
                for (String s : obj.type) {
                    if(typeMapping.getValue().equals(s)){
                        paint.setColor(typeMapping.getColor());
                        break;
                    }
                }
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
                degrees = 0;
                break;
            case Surface.ROTATION_270:
                degrees = 90;
                break;
        }
        return degrees;
    }

    @Override
    public void dataReady() {
        arPoints = arCameraInteraction.getPoiList();
    }

    private class DrawObj{
        public float x;
        public float y;
        public String name;
        public List<String> type;
    }
}
