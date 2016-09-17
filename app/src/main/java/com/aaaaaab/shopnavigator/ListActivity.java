package com.aaaaaab.shopnavigator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements TextWatcher {
    private EditText editText;
    private ListView listView;
    private ArrayAdapter listViewAdapter;
    private ArrayList<Editable> list;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        //Create an EditText widget and add the watcher
        editText = (EditText) findViewById(R.id.text_search);
        editText.addTextChangedListener(this);

        list = new ArrayList<>();
        listViewAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(listViewAdapter);
    }


    public void startScandit(View view) {
        // starts the intent that will use the Scandit API to add a product to the shopping list
        Intent intent = new Intent(this, ScanSuccessful.class);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        CharSequence text = s;
        listViewAdapter.add(text);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
