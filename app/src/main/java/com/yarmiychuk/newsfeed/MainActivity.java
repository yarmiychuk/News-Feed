package com.yarmiychuk.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final int LOADER_ID = 1;
    private static final String MAIN_REQUEST_URL = "https://content.guardianapis.com/search?";
    private static final String QUERY_REQUEST_KEY = "q";
    private static final String QUERY_REQUEST_VALUE = "ballet";
    private static final String QUERY_FORMAT_KEY = "format";
    private static final String QUERY_FORMAT_VALUE = "json";
    private static final String QUERY_PAGE_SIZE_KEY = "page-size";
    private static final String QUERY_TAG_KEY = "tag";
    private static final String QUERY_TAG_VALUE = "stage/dance";
    private static final String QUERY_FIELDS_KEY = "show-fields";
    private static final String QUERY_FIELDS_VALUE = "trailText,byline";
    private static final String QUERY_ORDER_KEY = "order-by";
    private static final String QUERY_KEY_KEY = "api-key";
    private static final String QUERY_KEY_VALUE = "c25750ff-e158-4d63-a537-4754cdbc3769";

    private ProgressBar pbLoading;
    private TextView tvInfo;

    private NewsAdapter adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define default Shared Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Invalidate views
        invalidateViews();

        // Invalidate loader for app
        invalidateLoader();
    }

    private void invalidateViews() {

        // Action Bar
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getString(R.string.full_app_name));
        }

        // Define views
        pbLoading = findViewById(R.id.loading_spinner);
        tvInfo = findViewById(R.id.tv_info);

        // RecyclerView, adapter and manager for show news list
        RecyclerView rvFeed = findViewById(R.id.rv_feed);
        adapter = new NewsAdapter(MainActivity.this, new ArrayList<NewsItem>());
        rvFeed.setAdapter(adapter);
        rvFeed.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    /**
     * Invalidate and start loader or show message about no internet connection
     */
    private void invalidateLoader() {
        if (isConnected()) {
            // Get a reference to the LoaderManager and initialize the loader.
            getLoaderManager().initLoader(LOADER_ID, null, this);
            // Show loading indicator
            pbLoading.setVisibility(View.VISIBLE);
            // Hide info message
            tvInfo.setVisibility(View.GONE);
        } else {
            // Hide loading indicator
            pbLoading.setVisibility(View.GONE);
            // Show error message
            tvInfo.setText(getString(R.string.no_internet_connection));
            tvInfo.setVisibility(View.VISIBLE);
        }
    }

    /**
     * The method gets the connection available.
     *
     * @return Is there access to the Internet.
     */
    private boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        // Get details on the currently active default data network
        NetworkInfo info = manager.getActiveNetworkInfo();
        // Return result
        return info != null && info.isConnected();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        //Create a new loader with special URL and user's preferences
        String numbersToDisplay = preferences.getString(
                getString(R.string.settings_number_to_display_key),
                getString(R.string.settings_number_to_display_default));
        String orderBy = preferences.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default));

        // Prepare request Uri
        Uri baseUri = Uri.parse(MAIN_REQUEST_URL);
        // Add parameters to request
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(QUERY_REQUEST_KEY, QUERY_REQUEST_VALUE);
        uriBuilder.appendQueryParameter(QUERY_FORMAT_KEY, QUERY_FORMAT_VALUE);
        uriBuilder.appendQueryParameter(QUERY_PAGE_SIZE_KEY, numbersToDisplay);
        uriBuilder.appendQueryParameter(QUERY_TAG_KEY, QUERY_TAG_VALUE);
        uriBuilder.appendQueryParameter(QUERY_FIELDS_KEY, QUERY_FIELDS_VALUE);
        uriBuilder.appendQueryParameter(QUERY_ORDER_KEY, orderBy);
        uriBuilder.appendQueryParameter(QUERY_KEY_KEY, QUERY_KEY_VALUE);

        // Return new Loader
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> newsItems) {
        // Hide loading indicator because the data has been loaded
        pbLoading.setVisibility(View.GONE);
        // Invalidate info about news
        if (newsItems != null && newsItems.size() > 0) {
            // There is news on the topic.
            tvInfo.setVisibility(View.GONE);
        } else {
            // There is no news on the topic. Show the message
            tvInfo.setText(getString(R.string.no_news_found));
            tvInfo.setVisibility(View.VISIBLE);
        }
        // Notify adapter about new data set
        adapter.setNewDataSet(newsItems);
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        // Clear data in adapter
        adapter.clearData();
    }
}
