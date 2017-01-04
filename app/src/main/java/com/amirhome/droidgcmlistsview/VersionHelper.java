package com.amirhome.droidgcmlistsview;

import android.app.Activity;

/**
 * Created by AmirHome.com on 1/3/2017.
 */
class VersionHelper
{
    static void refreshActionBarMenu(Activity activity)
    {
        activity.invalidateOptionsMenu();
    }
}