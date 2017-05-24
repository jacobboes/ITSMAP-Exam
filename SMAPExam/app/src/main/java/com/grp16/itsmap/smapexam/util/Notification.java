package com.grp16.itsmap.smapexam.util;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.grp16.itsmap.smapexam.R;
import com.grp16.itsmap.smapexam.app.MainActivity;
import static android.content.Context.NOTIFICATION_SERVICE;

//https://developer.android.com/training/notify-user/build-notification.html
public class Notification {
    private Context context;

    public Notification(Context context){
        this.context = context;
    }

    public void Send(String title, String msg){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.location_mark)
                        .setContentTitle(title)
                        .setContentText(msg);

        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = 001;
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(mNotificationId);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
