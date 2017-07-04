package com.amirhome.droidgcmlistsview;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class DetailActivity extends AppCompatActivity implements Runnable{
    public static final String ONAYLI_BEKLIYOR = "Warten auf Akzeptieren";
    public static final String TESLIM_BEKLIYOR = "Bestellung wird vorbereitet";
    public static final String REJECT_REASON1 = "Falsches Produkt geliefert";
    public static final String REJECT_REASON2 = "Produkt nicht hygienisch";
    public static final String REJECT_REASON3 = "Bestellung nicht vollständig";
    public static final String REJECT_REASON4 = "Lieferung zu spät";
    public static final String REJECT_REASON5 = "Lieferung wurde nicht geliefert";
    public static final String REJECT_REASON6 = "Person oder Adresse nicht gefunden";
    public static final String REJECT_REASON7 = "Keine Zahlung erhalten";

    public static final String CONST_REJECT_AUTO = "AutoAbgelehnt";
    public static final String CONST_REJECT = "Abgelehnt";
    public static final String CONST_DELIVERD = "Zugestellt";

    //                public static final String BASE_URL_API_SYNC = "https://beta.eat2donate.at/api/v1/";
//                public static final String BASE_URL_API_SYNC = "https://eat2donate.at/api/v1/";
//    public static final String BASE_URL_API_SYNC = "https://www.eat2donate.at/api/v1/";

        public static final String BASE_URL_API_SYNC = "https://beta.eat2donate.ga/api/v1/";
//    public static final String BASE_URL_API_SYNC = "http://test162.eat2donate.at/api/v1/";
    public int btnID;
    RadioGroup rg_restaurant;
    RadioGroup rg_customer;
    Button btnAccept;
    Button btnReject;

    Firebase fire;

    final List<Order> dOrderList = new ArrayList<>();
    private RecyclerView dRecyclerView;
    private MenuAdapter dAdapter;

    /* Bluetooth Config */
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    protected static final String TAG = "AmirHomeLog";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView strOrderNo = (TextView) findViewById(R.id.tvOrderNo);
        strOrderNo.setText(id);

        setTitle("Order:  " + id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(this);
        fire = new Firebase(MainActivity.DB_URL + MainActivity.rCode + "/" + id);

        this.retrieveData();

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnReject = (Button) findViewById(R.id.btnReject);

        // if button is clicked, close the custom dialog
        btnAccept.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnID = v.getId();
                showRadioButtonDialog();
            }
        });

        // if button is clicked, close the custom dialog
        btnReject.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnID = v.getId();
                showRadioButtonDialog();
            }
        });

        // float button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabPrint);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = "Can you help me please..";
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Thread t = new Thread() {
                    public void run() {
                        try {
                            OutputStream os = mBluetoothSocket
                                    .getOutputStream();
                            String BILL = "";

                            BILL = "                   XXXX MART    \n"
                                    + "                   XX.AA.BB.CC.     \n " +
                                    "                 NO 25 ABC ABCDE    \n" +
                                    "                  XXXXX YYYYYY      \n" +
                                    "                   MMM 590019091      \n";
                            BILL = BILL
                                    + "-----------------------------------------------\n";


                            BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel");
                            BILL = BILL + "\n";
                            BILL = BILL
                                    + "-----------------------------------------------";
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00");
                            BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00");

                            BILL = BILL
                                    + "\n-----------------------------------------------";
                            BILL = BILL + "\n\n ";

                            BILL = BILL + "                   Total Qty:" + "      " + "85" + "\n";
                            BILL = BILL + "                   Total Value:" + "     " + "700.00" + "\n";

                            BILL = BILL
                                    + "-----------------------------------------------\n";
                            BILL = BILL + "\n\n ";
                            os.write(BILL.getBytes());
                            //This is printer specific code you can comment ==== > Start

                            // Setting height
                            int gs = 29;
                            os.write(intToByteArray(gs));
                            int h = 104;
                            os.write(intToByteArray(h));
                            int n = 162;
                            os.write(intToByteArray(n));

                            // Setting Width
                            int gs_width = 29;
                            os.write(intToByteArray(gs_width));
                            int w = 119;
                            os.write(intToByteArray(w));
                            int n_width = 2;
                            os.write(intToByteArray(n_width));


                        } catch (Exception e) {
                            Log.e("MainActivity", "Exe ", e);
                        }
                    }
                };
                t.start();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                String msg = "Long Can you help me please..";
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter == null) {
                        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        Log.d(TAG,"adapter null");
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Log.d(TAG,"is not enable");
                            Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                        } else {
                            Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
                            Log.d(TAG,"is enable");
                            if (mPairedDevices.size() > 0) {
                                Log.d(TAG,"size > 0");

                                for (BluetoothDevice mDevice : mPairedDevices) {
                                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDevice.getAddress());
                                    Log.d(TAG, String.valueOf(mBluetoothDevice));
                                }
                                mBluetoothConnectProgressDialog = ProgressDialog.show(DetailActivity.this,
                                        "Connecting...", mBluetoothDevice.getName() + " : "
                                                + mBluetoothDevice.getAddress(), true, false);
                                Thread mBlutoothConnectThread = new Thread(DetailActivity.this);
                                mBlutoothConnectThread.start();
                            } else {
                                String mNoDevices = "None Paired";//getResources().getText(R.string.none_paired).toString();

                            }
                        }
                    }


                return true;
            }
        });
    }

    //Retrieve
    private void retrieveData() {

        dRecyclerView = (RecyclerView) findViewById(R.id.dRecyclerView);
        dAdapter = new MenuAdapter(dOrderList);
        dRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        dRecyclerView.setLayoutManager(mLayoutManager);
        dRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        dRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dRecyclerView.setAdapter(dAdapter);

        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    int size = dOrderList.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            dOrderList.remove(0);
                        }
                        dAdapter.notifyItemRangeRemoved(0, size);
                    }

                    for (DataSnapshot food : dataSnapshot.child("foods").getChildren()) {

                        Order cartDetails = dataSnapshot.getValue(Order.class);
                        cartDetails.setOrderNo(dataSnapshot.getKey());
                        cartDetails.setCost(cartDetails.order_cost);
                        cartDetails.setOrderTime(cartDetails.order_date);
                        cartDetails.setStatus(cartDetails.status_order, cartDetails.status_delivery);

                        cartDetails.setFood(food);
                        dOrderList.add(cartDetails);
                        dRecyclerView.scrollToPosition(dOrderList.size() - 1);
                        dAdapter.notifyItemInserted(dOrderList.size() - 1);

                    }

                    getUpdates(dataSnapshot);
                } catch (Exception ex) {
                    Log.e("AmirHomeLog", ex.getMessage());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DetailActivity", "onCancelled", firebaseError.toException());
            }
        });
    }

    static public void httpRequestRestaurantServiceDeactive(String param1) {

        String request_url = BASE_URL_API_SYNC + "sync-restaurant-db/" + MainActivity.rCode;
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("token", param1);
            parameters.put("restourantn_no", MainActivity.restourantn_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, request_url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            MainActivity.restourantn_no = response.getJSONObject("data").getString("id");
                            MainActivity.restourantn_title = response.getJSONObject("data").getString("title");
                            MainActivity.open_status = response.getJSONObject("data").getString("open_status");
                            MainActivity.service_status = response.getJSONObject("data").getString("service_status");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AmirHomeLog", error.toString());
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        App.getInstance().addToRequestQueue(request);

    }

    static public void httpRequestSyncRestaurant(final String param1, final VolleyCallback callback) {

        String request_url = BASE_URL_API_SYNC + "sync-restaurant-db/" + MainActivity.rCode;
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("token", param1);
            parameters.put("restourantn_no", MainActivity.restourantn_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, request_url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccessResponse(response);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        App.getInstance().addToRequestQueue(request);

    }

    static public void httpRequestSyncCart(String param1) {

        String request_url = BASE_URL_API_SYNC + "sync-cart-db/" + MainActivity.rCode;

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("cart_id", param1);
            parameters.put("restourantn_no", MainActivity.restourantn_no);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, request_url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String cart_id = response.getString("cart_id");
//                            Log.d("AmirHomeLog", "onResponse " + cart_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AmirHomeLog", error.toString());
            }
        }) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                4000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        App.getInstance().addToRequestQueue(request);

    }

    private void getUpdates(DataSnapshot ds) {

        TextView strCustomer = (TextView) findViewById(R.id.tvCustomer);
        strCustomer.setText((String) ds.child("customer").getValue());

        TextView strTelephone = (TextView) findViewById(R.id.tvtelephone);
        strTelephone.setText((String) ds.child("phone").getValue());

        TextView tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDate);
        tvDeliveryDate.setText((String) ds.child("delivery_date").getValue());
        if (tvDeliveryDate.getText().toString().equals("0")) {
            tvDeliveryDate.setText("");
        }

        TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
        tvOrderDate.setText((String) ds.child("order_date").getValue());

        btnAccept.setEnabled(false);
        btnReject.setEnabled(false);
        btnAccept.setText("akzeptieren");
        btnReject.setText("ABLEHNEN");

        TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
        String status_order = (String) ds.child("status_order").getValue();

        if (status_order.equals("0") || status_order.equals("Reject") || status_order.equals("RejectAuto")) {
            btnAccept.setText("akzeptieren");
            btnReject.setText("ABLEHNEN");
            tvStatusDelivery.setText("");
        } else {
            btnAccept.setText("GELIEFERT");
            btnReject.setText("ABGELEHNT DURCH KUNDEN");
            switch ((String) ds.child("status_delivery").getValue()) {
                case "0":
                    tvStatusDelivery.setText(TESLIM_BEKLIYOR);
                    btnAccept.setEnabled(true);
                    btnReject.setEnabled(true);
                    break;
                case "Reject_reason1":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON1);
                    break;
                case "Reject_reason2":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON2);
                    break;
                case "Reject_reason3":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON3);
                    break;
                case "Reject_reason4":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON4);
                    break;
                case "Reject_reason5":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON5);
                    break;
                case "Reject_reason6":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON6);
                    break;
                case "Reject_reason7":
                    tvStatusDelivery.setText(DetailActivity.REJECT_REASON7);
                    break;
                case "Delivered":
                    tvStatusDelivery.setText(DetailActivity.CONST_DELIVERD);
                    break;
            }
        }

        TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);
        if (ds.child("status_order").getValue().equals("0")) {
            tvStatusOrder.setText(ONAYLI_BEKLIYOR);
            btnAccept.setEnabled(true);
            btnReject.setEnabled(true);
        } else {
            switch (status_order) {
                case "RejectAuto":
                    tvStatusOrder.setText(CONST_REJECT_AUTO);
                    break;
                case "Reject":
                    tvStatusOrder.setText(CONST_REJECT);
                    break;
                case "Accept15":
                    tvStatusOrder.setText("Lieferung in 15 Min.");
                    break;
                case "Accept30":
                    tvStatusOrder.setText("Lieferung in 30 Min.");
                    break;
                case "Accept45":
                    tvStatusOrder.setText("Lieferung in 45 Min.");
                    break;
                case "Accept60":
                    tvStatusOrder.setText("Lieferung in 60 Min.");
                    break;
                default:
                    tvStatusOrder.setText((String) ds.child("status_order").getValue());
                    break;
            }
        }


        TextView tvOrderCost = (TextView) findViewById(R.id.tvOrderCost);
        tvOrderCost.setText((String) ds.child("order_cost").getValue());

        TextView tvPaymentType = (TextView) findViewById(R.id.tvPaymentType);
        tvPaymentType.setText((String) ds.child("payment_type").getValue());

        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setText((String) ds.child("address_full").getValue());

        TextView tvAddressDescription = (TextView) findViewById(R.id.tvAddressDescription);
        tvAddressDescription.setText((String) ds.child("address_description").getValue());

        TextView descriptionOrder = (TextView) findViewById(R.id.descriptionOrder);
        descriptionOrder.setText((String) ds.child("description").getValue());

        TextView tvOrderIncome = (TextView) findViewById(R.id.tvOrderIncome);
        tvOrderIncome.setText((String) ds.child("restaurant_order_income").getValue());

        TextView tvTotalDonate = (TextView) findViewById(R.id.tvTotalDonate);
        tvTotalDonate.setText((String) ds.child("total_donate").getValue());

    }

    private void showRadioButtonDialog() {
        final Dialog dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.detail_dialog);
        dialog.setTitle("Title...");

        // set the custom dialog components, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.logo);

        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        dialogButtonCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButtonOk.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {

                int selectedRadioButtonIdR = rg_restaurant.getCheckedRadioButtonId();
                int selectedRadioButtonIdC = rg_customer.getCheckedRadioButtonId();

                TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
                TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);

                switch (selectedRadioButtonIdR) {
                    case R.id.Accept15:
                        fire.child("status_order").setValue("Accept15");
                        break;
                    case R.id.Accept30:
                        fire.child("status_order").setValue("Accept30");
                        break;
                    case R.id.Accept45:
                        fire.child("status_order").setValue("Accept45");
                        break;
                    case R.id.Accept60:
                        fire.child("status_order").setValue("Accept60");
                        break;
                    default:
                        if (R.id.btnReject == btnID && tvStatusOrder.getText().equals(ONAYLI_BEKLIYOR)) {
                            fire.child("status_order").setValue("Reject");
                        }
                        break;
                }
                switch (selectedRadioButtonIdC) {
                    case R.id.Reject_reason1:
                        fire.child("status_delivery").setValue("Reject_reason1");
                        break;
                    case R.id.Reject_reason2:
                        fire.child("status_delivery").setValue("Reject_reason2");
                        break;
                    case R.id.Reject_reason3:
                        fire.child("status_delivery").setValue("Reject_reason3");
                        break;
                    case R.id.Reject_reason4:
                        fire.child("status_delivery").setValue("Reject_reason4");
                        break;
                    case R.id.Reject_reason5:
                        fire.child("status_delivery").setValue("Reject_reason5");
                        break;
                    case R.id.Reject_reason6:
                        fire.child("status_delivery").setValue("Reject_reason6");
                        break;
                    case R.id.Reject_reason7:
                        fire.child("status_delivery").setValue("Reject_reason7");
                        break;
                    default:
                        if (R.id.btnAccept == btnID && tvStatusDelivery.getText().equals(TESLIM_BEKLIYOR) && !tvStatusOrder.getText().equals(ONAYLI_BEKLIYOR)) {
                            fire.child("status_delivery").setValue("Delivered");
                            SimpleDateFormat sdfDateTime = new SimpleDateFormat(MainActivity.DateTimeFormat, Locale.US);
                            String currentTime = sdfDateTime.format(new Date(System.currentTimeMillis()));
                            fire.child("delivery_date").setValue(currentTime);
                        }
                        break;
                }
                dialog.dismiss();
                httpRequestSyncCart(fire.getKey());

            }
        });

        TextView textWarning = (TextView) dialog.findViewById(R.id.textWarning);
        rg_restaurant = (RadioGroup) dialog.findViewById(R.id.radio_group_restaurant);
        rg_customer = (RadioGroup) dialog.findViewById(R.id.radio_group_customer);

        TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
        TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);

        textWarning.setVisibility(View.INVISIBLE);
        rg_restaurant.setVisibility(View.INVISIBLE);
        rg_customer.setVisibility(View.INVISIBLE);

        // get selected radio button from radioGroup

        switch (btnID) {
            case R.id.btnAccept:
                if (tvStatusOrder.getText().equals(ONAYLI_BEKLIYOR)) {
                    rg_restaurant.setVisibility(View.VISIBLE);
                } else if (tvStatusDelivery.getText().equals(TESLIM_BEKLIYOR)) {

                    textWarning.setVisibility(View.VISIBLE);
                    rg_customer.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.btnReject:
                if (tvStatusOrder.getText().equals(ONAYLI_BEKLIYOR)) {
                    textWarning.setVisibility(View.VISIBLE);
                } else if (tvStatusDelivery.getText().equals(TESLIM_BEKLIYOR)) {
                    textWarning.setVisibility(View.INVISIBLE);
                    rg_customer.setVisibility(View.VISIBLE);
                }
                break;
        }

        dialog.show();

    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);
Log.d(TAG, "onActivityResult");
        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);

                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
//                    Intent connectIntent = new Intent(MainActivity.this, DeviceListActivity.class);
//                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
//                    Log.d(TAG,"DeviceListActivity.class");
                } else {
                    Toast.makeText(DetailActivity.this, "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }


    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            Log.d(TAG, "connect");
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }
    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(DetailActivity.this, "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }
}

