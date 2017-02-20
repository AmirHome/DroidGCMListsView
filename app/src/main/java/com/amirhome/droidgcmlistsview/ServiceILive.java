package com.amirhome.droidgcmlistsview;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AmirHome.com o9125 on 2/17/2017.
 */
public class ServiceILive extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d("AmirHomeLog", "onRunTask");
/*        MainActivity.isRepeat = true;
        getInfo();*/
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AmirHomeLog", "onStartCommand");
        MainActivity.isRepeat = true;
        getInfo();
//        return GcmTaskService.START_STICKY_COMPATIBILITY;
        return super.onStartCommand(intent, flags, startId);
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
                    MainActivity.isRepeat = true;
                    Log.d("AmirHomeLog", "20000 onSuccessResponse");

                } catch (JSONException e) {
                    MainActivity.restourantn_no = "";
                    MainActivity.restourantn_title = "";
                    MainActivity.open_status = "";
                    MainActivity.service_status = "";

                    // Refresh Action Menu
                    MainActivity.isChangedStat = true;

                    if (MainActivity.isRepeat) {
                        MainActivity.isRepeat = false;
                        //Do something after 7s = 7000ms
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                getInfo();
                                Log.d("AmirHomeLog", "7000 catch");
                            }
                        };
                        for (int i=1; i<3; i++) {
                            final int finalRepeat = i;
                            handler.postDelayed(r, 7000 * finalRepeat);
                        }
                    }
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

                if (MainActivity.isRepeat) {
                    MainActivity.isRepeat = false;
                    //Do something after 6s = 6000ms
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            getInfo();
                            Log.d("AmirHomeLog", "7000 onErrorResponse");
                        }
                    };
                    for (int i=1; i<3; i++) {
                        final int finalRepeat = i;
                        handler.postDelayed(r, 7000 * finalRepeat);
                    }
                }
//                handler.postDelayed(r, 6000);
                Log.e("AmirHomeLog", "onErrorResponse " + error.toString());
            }
        });
    }

}