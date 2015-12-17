/*  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license that can
 *  be found in the License.html file in the root of the source tree.
 */
package org.mediainfo.android.app;

import android.widget.TextView;

import org.mediainfo.android.MediaInfo;

/**
 * Asynchronous task that retrieves a media info from a given file.
 */
public class MediaLibraryInfoRetrieverTask extends MediaInfoRetrieverTask {

    public MediaLibraryInfoRetrieverTask(TextView textView) {
        super(textView);
    }

    @Override
    /** Override this method to perform a computation on a background thread. */
    protected Void doInBackground(String... paths) {

        MediaInfo mi = new MediaInfo();
        // Info about the library
        publishProgress(mi.getMIOption("Info_Version"));

        publishProgress("\n\n\n### Parameters\n\n");
        publishProgress(mi.getMIOption("Info_Parameters"));

        publishProgress("\n\n\n### Codecs\n\n");
        publishProgress(mi.getMIOption("Info_Codecs"));

        publishProgress("\n\n\n### Capacities\n\n");
        publishProgress(mi.getMIOption("Info_Capacities"));

        return null;
    }
}
