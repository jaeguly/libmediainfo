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

        // Info about the library
        publishProgress(MediaInfo.optionStatic("Info_Version"));

        publishProgress("\n\n\n### Parameters\n\n");
        publishProgress(MediaInfo.optionStatic("Info_Parameters"));

        publishProgress("\n\n\n### Codecs\n\n");
        publishProgress(MediaInfo.optionStatic("Info_Codecs"));

        publishProgress("\n\n\n### Capacities\n\n");
        publishProgress(MediaInfo.optionStatic("Info_Capacities"));

        return null;
    }
}
