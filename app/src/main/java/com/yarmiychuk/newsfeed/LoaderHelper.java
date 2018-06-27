package com.yarmiychuk.newsfeed;

import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by DmitryYarmiychuk on 25.06.2018.
 * Helper methods related to requesting and receiving news data.
 */

final class LoaderHelper {

    private static final String LOG_TAG = LoaderHelper.class.getSimpleName();

    private static final String JSON_RESPONSE = "response";
    private static final String JSON_RESULTS = "results";
    private static final String JSON_TITLE = "webTitle";
    private static final String JSON_TEXT = "trailText";
    private static final String JSON_PUBLICATION_DATE = "webPublicationDate";
    private static final String JSON_URL = "webUrl";
    private static final String JSON_FIELDS = "fields";
    private static final String JSON_BYLINE = "byline";

    // Default constructor
    private LoaderHelper() {
    }

    /**
     * Make a query and return a list of {@link NewsItem} objects.
     */
    static List<NewsItem> getNewsData(String requestUrl) {
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(createUrl(requestUrl));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
        }
        // Extract relevant fields from the JSON response and create a list of NewsItem's
        // Return the list of NewsItem's
        return extractNewsFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL: " + e.getMessage());
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        //Make connection
        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results. " + e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Close InputStream.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from parsing a JSON response.
     */
    private static List<NewsItem> extractNewsFromJson(String newsJSON) {

        // Create an empty List that we can start adding news to
        List<NewsItem> news = new ArrayList<>();

        if (!TextUtils.isEmpty(newsJSON)) {

            // Try to parse the SAMPLE_JSON_RESPONSE.
            try {
                // Convert String to JSONObject
                JSONObject baseJsonResponse = new JSONObject(newsJSON);
                // Get query result
                JSONObject resultsJsonResponse = baseJsonResponse.getJSONObject(JSON_RESPONSE);
                // Get array of results
                JSONArray newsArray = resultsJsonResponse.getJSONArray(JSON_RESULTS);
                for (int i = 0; i < newsArray.length(); i++) {
                    // Get current item
                    JSONObject currentNews = newsArray.getJSONObject(i);
                    // Date of publication
                    String date = convertDate(currentNews.getString(JSON_PUBLICATION_DATE));
                    // Title
                    String title = currentNews.getString(JSON_TITLE);
                    // Link to publication
                    String webUrl = currentNews.getString(JSON_URL);
                    // Additional fields
                    JSONObject jsonFields = currentNews.getJSONObject(JSON_FIELDS);
                    // Short text of publication
                    String description = convertText(jsonFields.getString(JSON_TEXT));
                    // Author of publication
                    String author = jsonFields.getString(JSON_BYLINE);

                    // Create a new NewsItem object.
                    NewsItem newsItem = new NewsItem(date, title, description, author, webUrl);

                    // Add the new NewsItem to the list of news.
                    news.add(newsItem);
                }

            } catch (JSONException e) {
                // If an error - print a log message from the exception.
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results" + e.getMessage());
            }
        }

        // Return the list of news
        return news;
    }

    /**
     * Convert String 'date' from received format
     *
     * @param date - string in received format
     * @return - date in user-friendly format
     */
    @Contract("null -> null")
    private static String convertDate(String date) {
        if (date == null) {
            return null;
        }
        date = date.substring(11, 16) + ", " + date.substring(0, 10);
        date = date.replaceAll("-", ".");
        return date;
    }

    /**
     * Method removed html tags from text
     *
     * @param text - jriginal text
     * @return - converted text without tags
     */
    @Contract("null -> null")
    private static String convertText(String text) {
        if (text == null) {
            return null;
        }
        text = Html.fromHtml(text).toString();
        return text;
    }
}
