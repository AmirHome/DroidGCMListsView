package com.amirhome.droidgcmlistsview;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AmirHome.com o9125 on 2/17/2017.
 */
public class ServiceILive extends IntentService {
    // Create the Handler object (on the main thread by default)
    Handler handler = new Handler();

    public ServiceILive() {
        super("ServiceILive");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        getInfo();

//        handler.postDelayed(runnable, 100);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        // Do work here, based on the contents of dataString
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
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getInfo();
                            Log.d("AmirHomeLog", "20000 onSuccessResponse" );
                        }
                    }, MainActivity.PERIOD_TIME_CHECKING);

                } catch (JSONException e) {
                    MainActivity.restourantn_no = "";
                    MainActivity.restourantn_title = "";
                    MainActivity.open_status = "";
                    MainActivity.service_status = "";

                    // Refresh Action Menu
                    MainActivity.isChangedStat = true;

                    //Do something after 5s = 5000ms
                   handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getInfo();
//                            Log.d("AmirHomeLog", "5000 catch run" );
                        }
                    }, 5000);
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

                //Do something after 5s = 5000ms
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getInfo();
//                        Log.d("AmirHomeLog", "5000 onErrorResponse" );
                    }
                }, 5000);
                Log.e("AmirHomeLog", "onErrorResponse " + error.toString());
            }
        });
    }

    public  Runnable sRunnableOneMinut = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            getInfo();

      /* and here comes the "trick" */
            handler.postDelayed(this, 100);
        }
    };

    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d("AmirHomeLog", "Called on main thread");
            getInfo();
            // Repeat this the same runnable code block again another 2 seconds
            handler.postDelayed(runnableCode, 20000);
        }
    };
}