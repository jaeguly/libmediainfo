package org.mediainfo.android.app.util;


import android.os.Handler;
import android.widget.TextView;

public class Utils {

    private static StatusTracker mStatusTracker = StatusTracker.getInstance();

    /**
     * Helper method to print out the media information of a media file.
     * Note this has been wrapped in a Handler to delay the output due to
     * a more or less operation time of MediaInfo.
     *
     * @param viewStatus TextView to list out the status of MediaInfo
     */
    public static void printStatus(final TextView viewStatus, final String message) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
//                // Get the stack of Activity lifecycle methods called and print to TextView
//                StringBuilder sbMethods = new StringBuilder();
//                List<String> listMethods = mStatusTracker.getMethodList();
//                for (String method : listMethods) {
//                    sbMethods.insert(0, method + "\r\n");
//                }
//
//                // Get the status of all Activity classes and print to TextView
//                StringBuilder sbStatus = new StringBuilder();
//                for (String key : mStatusTracker.keySet()) {
//                    sbStatus.insert(0,key + ": " + mStatusTracker.getStatus(key) + "\n");
//                }
//                if(viewStatus != null) {
//                    viewStatus.setText(sbStatus.toString());
//                }
                if (viewStatus != null)
                    viewStatus.append(message);
            }
        }, 750);
    }
}


