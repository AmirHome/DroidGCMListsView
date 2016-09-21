package com.amirhome.droidgcmlistsview;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class ServiceOrderControl extends Service {
    Firebase fire;

    public ServiceOrderControl() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);

            String value = intent.getStringExtra("ServiceOrderControl.data");

        Toast.makeText(this,"Service Started" + Integer.toString(startId)+ " " + value, Toast.LENGTH_LONG).show();

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                Log.d("MainActivity", "Timer Run");

                //stopService(new Intent(getBaseContext(), ServiceOrderControl.class));
                addData("", "");
                //handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 10000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //String id = intent.getStringExtra("ServiceOrderControl.data");
        Firebase.setAndroidContext(this);
        //fire = new Firebase(MainActivity.DB_URL + "imei0000012/" + id);


        Toast.makeText(this,"Service Stopped", Toast.LENGTH_LONG).show();
    }


    private void addData(String orderId, String statusOrder) {
        fire = new Firebase(MainActivity.DB_URL + "imei0000012/test/");

        //Cart c = new Cart();
        //c.setOrderId("test");
        //c.setStatusOrder("0");

        fire.child("status_order").setValue("2");
        Notification();
    }

    public void Notification() {
        try {
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager =   (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),intent,0);

            android.app.Notification notif = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("title")
                    .setContentText("text")
                    .setSound(uri)
                    .build();


            notificationManager.notify(0, notif);
        } catch (Exception e){
            Log.d("MainActivity", e.toString());
        }
    }
}

