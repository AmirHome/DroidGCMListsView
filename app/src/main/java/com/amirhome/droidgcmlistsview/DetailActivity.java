package com.amirhome.droidgcmlistsview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        TextView displayNumber = (TextView) findViewById(R.id.textViewOrderNo);
        displayNumber.setText("Order:  " + id);
    }
}
