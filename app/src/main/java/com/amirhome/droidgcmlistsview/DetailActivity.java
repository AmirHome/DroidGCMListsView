package com.amirhome.droidgcmlistsview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private void setStatusAction(String statusOrder, String statusDelivery) {
        Log.d("MainActivity", statusDelivery+" " + statusOrder);
        spinner = (Spinner) findViewById(R.id.spinnerStatus);

        listStatus = new ArrayList<String>();
        if (statusOrder.equals("0"))
        {
            Log.d("MainActivity", statusDelivery+" " + statusOrder);

            listStatus.add("Nothing");
            listStatus.add("Accept15");
            listStatus.add("Accept30");
            listStatus.add("Accept45");
            listStatus.add("Accept60");
            listStatus.add("Reject");

        }else if (statusOrder.equals("Reject")){
            listStatus.add("Rejected.");
        }else{//statusOrder is Accept
            if (statusDelivery.equals("0")){
                listStatus.add("Accepted");
                listStatus.add("reject_reason1");
                listStatus.add("reject_reason2");
                listStatus.add("reject_reason3");
            }else if (statusDelivery.equals("Accepted")){

            }else {

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

                switch(arg2) {

                    case 0 :
                        break;
                    case 1 :
                        Toast.makeText(DetailActivity.this, "21", Toast.LENGTH_SHORT).show();
                        break;
                    case 2 :
                        Toast.makeText(DetailActivity.this, "31", Toast.LENGTH_SHORT).show();
                        break;
                    case 3 :
                        Toast.makeText(DetailActivity.this, "41", Toast.LENGTH_SHORT).show();
                        break;
                    default :
                        //Toast.makeText(DetailActivity.this, "51", Toast.LENGTH_SHORT).show();
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
