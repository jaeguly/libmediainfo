package org.mediainfo.android.app;

/**
 * This listener receives notifications from an retrieving information from a media file.
 */
public interface MediaInfoRetrieverListener {
    /** Notifies the start of the a media file parsing. */
    void onRetrieverStart(MediaInfoRetrieverTask task, String filePath);

    /** Notifies the part of the media parsing. */
    void onRetrieverProgress(MediaInfoRetrieverTask task, String mediaInfoPart);

    /** Notifies the end of the a media file parsing. */
    void onRetrieverEnd(MediaInfoRetrieverTask task);
}
