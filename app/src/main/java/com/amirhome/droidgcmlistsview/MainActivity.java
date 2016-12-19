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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


                Firebase alanRef = fire.child("users").child("alanisawesome");

                alanRef.child("fullName").setValue("Alan Turing");



                String msg = "Can you help me please..";
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void http_request(String param1) {

        String request_url = "http://www.google.com/";

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("param1" ,param1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, request_url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String message = response.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyError",error.toString());
            }
        }) {
/*            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjQ3LCJpc3MiOiJodHRwOlwvXC9lYXQyZG9uYXRlMy50a1wvYXBpXC9hdXRoXC9sb2dpbiIsImlhdCI6MTQ4MjE2MDQwMywiZXhwIjoxNDgzMDI0NDAzLCJuYmYiOjE0ODIxNjA0MDMsImp0aSI6ImUyNTA2MjAzZGU0NzI3MTI0ZTE3MDk4NzRiMzMyYTc5In0.rORWETjYea5FvmP50tHvs-QN_ElLlwGplmezieB6f30");
                return params;
            }*/

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.i("VolleyError",mStatusCode + "");
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        App.getInstance().addToRequestQueue(request);

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

//                        mAdapter.notifyDataSetChanged();

                        if (cartDetails.status_order.equals("0")) {
                            Intent service = new Intent(getBaseContext(), ServiceOrderControl.class);
                            service.putExtra("ServiceOrderControl.orderId", dataSnapshot.getKey());
                            service.putExtra("ServiceOrderControl.order_date", cartDetails.order_date);
//                            Log.d("MainActivity", "onChildChanged " + dataSnapshot.getKey() + " " + cartDetails.order_date);
                            startService(service);
                        }

                    } catch (Exception ex) {
                        Log.d("MainActivity", ex.getMessage());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("MainActivity", "onChildChanged getOrderNo=" + dataSnapshot.getKey());
                int pos = -1;
                for (int i = 0; i < orderList.size(); i++) {
                    orderList.get(i).getOrderNo();
                    if (dataSnapshot.getKey().equals(orderList.get(i).getOrderNo())) {
                        pos = i;
                        break;
                    }

                }
                Log.d("MainActivity", "position " + pos);
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
