package com.android.sagot.go4lunch.Controllers.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.sagot.go4lunch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**************************************************************************************************
 *
 *  ACTIVITY that displays the web page of the restaurant
 *  ------------------------------------------------------
 *  IN = Url of the Restaurant site : String
 *
 **************************************************************************************************/

public class WebViewActivity extends AppCompatActivity {

    // For Debug
    private static final String TAG = WebViewActivity.class.getSimpleName();

    // Parameter for the construction of the fragment
    public static final String KEY_RESTAURANT_WEB_SITE_URL = "KEY_RESTAURANT_WEB_SITE_URL";

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    @BindView(R.id.activity_web_view_layout) WebView mWebView;
    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    // URL of the Restaurant WebSite
    private String mRestaurantWebSiteURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Get & serialise all views
        ButterKnife.bind(this);

        // Get back the data intent in the activity
        Intent i = getIntent();
        mRestaurantWebSiteURL = i.getStringExtra(KEY_RESTAURANT_WEB_SITE_URL);

        // Configuring Toolbar
        this.configureToolbar();

        // Set emulator View
        mWebView.setWebViewClient(new WebViewClient());

        // Show the page of the news
        mWebView.loadUrl(mRestaurantWebSiteURL);
    }

    private void configureToolbar(){
        Log.d(TAG, "configureToolbar: ");
         //Set the toolbar
        setSupportActionBar(mToolBar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
