package com.amirhome.droidgcmlistsview;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    public static final String ONAYLI_BEKLIYOR = "waiting for accept";
    public static final String TESLIM_BEKLIYOR = "Bestellung wird vorbereitet";
    public static final String REJECT_REASON1 = "Falsches Produkt geliefert";
    public static final String REJECT_REASON2 = "Produkt nicht hygienisch";
    public static final String REJECT_REASON3 = "Bestellung nicht vollständig";
    public static final String REJECT_REASON4 = "Lieferung zu spät";
    public static final String REJECT_REASON5 = "Lieferung wurde nicht geliefert";
    public static final String REJECT_REASON6 = "Person oder Adresse nicht gefunden";
    public static final String REJECT_REASON7 = "Keine Zahlung erhalten";

    public static final String CONST_REJECT_AUTO = "RejectAuto";
    public static final String CONST_REJECT = "Reject";
    public static final String CONST_DELIVERD = "Zugestellt";

    //    public static final String BASE_URL_API_SYNC = "https://beta.eat2donate.at/api/v1/";
    public static final String BASE_URL_API_SYNC = "https://beta.eat2donate.ga/api/v1/";
    public int btnID;
    RadioGroup rg_restaurant;
    RadioGroup rg_customer;
    Button btnAccept;
    Button btnReject;

    Firebase fire;

    final List<Order> dOrderList = new ArrayList<>();
    private RecyclerView dRecyclerView;
    private MenuAdapter dAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView strOrderNo = (TextView) findViewById(R.id.tvOrderNo);
        strOrderNo.setText("Order:  " + id);

        setTitle("Order:  " + id);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

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

//                Log.d("AmirHomeLog", "onDataChange " + dataSnapshot.getKey());
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

    static public void httpRequestSyncRestaurant(String param1) {

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
            tvStatusOrder.setText((String) ds.child("status_order").getValue());
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
}

