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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ServiceOrderControl extends Service {
    Firebase fire;
    String orderId;
    String orderDate;
    int notifyID = 0;

    public ServiceOrderControl() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orderId = intent.getStringExtra("ServiceOrderControl.orderId");
        orderDate = intent.getStringExtra("ServiceOrderControl.order_date");

        // The new order came
        setNotification(orderId);

        // Set Timer if status == 0
        setTimerStatusCtrl(orderId, orderDate);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setTimerStatusCtrl(final String orderId, final String orderDate) {

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                setStatusReject(orderId);
            }
        };

        // Control orderDate
        String diff = MainActivity.getDiff(orderDate);

        if (Integer.parseInt(diff) < MainActivity.DelayedMili) {
            handler.postDelayed(r, MainActivity.DelayedMili - Integer.parseInt(diff));
        } else {
            handler.postDelayed(r, 1000);
        }

    }

    private void setStatusReject(final String orderId) {

        Firebase.setAndroidContext(this);
        fire = new Firebase(MainActivity.DB_URL + MainActivity.rCode + "/" + orderId + "/");

        fire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                if (ds.child("status_order").getValue().equals("0")) {
                    try {
                        // Order is reject
                        fire.child("status_order").setValue("RejectAuto");

                        //service status is off
                        MainActivity.setServiceStatus("deactive");
                        MainActivity.swServiceStatus.setChecked(false);
                        DetailActivity.httpRequestSyncCart(orderId);

                        // Service Stoped
                        stopService(new Intent(getBaseContext(), ServiceOrderControl.class));

                        // setNotification(orderId);
                    } catch (Exception e) {
                        Log.e("AmirHomeLog", "Order is reject or service status is off or Service Stoped " + e);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void setNotification(String id) {
        try {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Sets an ID for the notification, so it can be updated
            Intent intent = new Intent(this, DetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

            android.app.Notification notif = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.donation_white)
                    .setContentTitle("Order #" + id + " is waiting..")
                    .setContentText("New order come now..")
                    .setSound(uri)
                    .build();

            notificationManager.notify(notifyID++, notif);
        } catch (Exception e) {
//            Log.d("AmirHomeLog", e.toString());
        }
    }
}

