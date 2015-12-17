/*  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license that can
 *  be found in the License.html file in the root of the source tree.
 */
package org.mediainfo.android.app;

import android.os.AsyncTask;
import android.widget.TextView;

import org.mediainfo.android.MediaInfo;
import org.mediainfo.android.app.util.FileTreeWalker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous task that retrieves a media info from a given file.
 */
public class MediaInfoRetrieverTask extends AsyncTask<String, String, Void> {

    public MediaInfoRetrieverTask(TextView textView) {
        mTextView = textView;
    }

    /**
     * If defines an output directory, will save the result into a file in outputDir.
     */
    public void setOutputDir(File outputDir) {
        //  if dir doesn't exists, then create it
        if (!outputDir.exists())
            outputDir.mkdirs();

        mOutDir = outputDir;
    }

    /**
     * Register a callback to be invoked when the end of a task.
     */
    public void setOnCompleteListener(OnCompleteListener listener) {
        mCompleteListener = listener;
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

            openOutput(path);

            // checks cancelled
            if (isCancelled())
                break;

            // retrieve a media information
            printOutput("\n#\n# '" + path + "'\n#\n");
            printOutput(mi.getMI(path);

            // checks cancelled
            if (isCancelled())
                break;

            // releases resources
            closeOutput();
        }

        if (isCancelled()) {
            mi.close();
            closeOutput();
        }

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
        if (mCompleteListener != null)
            mCompleteListener.onCompletion(this);
    }

    @Override
    protected void onCancelled() {
        // TODO: do something
    }

    private void openOutput(String filePath) {
        if (mOutDir != null) {
            try {

                String infoFileName = new File(filePath).getName() + ".info";

                mOutStream = new BufferedOutputStream(
                        new FileOutputStream(
                            // The file will be truncated if it exists,
                            // and created if it doesn't exist.
                            new File(mOutDir, infoFileName)
                        )
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printOutput(String... params) {
        publishProgress(params);

        if (mOutStream != null) {
            for (String param : params) {
                try {
                    mOutStream.write(param.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeOutput() {
        if (mOutStream != null) {

            try {
                mOutStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mOutStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
     * This listener to be invoked when retrieving the media information has completed
     * and is for something in the UI thread.
     */
    public static interface OnCompleteListener {
        void onCompletion(MediaInfoRetrieverTask task);
    }

    protected File mOutDir;
    protected BufferedOutputStream mOutStream;
    protected TextView mTextView;
    protected OnCompleteListener mCompleteListener;
}
