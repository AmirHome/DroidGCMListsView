package com.amirhome.droidgcmlistsview;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by AmirHome.com o9125 on 2/17/2017.
 */
public class ServiceILive extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 10 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, MainActivity.PERIOD_TIME_CHECKING);

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                getInfo();
                handler.postDelayed(r,  MainActivity.PERIOD_TIME_CHECKING);
            }
        };
        handler.postDelayed(r,  100);
        return START_STICKY;
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
                    Toast.makeText(getApplicationContext(), "amir",
                            Toast.LENGTH_SHORT).show();
//                    getInfo();
                }

            });
        }
    }

    public void getInfo() {
        DetailActivity.httpRequestSyncRestaurant("info", new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                try {
                    MainActivity.restourantn_no = result.getJSONObject("data").getString("id");
                    MainActivity.restourantn_title = result.getJSONObject("data").getString("title");
                    MainActivity.open_status = result.getJSONObject("data").getString("open_status");
                    MainActivity.service_status = result.getJSONObject("data").getString("service_status");

                    // Refresh Action Menu
                    MainActivity.isChangedStat = true;

/*                    // Create the Handler object (on the main thread by default)
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            getInfo();
                            Log.d("AmirHomeLog", "20000 onSuccessResponse" );
                        }
                    };
                    handler.postDelayed(r, MainActivity.PERIOD_TIME_CHECKING);*/
                    Log.d("AmirHomeLog", "20000 onSuccessResponse" );

                } catch (JSONException e) {
                    MainActivity.restourantn_no = "";
                    MainActivity.restourantn_title = "";
                    MainActivity.open_status = "";
                    MainActivity.service_status = "";

                    // Refresh Action Menu
                    MainActivity.isChangedStat = true;

                    //Do something after 7s = 7000ms
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            getInfo();
                            Log.d("AmirHomeLog", "7000 catch" );
                        }
                    };
                    handler.postDelayed(r, 7000);
                    Log.e("AmirHomeLog", "catch " + e.getMessage());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                MainActivity.restourantn_no = "";
                MainActivity.restourantn_title = "";
                MainActivity.open_status = "";
                MainActivity.service_status = "";
                // Refresh Action Menu
                MainActivity.isChangedStat = true;

                //Do something after 6s = 6000ms
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        getInfo();
                        Log.d("AmirHomeLog", "6000 onErrorResponse" );
                    }
                };
                handler.postDelayed(r, 6000);
                Log.e("AmirHomeLog", "onErrorResponse " + error.toString());
            }
        });
    }

}