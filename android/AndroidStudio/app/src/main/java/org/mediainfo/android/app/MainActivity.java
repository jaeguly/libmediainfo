package org.mediainfo.android.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Activity implements MediaInfoRetrieverTask.OnCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Set up ShareActionProvider's default share intent
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        mShareActionProvider.setShareIntent(createShareIntent());
        mShareActionProvider.setOnShareTargetSelectedListener(
                new ShareActionProvider.OnShareTargetSelectedListener() {
                    @Override
                    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                        final String appName = intent.getComponent().getPackageName();
                        Toast.makeText(MainActivity.this, appName, Toast.LENGTH_SHORT).show();

                        return false;
                    }
                });

        // Return true to display menu
        return true;
    }

    /**
     * Returns an Intent which can be used to share this item's content with other applications.
     *
     * @return Intent to be given to a ShareActionProvider.
     */
    public Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mMessageView.getText());
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_share:
                mShareActionProvider.setShareIntent(createShareIntent());
                break;

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

                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {

                        MediaInfoRetrieverTask task = new MediaLibraryInfoRetrieverTask(mMessageView);
                        task.execute(params);

                        setMediaInfoTask(task);
                        return null;
                    }
                }.execute("");

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

            case R.id.action_app_exit:

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

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

        // for
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
    private void setMediaInfoTask(MediaInfoRetrieverTask task) {
        // If exists already a running task
        cancelMediaInfoTask();

        if (task != null)
            task.setOnCompleteListener(this);

        mMediaInfoTask = task;
    }

    private void cancelMediaInfoTask() {
        if (mMediaInfoTask != null && mMediaInfoTask.getStatus() != AsyncTask.Status.FINISHED) {
            mMediaInfoTask.cancel(true);
            mMediaInfoTask = null;
        }
    }

    @Override
    public void onCompletion(MediaInfoRetrieverTask task) {
        if (mShareActionProvider != null && mMessageView != null)
            mShareActionProvider.setShareIntent(createShareIntent());
    }

    private class RequestMediaInfoRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            File logDir = new File(LogDir);
            if (!logDir.exists())
                logDir.mkdirs();

            MediaInfoRetrieverTask task = new MediaInfoRetrieverTask(mMessageView);
            task.setOutputDir(logDir);
            task.execute(params);

            setMediaInfoTask(task);
            return null;
        }
    }

    private class RequestMediaInfoReportRetriever extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            MediaInfoRetrieverTask task = new MediaInfoReportRetrieverTask(mMessageView);
            task.execute(params);

            setMediaInfoTask(task);
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

    private ShareActionProvider mShareActionProvider;
    public static final String LogDir = "/mnt/sdcard/LogFiles/MediaInfo";
    private TextView mMessageView;
    private MediaInfoRetrieverTask mMediaInfoTask;
}

