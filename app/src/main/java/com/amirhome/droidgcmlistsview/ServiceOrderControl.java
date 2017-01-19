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
    String orderId;
    String orderDate;
    public boolean res;
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

        Firebase.setAndroidContext(this);
        orderId = intent.getStringExtra("ServiceOrderControl.orderId");
        orderDate = intent.getStringExtra("ServiceOrderControl.order_date");

        // The new order came
        setNotification(orderId);

        /* Open Dailog */

        // Set Timer if status == 0
        setTimerStatusCtrl(orderId, orderDate);

        Toast.makeText(this, "Service Started" + Integer.toString(startId) + " " + orderId, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }

    private void setTimerStatusCtrl(final String orderId, final String orderDate) {

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {

                res = setStatusReject(orderId);
                if (res) {

                    //TODO: service status is off
                    //TODO: set penalty
                    DetailActivity.httpRequestSyncCart(orderId);
                    MainActivity.setServiceStatus("deactive");
                    MainActivity.swServiceStatus.setChecked(false);

                    Log.d("AmirHomeLog", "StatusReject");

                    stopService(new Intent(getBaseContext(), ServiceOrderControl.class));

//                    setNotification(orderId);
                }

                //        Cart c = new Cart();
                //        c.setOrderId("test");
                //        c.setStatusOrder("0");
                //stopService(new Intent(getBaseContext(), ServiceOrderControl.class));
                //handler.postDelayed(this, 1000);


            }
        };

//        handler.postDelayed(r, 1000);

        // Control orderDate
//        String diff = getDiff(orderDate);
        String diff = MainActivity.getDiff(orderDate);

        if (Integer.parseInt(diff) < MainActivity.DelayedMili) {
            handler.postDelayed(r, MainActivity.DelayedMili - Integer.parseInt(diff));
        } else {
            handler.postDelayed(r, 1000);
        }

    }

    private boolean setStatusReject(String orderId) {

        Firebase.setAndroidContext(this);

        fire = new Firebase(MainActivity.DB_URL + MainActivity.rCode + "/" + orderId + "/");
        fire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {

                if (ds.child("status_order").getValue().equals("0")) {
                    // Order is reject
                    fire.child("status_order").setValue("RejectAuto");

//                    DetailActivity.httpRequestSyncCart(fire.getKey());
                    res = true;
                } else {
                    res = false;
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        return res;
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


/*    @NonNull
    private String getDiff(String dateStop) {
        // SimpleDateFormat Class
        SimpleDateFormat sdfDateTime = new SimpleDateFormat(DateTimeFormat, Locale.US);
        String currentTime = sdfDateTime.format(new Date(System.currentTimeMillis()));

        SimpleDateFormat format = new SimpleDateFormat(DateTimeFormat);

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStop);
            d2 = format.parse(currentTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(d2.getTime() - d1.getTime());
    }*/
}

