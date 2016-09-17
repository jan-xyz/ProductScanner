package com.aaaaaab.shopnavigator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DataBaseActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_base);
//    }

    public static final String LOG_TAG = DataBaseActivity.class.getSimpleName();

    private ShoppingMemoDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

//        ShoppingMemo testMemo = new ShoppingMemo("Apples", 5, 102);
//        Log.d(LOG_TAG, "Content of the test memo: " + testMemo.toString());
//
//        dataSource = new ShoppingMemoDataSource(this);

//        Log.d(LOG_TAG, "The data source is being opened.");
//        dataSource.open();
//
//        ShoppingMemo shoppingMemo = dataSource.createShoppingMemo("Test products", 2);
//        Log.d(LOG_TAG, "The following entry has been written to the database:");
//        Log.d(LOG_TAG, "ID: " + shoppingMemo.getId() + ", Content: " + shoppingMemo.toString());
//
//        Log.d(LOG_TAG, "The following entries are in the database:");
//        showAllListEntries();
//
//        Log.d(LOG_TAG, "The data source is being closed.");
//        dataSource.close();
        Log.d(LOG_TAG, "The database will be set up.");
        dataSource = new ShoppingMemoDataSource(this);

        activateAddButton();

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

    public void addContent(View view) {
        dataSource.open();

        ShoppingMemo shoppingMemo = dataSource.createShoppingMemo("Test products", 2);
        Log.d(LOG_TAG, "The following entry has been written to the database:");
        Log.d(LOG_TAG, "ID: " + shoppingMemo.getId() + ", Content: " + shoppingMemo.toString());

        Log.d(LOG_TAG, "The following entries are in the database:");
        showAllListEntries();

        Log.d(LOG_TAG, "The data source is being closed.");
        dataSource.close();
    }

    private void activateAddButton() {
        Button buttonAddProduct = (Button) findViewById(R.id.button_add_product);
        final EditText editTextQuantity = (EditText) findViewById(R.id.editText_quantity);
        final EditText editTextProduct = (EditText) findViewById(R.id.editText_product);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String quantityString = editTextQuantity.getText().toString();
                String product = editTextProduct.getText().toString();

                if(TextUtils.isEmpty(quantityString)) {
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    return;
                }
                if(TextUtils.isEmpty(product)) {
                    editTextProduct.setError(getString(R.string.editText_errorMessage));
                    return;
                }

                int quantity = Integer.parseInt(quantityString);
                editTextQuantity.setText("");
                editTextProduct.setText("");

                dataSource.createShoppingMemo(product, quantity);

                InputMethodManager inputMethodManager;
                inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                showAllListEntries();
            }
        });

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
                        return true;

                    default:
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
}