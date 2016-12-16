package com.amirhome.droidgcmlistsview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    Firebase fire;
    Spinner spinner;
    List<String> listStatus;

    final List<Order> orderList = new ArrayList<>();
    private RecyclerView recyclerView;
    private OrdersAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView strOrderNo = (TextView) findViewById(R.id.tvOrderNo);
        strOrderNo.setText("Order:  " + id);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new OrdersAdapter(orderList, R.layout.order_food);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareOrderData();

        Firebase.setAndroidContext(this);
        fire = new Firebase(MainActivity.DB_URL + MainActivity.rCode + "/" + id);

        this.retrieveData();


    }

    private void prepareOrderData() {
        Order order = new Order("menu_count", "Action & Adventure", "2015", "2015");
        orderList.add(order);

        order = new Order("Inside Out", "Animation, Kids & Family", "2015", "asdfasdf");
        orderList.add(order);
        /*Order cartDetails = dataSnapshot.getValue(Order.class);
        cartDetails.setOrderNo(dataSnapshot.getKey());
        cartDetails.setCost(cartDetails.order_cost);
        cartDetails.setOrderTime(cartDetails.order_date);
        cartDetails.setStatus(cartDetails.status_order, cartDetails.status_delivery);

        orderList.add(cartDetails);
        recyclerView.scrollToPosition(orderList.size() - 1);
        mAdapter.notifyItemInserted(orderList.size() - 1);*/
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
            if (statusOrder.equals("Reject")) {
                listStatus.add(statusOrder);
            } else {
                //statusOrder is Accept
                if (statusDelivery.equals("0")) {
                    listStatus.add(statusOrder);
                    listStatus.add("Accepted");
                    listStatus.add("reject_reason1");
                    listStatus.add("Reject_reason2");
                    listStatus.add("reject_reason3");
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

                if (arg2 != 0)
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
                        case "Accepted":
                        case "Reject_reason1":
                        case "Reject_reason2":
                        case "Reject_reason3":
                            Toast.makeText(DetailActivity.this, "set status_delivery, " + selectStatusAction, Toast.LENGTH_SHORT).show();
                            fire.child("status_delivery").setValue(selectStatusAction);
                            break;
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


        fire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot child: dataSnapshot.getChildren()) {
//                String newCondition = (String) dataSnapshot.child("title").getValue();
//                    Log.i("DetailActivity", (String) dataSnapshot.child("address").getValue());
                //}

                setStatusAction((String) dataSnapshot.child("status_order").getValue(), (String) dataSnapshot.child("status_delivery").getValue());
                getUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DetailActivity", "onCancelled", firebaseError.toException());
            }
        });


    }

    private void getUpdates(DataSnapshot ds) {

        TextView strCustomer = (TextView) findViewById(R.id.tvCustomer);
        strCustomer.setText((String) ds.child("customer").getValue());

        TextView tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDate);

        if ("0" == (String) ds.child("delivery_date").getValue()) {
            tvDeliveryDate.setText("Onayli Bekliyor");
        } else {
            tvDeliveryDate.setText((String) ds.child("delivery_date").getValue());
        }

        TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
        tvOrderDate.setText((String) ds.child("order_date").getValue());

        TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);
        tvStatusOrder.setText((String) ds.child("status_order").getValue());

        TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
        tvStatusDelivery.setText((String) ds.child("status_delivery").getValue());

        TextView tvOrderCost = (TextView) findViewById(R.id.tvOrderCost);
        tvOrderCost.setText((String) ds.child("order_cost").getValue());

        TextView tvPaymentType = (TextView) findViewById(R.id.tvPaymentType);
        tvPaymentType.setText((String) ds.child("payment_type").getValue());

        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAddress.setText((String) ds.child("address_full").getValue());

        TextView tvAddressDescription = (TextView) findViewById(R.id.tvAddressDescription);
        tvAddressDescription.setText((String) ds.child("address_description").getValue());

        TextView tvFoodDescriotion = (TextView) findViewById(R.id.tvFoodDescriotion);
        tvFoodDescriotion.setText((String) ds.child("description").getValue());


        TextView foo = (TextView) findViewById(R.id.tvFoodDescriotion);
        foo.setText(Html.fromHtml("teststs <b>Amir</b> atasgtd"));
    }
}
