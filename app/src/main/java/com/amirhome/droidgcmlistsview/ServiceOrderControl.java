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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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

        final String orderId = intent.getStringExtra("ServiceOrderControl.data");

        Toast.makeText(this, "Service Started" + Integer.toString(startId) + " " + orderId, Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                addData(orderId);

                //stopService(new Intent(getBaseContext(), ServiceOrderControl.class));
                //handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 10000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Firebase.setAndroidContext(this);

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    private void addData(final String orderId) {
        Firebase.setAndroidContext(this);
        fire = new Firebase(MainActivity.DB_URL + "imei0000012/" + orderId + "/");
        fire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                if (ds.child("status_order").getValue().equals("0"))
                Log.d( "MainActivity", orderId + ds.child("status_order").getValue() );
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
//        Cart c = new Cart();
//        c.setOrderId("test");
//        c.setStatusOrder("0");


        //fire.child("status_order").setValue("2");
        Notification();
    }


    public void Notification() {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(this, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            android.app.Notification notif = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("title")
                    .setContentText("text")
                    .setSound(uri)
                    .build();


            notificationManager.notify(0, notif);
        } catch (Exception e) {
            Log.d("MainActivity", e.toString());
        }
    }
}

