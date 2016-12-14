package com.amirhome.droidgcmlistsview;

import android.Manifest;
import android.app.ActivityManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amirhome.droidgcmlistsview.Data.Cart;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;

    ///final static String DB_URL = "https://droidgcmlistsview.firebaseio.com/";
    final static String DB_URL = "https://eat2donatemap.firebaseio.com/";

    static String rCode;

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 777;
    private TelephonyManager mTelephonyManager;

    Firebase fire;
    ListView lv;
    ArrayList<String> names = new ArrayList<>();

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

//        lv = (ListView) findViewById(R.id.lv);
        Firebase.setAndroidContext(this);
        fire = new Firebase(DB_URL + rCode);


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
            public void onClick(View view, int position) {
                Order order = orderList.get(position);
                Toast.makeText(getApplicationContext(), order.getOrderNo() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        prepareOrderData();

        retrieveData();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//
//                for (ActivityManager.RunningAppProcessInfo service : manager.getRunningAppProcesses()) {
//                    Log.d("MainActivity" , service.processName);
//                }
//                String msg = "Can you help me please.." ;
//                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    private void prepareOrderData() {
        Order order = new Order("order number",  "15:05:54","1,245.5 LR","Reject");
        orderList.add(order);
//
//        order = new Order("1024",  "15:05:54","1,245.5 LR","Reject");
//        orderList.add(order);
//
//        order = new Order("1023",  "15:04:54","1,245.5 LR","Accept15");
//        orderList.add(order);
//
//        order = new Order("1022",  "15:03:34","1,245.5 LR","Accept45");
//        orderList.add(order);
//
//        order = new Order("1021",  "15:01:34","145.5 LR","Accept15");
//        orderList.add(order);
//
//        order = new Order("1020",  "15:00:34","15.5 LR","Reject");
//        orderList.add(order);



        mAdapter.notifyDataSetChanged();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Retrieve
    private void retrieveData() {



        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot, "A");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "onChildChanged " + dataSnapshot.getKey());

                //getUpdates(dataSnapshot, "C");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "onChildRemoved " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getUpdates(DataSnapshot ds, String status) {

        Cart cartDetails = ds.getValue(Cart.class);
        String orderId = ds.getKey();
//        String description = cartDetails.getDescription();
        names.add(orderId);

/*
        if (cartDetails.status_order.equals("0")) {
            Intent service = new Intent(getBaseContext(), ServiceOrderControl.class);
            service.putExtra("ServiceOrderControl.orderId", orderId);
            service.putExtra("ServiceOrderControl.order_date", cartDetails.order_date);
            Log.d("MainActivity", "onChildChanged " + orderId +" "+cartDetails.order_date);
            startService(service);

        }*/

        if (names.size() > 0) {
//            Toast.makeText(MainActivity.this, "salam", Toast.LENGTH_SHORT).show();
//            order = new Order( ds.getKey(),  "15:05:54","1,245.5 LR","Reject");

            Order order = new Order(  orderId,  cartDetails.order_date,cartDetails.order_cost,cartDetails.status_order);
            orderList.add(order);

           /* ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
            lv.setAdapter(adapter);

            itemClick();*/
        } else {
            Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    private void itemClick() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View viewClicked, int pos, long id) {
                TextView textView = (TextView) viewClicked;
//                String message = textView.getText().toString() + " " + pos;
//                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                detailOrder(textView.getText().toString());
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
