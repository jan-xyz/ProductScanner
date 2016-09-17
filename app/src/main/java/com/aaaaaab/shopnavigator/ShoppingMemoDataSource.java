package com.aaaaaab.shopnavigator;

/**
 * Created by paddy on 17/09/16.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class ShoppingMemoDataSource {

    private static final String LOG_TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;


    public ShoppingMemoDataSource(Context context) {
        Log.d(LOG_TAG, "The DataSource constructs now the dbHelper.");
        dbHelper = new ShoppingMemoDbHelper(context);
    }

    public void open() {
        Log.d(LOG_TAG, "A reference to the database has been requested.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "database reference received. Path to database is: " + database.getPath());
    }

    public void close() {
        dbHelper.close();
        Log.d(LOG_TAG, "Database was closed with DbHelper.");
    }
}
