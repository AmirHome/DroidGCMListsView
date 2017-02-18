package com.amirhome.droidgcmlistsview;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AmirHome.com o9125 on 2/17/2017.
 */
public class ServiceILive extends Service {

    public ServiceILive() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        getInfo();
        return START_STICKY;
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

                    // Create the Handler object (on the main thread by default)
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            getInfo();
                            Log.d("AmirHomeLog", "20000 onSuccessResponse" );
                        }
                    };
                    handler.postDelayed(r, MainActivity.PERIOD_TIME_CHECKING);

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