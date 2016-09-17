package com.aaaaaab.shopnavigator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private EditText editText;
    private ListView listView;
    private ArrayAdapter listViewAdapter;
    private ArrayList<Editable> list;
    private RequestQueue queue;

    String url ="https://api.siroop.ch/product/search/?query=";
    String limit = "&limit=10";
    String apiKey = "&apikey=f246b0749fa7484e81d387a471ad67aa";


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);

        //Create an EditText widget and add the watcher
        editText = (EditText) findViewById(R.id.text_search);

        list = new ArrayList<>();
        listViewAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(listViewAdapter);

        queue = Volley.newRequestQueue(this);
    }


    public void startScandit(View view) {
        // starts the intent that will use the Scandit API to add a product to the shopping list
        Intent intent = new Intent(this, ScanSuccessful.class);
        startActivity(intent);
    }

    public void sendRequest(View view) {
        listViewAdapter.clear();
        // assemble reqUrl to receive JSON obj
        String reqUrl = url + editText.getText() + limit + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, reqUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //parse response JSON obj
                        JSONArray jsonArray = null;
                        String name = null;
                        try {
                            jsonArray = new JSONArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                listViewAdapter.add(jsonArray.getJSONObject(i).getString("name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listViewAdapter.clear();
                listViewAdapter.add("Sorry, please try again.");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
