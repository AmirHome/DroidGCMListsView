package com.amirhome.droidgcmlistsview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static String DB_URL = "https://droidgcmlistsview.firebaseio.com/";

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get and set imei code = restaurant code
        this.SetImeiCode();

        lv = (ListView) findViewById(R.id.lv);
        Firebase.setAndroidContext(this);
//        fire = new Firebase(DB_URL);
        fire = new Firebase(DB_URL + rCode);
        names.clear();
        this.retrieveData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = "Can you help me please..";
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void SetImeiCode() {
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

    private void addData(String name, String desc) {
        Cart c = new Cart();
        c.setName(name);
        c.setDescription(desc);

        fire.child("Cart").push().setValue(c);
    }

    //Retrieve
    private void retrieveData() {


        fire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
/*                names.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Cart c = new Cart();

                    c.setName(data.getKey());
                    names.add(c.getName());

                }
                if (names.size() > 0) {
                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
                    lv.setAdapter(adapter);

                    itemClick();
                } else {
                    Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }*/
//                getUpdates(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("MainActivity", "onCancelled", firebaseError.toException());
            }
        });


        fire.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
                Log.d("MainActivity","onChildAdded-getKey " + dataSnapshot.getKey());

//                Cart test = dataSnapshot.getValue(Cart.class);
                //Log.d("MainActivity","onChildAdded " + test.title);

//                names.clear();

                //for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Cart c = new Cart();

                    c.setName(dataSnapshot.getKey());
                    names.add(c.getName());

                //}
                if (names.size() > 0) {
                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
                    lv.setAdapter(adapter);

                    itemClick();
                } else {
                    Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                getUpdates(dataSnapshot);
//                Log.d("MainActivity","onChildChanged " + dataSnapshot.getKey());
                //Log.d("MainActivity","onChildChanged s " + s);
            }


            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //Log.d("MainActivity","onChildAdded " + dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void getUpdates(DataSnapshot ds) {
        names.clear();

        for (DataSnapshot data : ds.getChildren()) {
            Cart c = new Cart();

//            c.setName(data.getValue(Cart.class).getName());
            c.setName(data.getKey());
            names.add(c.getName());

        }
        if (names.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, names);
            lv.setAdapter(adapter);

            itemClick();
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

    private void detailOrder(String  id) {
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
        deviceid = "imei0000012";
        return deviceid;
    }

}
