package org.mediainfo.android.app;

import android.os.AsyncTask;
import android.widget.TextView;

import org.mediainfo.android.MediaInfo;
import org.mediainfo.android.app.util.FileTreeWalker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous task that retrieves a media info from a given file.
 */
public class MediaInfoRetrieverTask extends AsyncTask<String, String, Void> {

    public MediaInfoRetrieverTask(TextView textView) {
        mTextView = textView;
    }

    @Override
    /** Override this method to perform a computation on a background thread. */
    protected Void doInBackground(String... paths) {

        // will contain a path of all files of a given path
        List<String> list = new ArrayList<String>();

        if (paths != null) {
            for (String path : paths) {
                new FileTreeWalker(list).walk(new File(path));
            }
        }

        MediaInfo mi = new MediaInfo();

        for (String path : list) {
            // checks cancelled
            if (isCancelled())
                break;

            mi.open(path);

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            mi.option("Complete", "1");

            publishProgress("\n>> '" + path + "'\n\n");
            publishProgress(mi.inform());

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            mi.close();
        }

        // release all resources of mi
        mi.dispose();

        return null;
    }

    @Override
    /** Runs on the UI thread after publishProgress(Progress...) is invoked. */
    protected void onProgressUpdate(String... infos) {
        for (String info : infos)
            mTextView.append(info);
    }

    @Override
    /** Runs on the UI thread after doInBackground(Params...). */
    protected void onPostExecute(Void result) {
        // TODO: do something
    }

    @Override
    protected void onCancelled() {
        // TODO: do something
    }

    protected TextView mTextView;
}
