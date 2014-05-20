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

import java.io.File;


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
                if (mMessageView != null)
                    mMessageView.setText("");

                new RequestMediaInfoReportRetriever().execute("/mnt/sdcard/Download/Example.ogg");
                return true;

            case R.id.action_cdsdata_test:
                if (mMessageView != null)
                    mMessageView.setText("");

                new RequestMediaInfoRetriever().execute("/mnt/sdcard/Download/cds-data");
                return true;

//            case R.id.action_clear_messages:
//                if (mMessageView != null)
//                    mMessageView.setText("");
//
//                return true;

            case R.id.action_detailed_info:
                if (mMessageView != null)
                    mMessageView.setText("");

                new RequestMediaInfoReportRetriever().execute();
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMessageView = (TextView) findViewById(R.id.message_view_a);

        // If launches by an intent
        if (getIntent() != null)
            handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();

        // cleanup AsyncTasks
        cancelMediaInfoTask();
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


    /**
     * Return a old AsyncTask.
     */
    private void setMediaInfoTask(AsyncTask task) {
        // If exists already a running task
        cancelMediaInfoTask();

        mMediaInfoTask = task;
    }

    private void cancelMediaInfoTask() {
        if (mMediaInfoTask != null && mMediaInfoTask.getStatus() != AsyncTask.Status.FINISHED) {
            mMediaInfoTask.cancel(true);
            mMediaInfoTask = null;
        }
    }

    private class RequestMediaInfoRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            File logDir = new File(LogDir);
            if (!logDir.exists())
                logDir.mkdirs();

            AsyncTask task = new MediaInfoRetrieverTask(mMessageView, logDir);
            setMediaInfoTask(task);
            task.execute(params);

            return null;
        }
    }

    private class RequestMediaInfoReportRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            AsyncTask task = new MediaInfoReportRetrieverTask(mMessageView);
            setMediaInfoTask(task);
            task.execute(params);

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

    public static final String LogDir = "/mnt/sdcard/LogFiles/MediaInfo";
    private TextView mMessageView;
    private AsyncTask mMediaInfoTask;
}

