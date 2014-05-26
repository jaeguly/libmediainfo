package org.mediainfo.android.app;

import android.widget.TextView;

import org.mediainfo.android.MediaInfo;
import org.mediainfo.android.app.util.FileTreeWalker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Asynchronous task that retrieves a media info from a given file.
 */
public class MediaInfoReportRetrieverTask extends MediaInfoRetrieverTask {

    public MediaInfoReportRetrieverTask(TextView textView) {
        super(textView);
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

            // print a file path
            publishProgress("\n#\n# '" + path + "'\n#\n");

            // try to open
            if (mi.open(path) > 0)
                publishProgress("\n\nOpen is OK\n");
            else
                publishProgress("\n\nOpen has a problem\n");

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            // try to inform
            mi.option("Complete", "");
            publishProgress("\n\nInform with Complete=false\n", mi.inform());

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            mi.option("Complete", "1");
            publishProgress("\n\nInform with Complete=true\n", mi.inform());

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            mi.option("Inform", "General;Example : FileSize=%FileSize%");
            publishProgress("\n\nCustom Inform\n", mi.inform());

            // checks cancelled
            if (isCancelled()) {
                mi.close();
                break;
            }

            // try to get
            publishProgress("\n\nGetI with Stream=General and Parameter=2\n",
                    mi.get(MediaInfo.StreamKind.GENERAL, 0, 2, MediaInfo.InfoKind.TEXT));

            publishProgress("\n\nCount with StreamKind=Stream_Audio\n",
                    String.valueOf(mi.countGet(MediaInfo.StreamKind.AUDIO, -1)));

            publishProgress("\n\nGet with Stream=General and Parameter=\"AudioCount\"\n",
                    mi.get(MediaInfo.StreamKind.GENERAL, 0, "AudioCount", MediaInfo.InfoKind.TEXT, MediaInfo.InfoKind.NAME));

            publishProgress("\n\nGet with Stream=Audio and Parameter=\"StreamCount\"\n",
                    mi.get(MediaInfo.StreamKind.AUDIO, 0, "StreamCount", MediaInfo.InfoKind.TEXT, MediaInfo.InfoKind.NAME));

            publishProgress("\n\nGet with Stream=General and Parameter=\"FileSize\"\n",
                    mi.get(MediaInfo.StreamKind.GENERAL, 0, "FileSize", MediaInfo.InfoKind.TEXT, MediaInfo.InfoKind.NAME));

            // try to close
            publishProgress("\n\nClose\n");

            mi.close();
        }

        // release all resources of mi
        mi.dispose();

        return null;
    }
}
