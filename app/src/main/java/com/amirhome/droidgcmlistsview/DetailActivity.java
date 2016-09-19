package com.amirhome.droidgcmlistsview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    Firebase fire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView strOrderNo = (TextView) findViewById(R.id.tvOrderNo);
        strOrderNo.setText("Order:  " + id);

        Firebase.setAndroidContext(this);

        fire = new Firebase(MainActivity.DB_URL + "imei0000012/" + id);

        this.retrieveData();
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
                getUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("DetailActivity", "onCancelled", firebaseError.toException());
            }
        });


/*        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
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
        });*/
    }

    private void getUpdates(DataSnapshot ds) {


        TextView strAddress = (TextView) findViewById(R.id.tvAddress);
        strAddress.setText((String) ds.child("address").getValue());

        TextView strCustomer = (TextView) findViewById(R.id.tvCustomer);
        strCustomer.setText((String) ds.child("customer").getValue());

        TextView tvDeliveryDate = (TextView) findViewById(R.id.tvDeliveryDate);
        tvDeliveryDate.setText((String) ds.child("delivery_date").getValue());

        TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
        tvOrderDate.setText((String) ds.child("order_date").getValue());

        TextView tvStatusOrder = (TextView) findViewById(R.id.tvStatusOrder);
        tvStatusOrder.setText((String) ds.child("status_order").getValue());

        TextView tvStatusDelivery = (TextView) findViewById(R.id.tvStatusDelivery);
        tvStatusDelivery.setText((String) ds.child("status_delivery").getValue());

        TextView strDescription = (TextView) findViewById(R.id.tvDescription);
        strDescription.setText((String) ds.child("description").getValue());

    }
}
