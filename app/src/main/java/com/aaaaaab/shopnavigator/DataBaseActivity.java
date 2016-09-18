package com.aaaaaab.shopnavigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class DataBaseActivity extends Activity {

    private EditText editText;
    private ListView listView;
    private ArrayAdapter listViewAdapter;
    private ArrayList<Editable> list;
    private RequestQueue queue;
    private JSONArray jsonArray;

    String url ="https://api.siroop.ch/product/search/?query=";
    String limit = "&limit=3";
    String apiKey = "&apikey=f246b0749fa7484e81d387a471ad67aa";


    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();

    public static final String LOG_TAG = DataBaseActivity.class.getSimpleName();

    private ShoppingMemoDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        //Create an EditText widget and add the watcher
        editText = (EditText) findViewById(R.id.text_search);

        list = new ArrayList<>();
        listViewAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(listViewAdapter);

        queue = Volley.newRequestQueue(this);
        Log.d(LOG_TAG, "The database will be set up.");
        dataSource = new ShoppingMemoDataSource(this);


        initializeContextualActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "Database will be opened.");
        dataSource.open();

        Log.d(LOG_TAG, "The database contains the following items:");
        showAllListEntries();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(LOG_TAG, "Database will be closed.");
        dataSource.close();
    }

    private void showAllListEntries () {
        List<ShoppingMemo> shoppingMemoList = dataSource.getAllShoppingMemos();

        ArrayAdapter<ShoppingMemo> shoppingMemoArrayAdapter = new ArrayAdapter<> (
                this,
                android.R.layout.simple_list_item_multiple_choice,
                shoppingMemoList);

        ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setAdapter(shoppingMemoArrayAdapter);
    }

    public void addContent(View view) throws JSONException {
        dataSource.open();

        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        //final EditText editTextProduct = (EditText) findViewById(R.id.editText_product);

        String quantityString = editTextQuantity.getText().toString();
        String product = jsonArray.getJSONObject(0).getString("name");

        if(TextUtils.isEmpty(quantityString)) {
            editTextQuantity.setError(getString(R.string.editText_errorMessage));
            return;
        }
        if(TextUtils.isEmpty(product)) {
            //editTextProduct.setError(getString(R.string.editText_errorMessage));
            return;
        }

        int quantity = Integer.parseInt(quantityString);
        editTextQuantity.setText("");
        //editTextProduct.setText("");

        dataSource.createShoppingMemo(product, quantity);

        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        showAllListEntries();

        dataSource.close();
    }

    private void initializeContextualActionBar() {

        final ListView shoppingMemosListView = (ListView) findViewById(R.id.listview_shopping_memos);
        shoppingMemosListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        shoppingMemosListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_contextual_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                dataSource.open();
                switch (item.getItemId()) {

                    case R.id.cab_delete:
                        SparseBooleanArray touchedShoppingMemosPositions = shoppingMemosListView.getCheckedItemPositions();
                        for (int i=0; i < touchedShoppingMemosPositions.size(); i++) {
                            boolean isChecked = touchedShoppingMemosPositions.valueAt(i);
                            if(isChecked) {
                                int postitionInListView = touchedShoppingMemosPositions.keyAt(i);
                                ShoppingMemo shoppingMemo = (ShoppingMemo) shoppingMemosListView.getItemAtPosition(postitionInListView);
                                Log.d(LOG_TAG, "Position im ListView: " + postitionInListView + " Inhalt: " + shoppingMemo.toString());
                                dataSource.deleteShoppingMemo(shoppingMemo);
                            }
                        }
                        showAllListEntries();
                        mode.finish();
                        dataSource.close();
                        return true;

                    default:
                        dataSource.close();
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
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

    public void sendRequest(View view) {
        listViewAdapter.clear();
        // assemble reqUrl to receive JSON obj
        String reqUrl = url + editText.getText() + limit + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, reqUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //parse response JSON obj
                        jsonArray = null;
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

    public void startScandit(View view) {
        // starts the intent that will use the Scandit API to add a product to the shopping list
        Intent intent = new Intent(this, ScanSuccessful.class);
        startActivity(intent);
    }
}