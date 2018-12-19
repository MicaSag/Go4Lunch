package com.android.sagot.go4lunch.Utils;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

public class Go4LunchAsyncTask extends AsyncTask<Void, Void, Long> {

    private static final String TAG = "Go4LunchAsyncTask";

    // Implement listeners methods (Callback)
    public interface Listeners {
        void onPreExecute();
        void doInBackground();
        void onPostExecute(Long success);
    }

    // Declare callback
    private final WeakReference<Listeners> callback;

    // Constructor
    public Go4LunchAsyncTask(Listeners callback){
        this.callback = new WeakReference<>(callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.callback.get().onPreExecute(); // Call the related callback method
        Log.d(TAG, "Go4LunchAsyncTask is started.");
    }

    @Override
    protected void onPostExecute(Long success) {
        super.onPostExecute(success);
        this.callback.get().onPostExecute(success); // Call the related callback method
        Log.d(TAG, "Go4LunchAsyncTask is finished.");
    }

    @Override
    protected Long doInBackground(Void... voids) {
        this.callback.get().doInBackground(); // Call the related callback method
        Log.d(TAG, "Go4LunchAsyncTask doing some big work...");
        return null;
    }
}
