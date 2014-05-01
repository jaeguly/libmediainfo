package org.mediainfo.android.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_do_test) {

            new RequestMediaInfoRetriever().execute("/mnt/sdcard/Download/cds-data");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMessageView = (TextView) findViewById(R.id.message_view_a);

        // If launches by an intent
        if (getIntent() != null)
            handleIntent(getIntent());
    }

    private boolean handleIntent(Intent intent) {
        String action = intent.getAction();

        // android.intent.action.VIEW
        if (action.equals(Intent.ACTION_VIEW)) {
            Uri fileUri = intent.getData();

            new RequestMediaInfoRetriever().execute(fileUri.getPath());

            return true;
        }

        return false;
    }

    private class RequestMediaInfoRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            new MediaInfoRetrieverTask(mMessageView).execute(params);

            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    private TextView mMessageView;
    private MediaInfoRetrieverTask mediaInfoRetrieverTask;
//    private StatusTracker mStatusTracker = StatusTracker.getInstance();
}

