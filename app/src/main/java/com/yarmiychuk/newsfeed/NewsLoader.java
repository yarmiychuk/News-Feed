package com.yarmiychuk.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by DmitryYarmiychuk on 24.06.2018.
 * Создал DmitryYarmiychuk 24.06.2018
 */
public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    private String requestUrl;

    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context    of the activity
     * @param requestUrl to load data from
     */
    NewsLoader(Context context, String requestUrl) {
        super(context);
        this.requestUrl = requestUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<NewsItem> loadInBackground() {
        if (requestUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of news.
        return LoaderHelper.getNewsData(requestUrl);
    }
}
