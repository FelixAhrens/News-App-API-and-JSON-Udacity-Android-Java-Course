package com.android.example.cryptonewsbytheguardian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsClass>> {

    //New LOADER
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;

    /**
     * Adapter for the list of news
     */
    private NewsAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    /**
     * URL for news data from the Guardian
     */
    private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search";

    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        //Check for Internet
        mNetworkChangeReceiver = new NetworkChangeReceiver();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<NewsClass>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        /** TextView that is displayed when the list is empty */
        mEmptyStateTextView = (TextView) findViewById(R.id.no_data);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                NewsClass currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //
        ////New LOADER
        //
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = LoaderManager.getInstance(this);

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);

    }

    //
    // METHODS FOR LOADER
    //

    @Override
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<NewsClass>> onCreateLoader(int i, Bundle bundle) {

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value
        uriBuilder.appendQueryParameter("q", "bitcoin");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("page-size", "200");
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("api-key", "test");

        // Return the completed uri
        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<NewsClass>> loader, List<NewsClass> news) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No data found."
        // test: comment out 'news.add(new_news);' at QueryUtils.java
        mEmptyStateTextView.setText(R.string.no_data);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link NewsClass}, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsClass>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mNetworkChangeReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
        ));

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mNetworkChangeReceiver);

        super.onPause();
    }
}

