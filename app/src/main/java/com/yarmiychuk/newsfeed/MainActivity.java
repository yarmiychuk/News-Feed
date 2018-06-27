package com.yarmiychuk.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private static final int LOADER_ID = 1;
    private static final String REQUEST_URL = "https://content.guardianapis.com/search?q=ballet&format=json&page-size=50&tag=stage/dance&show-fields=trailText,byline&order-by=newest&api-key=c25750ff-e158-4d63-a537-4754cdbc3769";

    private ProgressBar pbLoading;
    private TextView tvInfo;

    private NewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    public Loader<List<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader with special URL
        return new NewsLoader(this, REQUEST_URL);
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
