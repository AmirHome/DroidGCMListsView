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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    /*test*/
    RadioGroup rg;
    /*test end.*/

    Firebase fire;
    Spinner spinner;
    List<String> listStatus;

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

        Firebase.setAndroidContext(this);
        fire = new Firebase(MainActivity.DB_URL + MainActivity.rCode + "/" + id);

        this.retrieveData();


        /*test */






        showRadioButtonDialog();
        /*test end.*/

    }


    private void setStatusAction(String statusOrder, String statusDelivery) {
        spinner = (Spinner) findViewById(R.id.spinnerStatus);

        listStatus = new ArrayList<String>();
        if (statusOrder.equals("0")) {

            listStatus.add("Nothing");
            listStatus.add("Accept15");
            listStatus.add("Accept30");
            listStatus.add("Accept45");
            listStatus.add("Accept60");
            listStatus.add("Reject");

        } else {
            if (statusOrder.equals("Reject") || statusOrder.equals("RejectAuto")) {
                listStatus.add(statusOrder);
            } else {
                //statusOrder is Accept
                if (statusDelivery.equals("0")) {
                    listStatus.add(statusOrder);
                    listStatus.add("Delivered");
                    listStatus.add("Reject_reason1");
                    listStatus.add("Reject_reason2");
                    listStatus.add("Reject_reason3");
                } else {
                    listStatus.add(statusDelivery);
                }
            }
        }


        ArrayAdapter<String> adp = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, listStatus);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(adp);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String selectStatusAction = arg0.getItemAtPosition(arg2).toString();

                if (arg2 != 0) {
                    switch (selectStatusAction) {

                        case "Accept15":
                        case "Accept30":
                        case "Accept45":
                        case "Accept60":
                            Toast.makeText(DetailActivity.this, "set status order, " + selectStatusAction, Toast.LENGTH_SHORT).show();
                            fire.child("status_order").setValue(selectStatusAction);

                            break;
                        case "Reject":
                            Toast.makeText(DetailActivity.this, "set status order, " + selectStatusAction, Toast.LENGTH_SHORT).show();
                            fire.child("status_order").setValue(selectStatusAction);

                            break;
                        case "Delivered":
                        case "Reject_reason1":
                        case "Reject_reason2":
                        case "Reject_reason3":
                            Toast.makeText(DetailActivity.this, "set status_delivery, " + selectStatusAction, Toast.LENGTH_SHORT).show();
                            fire.child("status_delivery").setValue(selectStatusAction);
                            break;
                    }
                    httpRequest(fire.getKey());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

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


                setStatusAction((String) dataSnapshot.child("status_order").getValue(), (String) dataSnapshot.child("status_delivery").getValue());
                getUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DetailActivity", "onCancelled", firebaseError.toException());
            }
        });


    }

    static public void httpRequest(String param1) {

        String request_url = "http://192.168.1.109/eat2donate/api/v1/sync-cart-db/" + MainActivity.rCode;

        JSONObject parameters = new JSONObject();

        try {
            parameters.put("cart_id", param1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, request_url, parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String cart_id = response.getString("cart_id");
                            Log.d("AmirHomeLog", "onResponse " + cart_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("AmirHomeLog", error.toString());
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

        TextView tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDate);
        tvDeliveryDate.setText((String) ds.child("delivery_date").getValue());

        TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
        tvOrderDate.setText((String) ds.child("order_date").getValue());

        TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);
        if (ds.child("status_order").getValue().equals("0")) {
            tvStatusOrder.setText("Onayli Bekliyor");
        } else {
            tvStatusOrder.setText((String) ds.child("status_order").getValue());
        }

        TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
        String status_order = (String) ds.child("status_order").getValue();
        if (status_order.equals("Reject") || status_order.equals("RejectAuto")) {
            if (ds.child("status_delivery").getValue().equals("0")) {
                tvStatusDelivery.setText("");
            }
        } else {
            if (ds.child("status_delivery").getValue().equals("0")) {
                tvStatusDelivery.setText("Teslim Bekliyor");
            } else {
                tvStatusDelivery.setText((String) ds.child("status_delivery").getValue());
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

    }



    /*test*/
    private void showRadioButtonDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.detail_dialog);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Android custom dialog example!");
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

        List<String> stringList=new ArrayList<>();  // here is list
        for(int i=0;i<5;i++) {
            stringList.add("RadioButton " + (i + 1));
        }
        rg = (RadioGroup) dialog.findViewById(R.id.radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(DetailActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButtonOk.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = rg.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                Log.d("AmirHomeLog", String.valueOf(selectedId));
            }
        });
/*        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Log.d("AmirHomeLog", btn.getText().toString());
                    }
                }
            }
        });*/
        dialog.show();

    }


    /*test end.*/

}

