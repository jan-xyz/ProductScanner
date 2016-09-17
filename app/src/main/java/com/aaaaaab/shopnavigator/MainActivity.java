package com.aaaaaab.shopnavigator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startScandit(View view) {
        Intent intent = new Intent(this, ScanSuccessful.class);
        startActivity(intent);

    }

    public void showList(View view) {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

}
