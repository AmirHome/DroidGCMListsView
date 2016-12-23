package com.amirhome.droidgcmlistsview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;

    final static String DB_URL = "https://eat2donatemap.firebaseio.com/";

    static String rCode;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 777;
    private TelephonyManager mTelephonyManager;

    Firebase fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    /*  getSupportActionBar().setDisplayUseLogoEnabled(true);
       getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setLogo(R.drawable.ic_action_name2);*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get and set imei code = restaurant code
        this.setImeiCode();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new OrdersAdapter(orderList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View viewClicked, int position) {
                Order order = orderList.get(position);
                detailOrder(order.getOrderNo());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        Firebase.setAndroidContext(this);
        fire = new Firebase(DB_URL + rCode);

        retrieveData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

                for (ActivityManager.RunningAppProcessInfo service : manager.getRunningAppProcesses()) {
                    Log.d("MainActivity" , service.processName);
                }*/

//                var messageListRef = new Firebase('https://samplechat.firebaseio-demo.com/message_list');
//                var newMessageRef = fire.push();


//                Firebase alanRef = fire.child("users").child("alanisawesome");
//                alanRef.child("fullName").setValue("Alan Turing");

//                httpRequest("23");

                String msg = "Can you help me please..";
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        imeinumber.setTitle( this.rCode);

        MenuItem ipnumber = menu.findItem(R.id.ipnumber);
//        getLocalIpAddress()
        ipnumber.setTitle( getLocalIpAddress() );

        MenuItem version = menu.findItem(R.id.version);
        version.setTitle( "0.0.2.09");

        MenuItem restourantn_no = menu.findItem(R.id.restourantn_no);
        restourantn_no.setTitle( "2");
        return true;
    }
    public String getLocalIpAddress() {
        StringBuilder IFCONFIG=new StringBuilder();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        IFCONFIG.append(inetAddress.getHostAddress().toString()+"");
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

    //Retrieve
    private void retrieveData() {

        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {
                        Order cartDetails = dataSnapshot.getValue(Order.class);
                        cartDetails.setOrderNo(dataSnapshot.getKey());
                        cartDetails.setCost(cartDetails.order_cost);
                        cartDetails.setOrderTime(cartDetails.order_date);
                        cartDetails.setStatus(cartDetails.status_order, cartDetails.status_delivery);

                        orderList.add(cartDetails);
                        recyclerView.scrollToPosition(orderList.size() - 1);
                        mAdapter.notifyItemInserted(orderList.size() - 1);
//                        Log.d("AmirHomeLog", "onChildAdded"+ orderList.size());

//                        mAdapter.notifyDataSetChanged();

                        if (cartDetails.status_order.equals("0")) {
                            Intent service = new Intent(getBaseContext(), ServiceOrderControl.class);
                            service.putExtra("ServiceOrderControl.orderId", dataSnapshot.getKey());
                            service.putExtra("ServiceOrderControl.order_date", cartDetails.order_date);
//                            Log.d("AmirHomeLog", "onChildChanged " + dataSnapshot.getKey() + " " + cartDetails.order_date);
                            startService(service);
                        }

                    } catch (Exception ex) {
                        Log.d("AmirHomeLog", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.d("AmirHomeLog", "onChildChanged getOrderNo=" + dataSnapshot.getKey());


                int pos = -1;
                for (int i = 0; i < orderList.size(); i++) {
                    orderList.get(i).getOrderNo();
                    if (dataSnapshot.getKey().equals(orderList.get(i).getOrderNo())) {
                        pos = i;
                        break;
                    }

                }
//                Log.d("AmirHomeLog", "position " + pos);
                if (pos >= 0) {

                    orderList.remove(pos);
                    mAdapter.notifyItemRemoved(pos);
                    mAdapter.notifyItemRangeChanged(pos, orderList.size());

                    Order cartDetails = dataSnapshot.getValue(Order.class);
                    cartDetails.setOrderNo(dataSnapshot.getKey());
                    cartDetails.setCost(cartDetails.order_cost);
                    cartDetails.setOrderTime(cartDetails.order_date);
                    cartDetails.setStatus(cartDetails.status_order, cartDetails.status_delivery);

                    orderList.add(pos, cartDetails);
                    mAdapter.notifyItemInserted(pos);
                    mAdapter.notifyItemRangeChanged(pos, orderList.size());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d("AmirHomeLog", "onChildRemoved " + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    private void detailOrder(String id) {
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
