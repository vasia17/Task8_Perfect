package com.project.vasia.quotations;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {
    final Uri THEMES_URI = Uri.parse("content://ua.vasia.Quotations/themes");
    final Uri QUOTES_URI = Uri.parse("content://ua.vasia.Quotations/quotes");
    final Uri AUTHORS_URI = Uri.parse("content://ua.vasia.Quotations/authors");
    final Uri FAVOURITES_URI = Uri.parse("content://ua.vasia.Quotations/favourites");
    private ProgressDialog pDialog;
    // URL to get contacts JSON
    private static String url = "https://quote-collection.appspot.com/_ah/api/themeApi/v1/theme";
    // contacts JSONArray
    JSONArray themes = null;
    JSONArray quotes = null;
    JSONArray authors = null;
    // JSON Arrays names
    private static final String TAG_THEMES = "themes";
    private static final String TAG_QUOTES = "quotes";
    private static final String TAG_AUTHORS = "authors";
    // JSON Parameters names
    private static final String TAG_THEMES_ID = "id";
    private static final String TAG_THEMES_NAME = "name";
    private static final String TAG_THEMES_TIME = "udateTime";
    private static final String TAG_QUOTES_ID = "id";
    private static final String TAG_QUOTES_NAME = "quote";
    private static final String TAG_QUOTES_THEME = "themeID";
    private static final String TAG_QUOTES_TIME = "updateTimeStamp";
    private static final String TAG_QUOTES_AUTHOR = "authorID";
    private static final String TAG_AUTHORS_ID = "id";
    private static final String TAG_AUTHORS_NAME = "name";
    private static final String TAG_AUTHORS_TIME = "updateTime";

    private static ListView lvContact;
    private static String NEED_TABLE;
    private static Uri NEED_URI;
    private static String selection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NEED_TABLE = TAG_THEMES;
        NEED_URI = THEMES_URI;

        final Cursor cursor = getContentResolver().query(THEMES_URI, null, null,
                null, null);
        startManagingCursor(cursor);
        String from[] = { "name" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);

        lvContact = (ListView) findViewById(R.id.themes_list);
        lvContact.setAdapter(adapter);

            lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (NEED_URI == THEMES_URI) {
                        String item = ((TextView) view).getText().toString();
                        int count = position;
                        Log.e("ITEM_SELECTED", item);
                        Cursor crs = getContentResolver().query(NEED_URI,
                                new String[]{"_id"},
                                null,
                                null,
                                null);
                        startManagingCursor(crs);
                        ArrayList<String> arrcurval = new ArrayList<>();
                        if (crs.moveToFirst()) {
                            do {
                                arrcurval.add(crs.getString(0)); //<< pass column index here instead of i

                            } while (crs.moveToNext());
                        }
                        Log.e("ITEM_SELECTED", arrcurval.get(count));
                        crs = getContentResolver().query(QUOTES_URI,
                                null,
                                "theme_id = " + arrcurval.get(count),
                                null,
                                null);
                        startManagingCursor(crs);
                        selection = "theme_id = " + arrcurval.get(count);
                        String from[] = {"quotes"};
                        int to[] = {android.R.id.text1};
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
                                android.R.layout.simple_list_item_1, crs, from, to);
                        lvContact.setAdapter(adapter);
                        NEED_URI = QUOTES_URI;
                    }else if(NEED_URI == AUTHORS_URI){
                        String item = ((TextView) view).getText().toString();
                        int count = position;
                        Log.e("ITEM_SELECTED", item);
                        Cursor crs = getContentResolver().query(NEED_URI,
                                new String[]{"_id"},
                                null,
                                null,
                                null);
                        startManagingCursor(crs);
                        ArrayList<String> arrcurval = new ArrayList<>();
                        if (crs.moveToFirst()) {
                            do {
                                arrcurval.add(crs.getString(0)); //<< pass column index here instead of i

                            } while (crs.moveToNext());
                        }
                        Log.e("ITEM_SELECTED", arrcurval.get(count));
                        crs = getContentResolver().query(QUOTES_URI,
                                null,
                                "author_id = " + arrcurval.get(count),
                                null,
                                null);
                        startManagingCursor(crs);
                        selection = "author_id = " + arrcurval.get(count);
                        String from[] = {"quotes"};
                        int to[] = {android.R.id.text1};
                        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(),
                                android.R.layout.simple_list_item_1, crs, from, to);
                        lvContact.setAdapter(adapter);
                        NEED_URI = QUOTES_URI;
                    }else if(NEED_URI == QUOTES_URI){
                        String item = ((TextView) view).getText().toString();
                        int count = position;
                        Log.e("ITEM_SELECTED", item);
                        Cursor crs = getContentResolver().query(NEED_URI,
                                new String[]{"quotes"},
                                selection,
                                null,
                                null);
                        startManagingCursor(crs);
                        ArrayList<String> arrcurval = new ArrayList<>();
                        if (crs.moveToFirst()) {
                            do {
                                arrcurval.add(crs.getString(0)); //<< pass column index here instead of i

                            } while (crs.moveToNext());
                        }
                        Log.e("ITEM_SELECTED", arrcurval.get(count));
                        ContentValues values = new ContentValues();
                        values.put("quote", arrcurval.get(count));
                        Uri newUri = getContentResolver().insert(FAVOURITES_URI, values);
                        Log.d("INSERT", "insert, result Uri : " + newUri.toString());
                    }
                }
            });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_synchronize) {

        }else if (id == R.id.nav_delete_all){

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.themes) {
            NEED_TABLE = TAG_THEMES;
            NEED_URI = THEMES_URI;
        } else if (id == R.id.authors) {
            NEED_TABLE = TAG_AUTHORS;
            NEED_URI = AUTHORS_URI;
        }else if (id == R.id.quotes){
            NEED_TABLE = TAG_QUOTES;
            NEED_URI = QUOTES_URI;
        }
        else if (id == R.id.favorites) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * Async task class to get json by making HTTP call
     * */
    public class GetContacts extends AsyncTask<Void, Void, Void> {
        private static final String LOG_TAG = "ContentProvider";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            ContentValues values1 = new ContentValues();
            ContentValues values2 = new ContentValues();
            ContentValues values3 = new ContentValues();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array THEMES
                    themes = jsonObj.getJSONArray(TAG_THEMES);
                    quotes = jsonObj.getJSONArray(TAG_QUOTES);
                    authors = jsonObj.getJSONArray(TAG_AUTHORS);
                    Cursor cursor = getContentResolver().query(THEMES_URI, null, null,
                            null, null);
                    startManagingCursor(cursor);
                    // looping through All Contacts
                    for (int i = 0; i < themes.length(); i++) {
                        JSONObject c = themes.getJSONObject(i);

                        String id = c.getString(TAG_THEMES_ID);
                        String name = c.getString(TAG_THEMES_NAME);
                        String time = c.getString(TAG_THEMES_TIME);

                        values1.put("_id", id);
                        values1.put("name", name);
                        values1.put("udatetime", time);
                        Uri newUri = getContentResolver().insert(THEMES_URI, values1);
                        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
                    }
                    cursor = getContentResolver().query(QUOTES_URI, null, null,
                            null, null);
                    startManagingCursor(cursor);
                    for (int i = 0; i < quotes.length(); i++) {
                        JSONObject c = quotes.getJSONObject(i);

                        String id = c.getString(TAG_QUOTES_ID);
                        String name = c.getString(TAG_QUOTES_NAME);
                        String themeid = c.getString(TAG_QUOTES_THEME);
                        String  time = c.getString(TAG_QUOTES_TIME);
                        String authorid = c.getString(TAG_QUOTES_AUTHOR);

                        values2.put("_id", id);
                        values2.put("quotes", name);
                        values2.put("theme_id", themeid);
                        values2.put("timestamp", time);
                        values2.put("author_id", authorid);
                        Uri newUri = getContentResolver().insert(QUOTES_URI, values2);
                        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
                    }
                    cursor = getContentResolver().query(AUTHORS_URI, null, null,
                            null, null);
                    startManagingCursor(cursor);
                    for (int i = 0; i < authors.length(); i++) {
                        JSONObject c = authors.getJSONObject(i);

                        String id = c.getString(TAG_AUTHORS_ID);
                        String name = c.getString(TAG_AUTHORS_NAME);
                        String time = c.getString(TAG_AUTHORS_TIME);

                        values3.put("_id", id);
                        values3.put("name", name);
                        values3.put("timestamp", time);
                        Uri newUri = getContentResolver().insert(AUTHORS_URI, values3);
                        Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
                    }
                    cursor = getContentResolver().query(THEMES_URI, null, null,
                            null, null);
                    startManagingCursor(cursor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    public void onClickSync(MenuItem item) {
        new GetContacts().execute();
        Cursor cursor = getContentResolver().query(THEMES_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "name" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);

        lvContact.setAdapter(adapter);
    }
    public void onClickDelete(MenuItem item) {
        Cursor cursor = getContentResolver().query(THEMES_URI, null, null,
                null, null);
        startManagingCursor(cursor);
        getContentResolver().delete(THEMES_URI, null, null);
        cursor = getContentResolver().query(QUOTES_URI, null, null,
                null, null);
        startManagingCursor(cursor);
        getContentResolver().delete(QUOTES_URI, null, null);
        cursor = getContentResolver().query(AUTHORS_URI, null, null,
                null, null);
        startManagingCursor(cursor);
        getContentResolver().delete(AUTHORS_URI, null, null);
    }




    public void onClickThemes(MenuItem item) {
        Cursor cursor = getContentResolver().query(THEMES_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "name" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);

        lvContact.setAdapter(adapter);
        NEED_URI = THEMES_URI;
    }
    public void onClickAuthors(MenuItem item) {
        Cursor cursor = getContentResolver().query(AUTHORS_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "name" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);

        lvContact.setAdapter(adapter);
        NEED_URI = AUTHORS_URI;
    }
    public void onClickQuotes(MenuItem item) {
        Cursor cursor = getContentResolver().query(QUOTES_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "quotes" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);

        lvContact.setAdapter(adapter);
        NEED_URI = QUOTES_URI;
    }
    public void onClickFavourites(MenuItem item){
        Cursor cursor = getContentResolver().query(FAVOURITES_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { "quote" };
        int to[] = { android.R.id.text1 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cursor, from, to);
        lvContact.setAdapter(adapter);
    }
}
