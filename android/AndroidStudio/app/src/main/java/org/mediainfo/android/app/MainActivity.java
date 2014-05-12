package org.mediainfo.android.app;

import android.content.Context;
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
        switch (id) {
            case R.id.action_exampleogg_test:
                new RequestMediaInfoReportRetriever().execute("/mnt/sdcard/Download/Example.ogg");
                return true;
            case R.id.action_cdsdata_test:
                new RequestMediaInfoRetriever().execute("/mnt/sdcard/Download/cds-data");
                return true;
            case R.id.action_copy_clipboard:

                if (mMessageView != null) {
                    String text = mMessageView.getText().toString();

                    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                        clipboard.setText(text);
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("copied text", text));
                    }
                }
                return true;
            default:
                break;
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

    private class RequestMediaInfoReportRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            new MediaInfoReportRetrieverTask(mMessageView).execute(params);

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

