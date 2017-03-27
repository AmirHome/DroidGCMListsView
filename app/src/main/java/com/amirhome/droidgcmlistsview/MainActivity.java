package com.amirhome.droidgcmlistsview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

//    final static String DB_URL = "https://eat2donate-9f194.firebaseio.com/"; // beta.eat2donate.at
    final static String DB_URL = "https://eat2donatemap.firebaseio.com/"; // beta.eat2donate.ga
//    final static String DB_URL = "https://eat2donat-test.firebaseio.com/"; //test162.eat2donate.at
    public static final String APP_VERSION = "0.0.7.10";

    public static final String DateTimeFormat = "dd.MM.yyyy HH:mm:ss";
    public static final int DelayedMili = 180000;// 3 x 60 x 1000 = 180000 mis
    public static final long PERIOD_TIME_CHECKING = 29L;// the task should be executed every 30 seconds
    public static Switch swServiceStatus;

    static MediaPlayer mPlayer;
    static String rCode;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 777;
    public static String restourantn_no;
    public static String restourantn_title;
    public static String service_status;
    public static String open_status;
    public String currentFilter = "";

    public static boolean isChangedStat = false;
    public static boolean isRepeat = false;
    public static boolean service_first_boot = true;

    private GcmNetworkManager mGcmNetworkManager;

    private TelephonyManager mTelephonyManager;

    //    Firebase fireMain;
    Query fire;
    /* Dailog */
    private int countNewOrder = 0;
    private Dialog dialog;

    public static Handler mHandler = new Handler();

    private Player p;
    ArrayList<Player> players = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.e2d_full_quality);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setTitle("");

        //get and set imei code = restaurant code
        this.setImeiCode();


        // float button
/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlarm();

            //                String msg = "Can you help me please..";
            //                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/
    }


    private void afterPermission() {

        // Starts the Service Live Control
        if (service_first_boot) {
//            Log.d("AmirHomeLog", "Started the Service Live");
            service_first_boot = false;
            String tag = "myScan|1";
            PeriodicTask periodic = new PeriodicTask.Builder()
                    .setService(ServiceILive.class)
                    .setPeriod(PERIOD_TIME_CHECKING)
                    .setFlex(15L)
                    .setTag(tag)
                    .setPersisted(false)
                    .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_ANY)
                    .setRequiresCharging(false)
                    .setUpdateCurrent(true)
                    .build();

            GcmNetworkManager.getInstance(this).schedule(periodic);
        }

        // Refresh Action Menu
        mHandler.postDelayed(mRunnableRefreshActionBarMenu, 100);

        //init firebase
        Firebase.setAndroidContext(this);
        Firebase fireMain = new Firebase(DB_URL + rCode);
        fire = fireMain.orderByChild("/order_date");

        //SET ITS PROPETRIES
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());

        //ADAPTER
        final MyAdapter adapter = new MyAdapter(this, players);
        rv.setAdapter(adapter);

        // get Data
        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {
                        p = new Player();
                        p.setOrder_no(dataSnapshot.getKey());
                        p.setStatus(dataSnapshot.child("status_order").getValue().toString(), dataSnapshot.child("status_delivery").getValue().toString());
                        p.setOrder_cost(dataSnapshot.child("order_cost").getValue().toString());
                        p.setOrder_date(dataSnapshot.child("order_date").getValue().toString());
                        players.add(0, p);
                        rv.setAdapter(adapter);

                        if (dataSnapshot.child("status_order").getValue().toString().equals("0")) {

                            String diff = getDiff(p.getOrder_date());

                            if (Integer.parseInt(diff) < MainActivity.DelayedMili) {
//                            Run Service
                                Intent myService = new Intent(getBaseContext(), ServiceOrderControl.class);
                                myService.putExtra("ServiceOrderControl.orderId", p.getOrder_no());
                                myService.putExtra("ServiceOrderControl.order_date", p.getOrder_date());
                                startService(myService);

//                            Open Dialog
                                newOrderAlert();
                            } else {
                                // Order is reject
                                dataSnapshot.getRef().child("status_order").setValue("RejectAuto");
                                DetailActivity.httpRequestSyncCart(p.getOrder_no());
                            }
                        }

                    } catch (Exception ex) {
                        Log.e("AmirHomeLog", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.d("AmirHomeLog", "onChildChanged");
                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
                // Find position
                int pos = -1;
                for (int i = 0; i < players.size(); i++) {

                    if (dataSnapshot.getKey().equals(players.get(i).getOrder_no())) {
                        pos = i;
                        break;
                    }
                }
                // ReWrite record
                if (pos >= 0) {
                    players.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeChanged(pos, players.size());

                    p = new Player();
                    p.setOrder_no(dataSnapshot.getKey());
                    p.setStatus(dataSnapshot.child("status_order").getValue().toString(), dataSnapshot.child("status_delivery").getValue().toString());
                    p.setOrder_cost(dataSnapshot.child("order_cost").getValue().toString());
                    p.setOrder_date(dataSnapshot.child("order_date").getValue().toString());
                    players.add(pos, p);
                    rv.setAdapter(adapter);
                    adapter.getFilter().filter(currentFilter);

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
                // Find position
                int pos = -1;
                for (int i = 0; i < players.size(); i++) {

                    if (dataSnapshot.getKey().equals(players.get(i).getOrder_no())) {
                        pos = i;
                        break;
                    }
                }
                // ReWrite record
                if (pos >= 0) {
                    players.remove(pos);
                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeChanged(pos, players.size());
                    rv.setAdapter(adapter);
                    adapter.getFilter().filter(currentFilter);

                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        // set filters
        final Button btnAllFilter = (Button) findViewById(R.id.btnAll);
        final Button btnNew = (Button) findViewById(R.id.btnNew);
        final Button btnDeliveryWating = (Button) findViewById(R.id.btnDeliveryWating);
        final Button btnPenalty = (Button) findViewById(R.id.btnPenalty);
        final Button btnRejected = (Button) findViewById(R.id.btnRejected);
        final Button btnCustomerRejected = (Button) findViewById(R.id.btnCustomerRejected);
        final Button btnDelivered = (Button) findViewById(R.id.btnDelivered);

        btnAllFilter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //FILTER AS YOU TYPE
                currentFilter = "";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnAllFilter.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //FILTER AS YOU TYPE
                currentFilter = "btnNew";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnNew.setBackgroundResource(R.color.colorPrimaryDark);
            }
        });
        btnDeliveryWating.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                currentFilter = "btnDeliveryWating";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);

                btnDeliveryWating.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnPenalty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                currentFilter = "btnPenalty";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnPenalty.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnRejected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                currentFilter = "btnRejected";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnRejected.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                currentFilter = "btnDelivered";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnDelivered.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnCustomerRejected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                currentFilter = "btnCustomerRejected";
                adapter.getFilter().filter(currentFilter);
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnCustomerRejected.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
    }

    public  Runnable mRunnableRefreshActionBarMenu = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
//            Log.d("AmirHomeLog", "mRunnableRefreshActionBarMenu"+ isChangedStat);
            if ( isChangedStat ){
//                Log.d("AmirHomeLog", "mRunnableRefreshActionBarMenu if "+ isChangedStat);
//                VersionHelper.refreshActionBarMenu(MainActivity.this);
                VersionHelper.refreshActionBarMenu(MainActivity.this);
                invalidateOptionsMenu();
                isChangedStat = false;
            }
//            invalidateOptionsMenu();
//            isChangedStat = false;
//            VersionHelper.refreshActionBarMenu(MainActivity.this);

      /* and here comes the "trick" */
            mHandler.postDelayed(this, 100);
        }
    };

    public void startAlarm() {
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.alarm_reload);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void stopAlarm() {
        mPlayer.stop();
    }


    private void btnBackColorReset(Button btnAllFilter, Button btnDelivered, Button btnNew, Button btnDeliveryWating, Button btnPenalty, Button btnRejected, Button btnCustomerRejected) {
        btnAllFilter.setBackgroundResource(R.color.half_red);
        btnNew.setBackgroundResource(R.color.half_red);
        btnDeliveryWating.setBackgroundResource(R.color.half_red);
        btnPenalty.setBackgroundResource(R.color.half_red);
        btnRejected.setBackgroundResource(R.color.half_red);
        btnCustomerRejected.setBackgroundResource(R.color.half_red);
        btnDelivered.setBackgroundResource(R.color.half_red);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void setImeiCode() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            this.rCode = this.getDeviceImei();
            this.afterPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem itemSwitch = menu.findItem(R.id.ms_service_status);
        itemSwitch.setActionView(R.layout.switch_layout);

//        Log.d("AmirHomeLog","onCreateOptionsMenu");
        swServiceStatus = (Switch) menu.findItem(R.id.ms_service_status).getActionView().findViewById(R.id.switchForActionBar);

//        swServiceStatus.setChecked(false);
        if ("Active".equals(this.service_status))
            swServiceStatus.setChecked(true);
        else
            swServiceStatus.setChecked(false);

        swServiceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setServiceStatus("active");
                    Toast.makeText(MainActivity.this, R.string.send_request_active, Toast.LENGTH_LONG).show();
                } else {
                    setServiceStatus("deactive");
                    Toast.makeText(MainActivity.this, R.string.send_request_deactive, Toast.LENGTH_LONG).show();
                }
            }
        });

        MenuItem imeinumber = menu.findItem(R.id.imeinumber);
        imeinumber.setTitle(this.rCode);

        MenuItem ipnumber = menu.findItem(R.id.ipnumber);
        String ipAddress = getLocalIpAddress();
        if (ipAddress.isEmpty()) {
            ipnumber.setIcon(R.drawable.disconnected_50);
            this.service_status = null;
            this.open_status = null;
            this.restourantn_no = null;
        } else {
            ipnumber.setTitle(getLocalIpAddress());
        }

        MenuItem version = menu.findItem(R.id.version);
        version.setTitle(APP_VERSION);

        TextView tvDomainTitle = (TextView) findViewById(R.id.tvDomainTitle);
        tvDomainTitle.setText(DetailActivity.BASE_URL_API_SYNC);

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        swServiceStatus = (Switch) menu.findItem(R.id.ms_service_status).getActionView().findViewById(R.id.switchForActionBar);
//        swServiceStatus.setChecked(false);
        if ("Active".equals(this.service_status))
            swServiceStatus.setChecked(true);

        TextView tvRestaurantTitle = (TextView) findViewById(R.id.tvRestaurantTitle);
        tvRestaurantTitle.setText(restourantn_title);

        MenuItem restourantnNo = menu.findItem(R.id.restourantn_no);
        restourantnNo.setTitle(this.restourantn_no);

        MenuItem openStatus = menu.findItem(R.id.open_status);
        openStatus.setIcon(R.drawable.ic_deactive);
        if ("Open".equals(this.open_status))
            openStatus.setIcon(R.drawable.ic_active);

        return true;
    }

    public static void setServiceStatus(String Status) {
        DetailActivity.httpRequestRestaurantServiceDeactive(Status);
    }

    public static String getLocalIpAddress() {
        StringBuilder IFCONFIG = new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        IFCONFIG.append(inetAddress.getHostAddress().toString() + "");
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("AmirHomeLog", ex.toString());
        }
        return (IFCONFIG.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.open_status:
                if (null != this.rCode) {
                    //getInfo();
                    Toast.makeText(this, R.string.status_refreshing, Toast.LENGTH_LONG).show();
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(this, "Don't Have imei ...", Toast.LENGTH_LONG).show();

                }

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void newOrderAlert() {
        countNewOrder++;
        if (1 == countNewOrder) {
            AlertDialog.Builder builderInner = new AlertDialog.Builder(MainActivity.this);
//            builderInner.setIcon(R.drawable.logo);
            builderInner.setMessage("NEUE BESTELLUNG EINGETROFFEN! ");
            builderInner.setTitle("# " + countNewOrder);
            builderInner.setCancelable(false);
            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopAlarm();
                    countNewOrder = 0;

                    Button btnNew = (Button) findViewById(R.id.btnNew);
                    btnNew.callOnClick();

                    dialog.dismiss();
                }
            });
            dialog = builderInner.create();
            dialog.show();
            startAlarm();
        } else {
            dialog.setTitle("# " + countNewOrder);
        }
    }


    //    public void detailOrder(String id) {
//        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//        intent.putExtra("id", id);
//        startActivity(intent);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.rCode = this.getDeviceImei();
                    this.afterPermission();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private String getDeviceImei() {

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = mTelephonyManager.getDeviceId();
        return deviceid;
    }

    @NonNull
    public static String getDiff(String dateStop) {
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
    }

}
