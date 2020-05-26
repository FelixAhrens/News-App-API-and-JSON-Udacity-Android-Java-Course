package com.android.example.cryptonewsbytheguardian;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link NewsClass} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<NewsClass> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding news to
        List<NewsClass> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // extract the JSONObject associated with the key called "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news).
            JSONArray newsArray = response.getJSONArray("results");

            // For each news in the newsArray, create an {@link NewsClass} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String news_title = currentNews.getString("webTitle");

                if (news_title.contains("bitcoin")
                        || news_title.contains("Bitcoin")
                        || news_title.contains("cryptocurrencies")
                        || news_title.contains("Cryptocurrencies")
                        || news_title.contains("cryptocurrency")
                        || news_title.contains("Cryptocurrency")) {

                    // Extract the value for the key called "sectionId"
                    String section = currentNews.getString("sectionName");

                    // Extract the value for the key called "url"
                    String url = currentNews.getString("webUrl");

                    // Extract the value for the key called "webPublicationDate"
                    String date = currentNews.getString("webPublicationDate");

                    //another tree
                    // extract the JSONObject associated with the key called "fields"
                    JSONObject fields = currentNews.getJSONObject("fields");

                    // Extract the value for the key called "byline"
                    String author = fields.getString("byline");

                    // Create a new {@link NewsClass} object with the webTitle, byline and webUrl from the JSON response.
                    NewsClass new_news = new NewsClass(section, news_title, author, date, url);

                    // Add the new {@link NewsClass} to the list of news.
                    news.add(new_news);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        // Return the list of earthquakes
        return news;
    }
//
    // HELPER METHODES UNTIL readFromStream JUST COPY AND PAST e.g. from UDACITY Did You Feel It? APP
    //

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Guardian dataset and return a list of {@link NewsClass} objects.
     */
    public static List<NewsClass> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsClass}
        List<NewsClass> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsClass}
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
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
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

}
