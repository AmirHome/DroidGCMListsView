package com.amirhome.droidgcmlistsview;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by AmirHome.com o9125 on 2/16/2017.
 */
public interface VolleyCallback {
    void onSuccessResponse(JSONObject result);
    void onErrorResponse(VolleyError result);
}
