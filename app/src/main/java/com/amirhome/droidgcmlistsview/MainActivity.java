package com.amirhome.droidgcmlistsview;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


public class MainActivity extends AppCompatActivity {
    //    final List<Order> orderList = new ArrayList<>();
//    private RecyclerView recyclerView;
//    private OrdersAdapter mAdapter;
    final static String DB_URL = "https://eat2donatemap.firebaseio.com/";
    static MediaPlayer mPlayer;
    static String rCode;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 777;
    private TelephonyManager mTelephonyManager;

    Firebase fire;
    /* Dailog */
    private int countNewOrder = 0;
    private Dialog dialog;

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

        //get and set imei code = restaurant code
        this.setImeiCode();


        //init firebase
        Firebase.setAndroidContext(this);
        fire = new Firebase(DB_URL + rCode);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        //SET ITS PROPETRIES
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
                        players.add(0,p);
                        rv.setAdapter(adapter);
                        adapter.getFilter().filter("");

//                        rv.scrollToPosition(players.size() - 1);
//                        adapter.notifyItemInserted(players.size() - 1);

                        if (dataSnapshot.child("status_order").getValue().toString().equals("0")) {
                            Intent myService = new Intent(getBaseContext(), ServiceOrderControl.class);
                            myService.putExtra("ServiceOrderControl.orderId", p.getOrder_no());
                            myService.putExtra("ServiceOrderControl.order_date", p.getOrder_date());

                            startService(myService);
                            newOrderAlert();


                        }

                    } catch (Exception ex) {
                        Log.d("AmirHomeLog", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
                    adapter.getFilter().filter("");

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
                adapter.getFilter().filter("");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnAllFilter.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //FILTER AS YOU TYPE
                adapter.getFilter().filter("Reject");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnNew.setBackgroundResource(R.color.colorPrimaryDark);
            }
        });
        btnDeliveryWating.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                adapter.getFilter().filter("btnDeliveryWating");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);

                btnDeliveryWating.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnPenalty.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                adapter.getFilter().filter("btnPenalty");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnPenalty.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnRejected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                adapter.getFilter().filter("btnRejected");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnRejected.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                adapter.getFilter().filter("btnDelivered");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnDelivered.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });
        btnCustomerRejected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                adapter.getFilter().filter("btnCustomerRejected");
                btnBackColorReset(btnAllFilter, btnDelivered, btnNew, btnDeliveryWating, btnPenalty, btnRejected, btnCustomerRejected);
                btnCustomerRejected.setBackgroundResource(R.color.colorPrimaryDark);

            }
        });


        // float button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/* test */
//                p=new Player();
//                p.setName("Ander Herera");
//                p.setPos("Midfielder");
//                players.add(p);
//                final MyAdapter adapter=new MyAdapter(MainActivity.this,players);
                //rv.setAdapter(adapter);
/* test end. */
//                String msg = "Can you help me please..";
//                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    public void startAlarm() {
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.bleepsoundbible);
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


    private void setImeiCode() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            this.rCode = this.getDeviceImei();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem imeinumber = menu.findItem(R.id.imeinumber);
        imeinumber.setTitle(this.rCode);

        MenuItem ipnumber = menu.findItem(R.id.ipnumber);
        ipnumber.setTitle(getLocalIpAddress());

        MenuItem version = menu.findItem(R.id.version);
        version.setTitle("0.0.2.09");

        MenuItem restourantn_no = menu.findItem(R.id.restourantn_no);
        restourantn_no.setTitle("2");
        return true;
    }

    public String getLocalIpAddress() {
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
            Log.e("LOG_TAG", ex.toString());
        }
        return (IFCONFIG.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.service_status:
                // User chose the "Settings" item, show the app settings UI...
                Log.d("AmirHomeLog", "service_status");
                return true;

            case R.id.open_status:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Log.d("AmirHomeLog", "open_status");

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
            builderInner.setIcon(R.drawable.logo);
            builderInner.setMessage("New order come now..");
            builderInner.setTitle("Please click OK and go to the list. Count: " + countNewOrder);
            builderInner.setCancelable(false);
            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopAlarm();
                    countNewOrder = 0;
                    dialog.dismiss();
                }
            });
            dialog = builderInner.create();
            dialog.show();
            startAlarm();
        } else {
            dialog.setTitle("Please click OK and go to the list. Count: " + countNewOrder);
        }
    }


    public void detailOrder(String id) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.getDeviceImei();
        }
    }

    private String getDeviceImei() {

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = mTelephonyManager.getDeviceId();
        return deviceid;
    }

}
