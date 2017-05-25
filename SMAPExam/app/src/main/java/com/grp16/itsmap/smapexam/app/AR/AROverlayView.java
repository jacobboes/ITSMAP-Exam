package com.grp16.itsmap.smapexam.app.AR;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;

import com.grp16.itsmap.smapexam.app.ARCameraInteraction;
import com.grp16.itsmap.smapexam.model.POI;
import com.grp16.itsmap.smapexam.util.LocationHelper;
import com.grp16.itsmap.smapexam.util.PoiListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AROverlayView extends View implements PoiListener{
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<POI> arPoints;
    private ARCameraInteraction arCameraInteraction;
    Timer timer;
    int Interval = 30;
    int TimerInterval = 1000 * Interval;

    public AROverlayView(Context context, ARCameraInteraction arCameraInteraction) {
        super(context);
        this.arCameraInteraction = arCameraInteraction;
        this.currentLocation = arCameraInteraction.getLocation();
        this.arPoints = arCameraInteraction.getPoiList();
        this.arCameraInteraction.addListener(this);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runTimer();
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
        runInvalidate();
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
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(60);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude());
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).latitude, arPoints.get(i).longitude, arPoints.get(i).altitude);
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                canvas.drawCircle(x, y, radius, paint);
                canvas.drawText(arPoints.get(i).name, x - (30 * arPoints.get(i).name.length() / 2), y - 80, paint);

            }
        }
    }


}
