package net.sourceforge.mediainfo;

import android.util.Log;

import org.mediainfo.android.MediaInfo;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

/*
 * MediaFormatTest은 주어진 미디어 파일이나 특정 디렉토리 아래의 미디어 파일에 대한
 * DLNA profile 정보를 알려준다.
 * 
 * $ ./MediaFormatTest $url
 * $ ./MediaFormatTest -v $url
 * $ ./MediaFormatTest -r $url
 * $ ./MediaFormatTest --time $url
 * $ ./MediaFormatTest --excludes "xls;ifo" $url 
 * 
 * @author jaeguly
 */
public class MediaFormatTest {

    public static final String LOG_TAG = "MediaFormatTest";

    protected MediaFormatTest() {
        mediaInfo = new MediaInfo();
        mediaInfo.option("Complete", "1");
    }

    public void releaseAll() {
        // release all objects
        mediaInfo = null;
        excludesList = null;
    }

    public static void main(String[] args) {

        MediaFormatTest test = new MediaFormatTest();

        // check given arguments
        int argIndex = 0;
        int itemIndex = 0;
        String[] items = new String[args.length];

        while (argIndex < args.length) {
            if (args[argIndex].equals("-v")) {
                test.verboseMode = true;
            } else if (args[argIndex].equals("-r")) {
                test.recursiveMode = true;
            } else if (args[argIndex].equals("--time")) {
                test.timeRecordMode = true;
            } else if (args[argIndex].equals("--excludes")) {
                argIndex++;
                test.excludesList = args[argIndex].toUpperCase().split(";");
            } else if (args[argIndex].equals("-i")) {
                try {
                    Log.i(LOG_TAG, "waiting a key press ..\n");
                    System.in.read();
                } catch (IOException e) {
                    System.exit(1);
                }
            } else {
                items[itemIndex++] = args[argIndex];
            }

            argIndex++;
        }

        if (itemIndex == 0) {
            Log.e(LOG_TAG, "Path is empty.\n");
            return;
        }

        for (int i = 0; i < itemIndex; i++)
            test.doMatchingFormat(items[i]);

        test.releaseAll();

        Runtime r = Runtime.getRuntime();
        r.gc();
    }

    private void doMatchingFormat(String path) {
        File file = new File(path);

        if (file.isDirectory()) {

            // get a listing of all files in the directory
            String[] fileNames = file.list();

            if (fileNames != null) {
                // sort the list of files
                Arrays.sort(fileNames);

                for (int i = 0; i < fileNames.length; i++) {
                    doMatchingFormat(path + "/" + fileNames[i]);
                }
            }

        } else if (file.isFile()) {

            checkFileFormat(path);

        } else {
            // error case
            String filename = path.substring(path.lastIndexOf("\\") + 1);
            Log.e(LOG_TAG, "The '" + filename + "' is invalid!\n");
        }
    }

    private void checkFileFormat(String path) {
        //if (sVerboseMode)
        Log.i(LOG_TAG, "'" + path + "'\n");

        // ignore case: if filename started with '_'.
        if (path.substring(path.lastIndexOf("\\") + 1).startsWith("_")) {
            Log.w(LOG_TAG, "(skipped)\n");
            return;
        }

        // ignore case: if filename's extension is in a excluded list.
        if (excludesList != null) {
            String upperPath = path.toUpperCase();
            for (String extension : excludesList) {

                if (upperPath.endsWith('.' + extension)) {
                    Log.w(LOG_TAG, "(skipped)\n");
                    return;
                }
            }
        }

        Calendar cal1 = Calendar.getInstance();
        MediaInfoData infoData = MediaInfoDataBuilder.build(mediaInfo, path);
        if (infoData == null) {
            Log.w(LOG_TAG, "(can't parse)\n");
            return;
        }

        // media detection on native environment
        Calendar cal2 = Calendar.getInstance();

        // DLNA profile detection on JVM
        Calendar cal3 = Calendar.getInstance();

        String profileName = "-";
        String mimeName = infoData.general_internetmediatype;

        if (verboseMode) {
            String filename = path.substring(path.lastIndexOf("\\") + 1);
            Log.i(LOG_TAG, filename + " '" + profileName + "' '" + mimeName + "'\n");
        }

        File file = new File(path);

        if (timeRecordMode) {
            Log.i(LOG_TAG, "[RESULT]\t" + path + "\t" + profileName + "\t" + mimeName + "\t" + file.length()
                    + "bytes\t" + (cal2.getTimeInMillis() - cal1.getTimeInMillis()) + "ms\t"
                    + (cal3.getTimeInMillis() - cal2.getTimeInMillis()) + "ms\n");
        } else {
            Log.i(LOG_TAG, "[RESULT]\t" + path + "\t" + profileName + "\t" + mimeName + "\t" + file.length()
                    + " bytes\n");
        }

        if (verboseMode) {
            Log.i(LOG_TAG, infoData.toStringGeneralPart());
            Log.i(LOG_TAG, infoData.toStringVideoPart());
            Log.i(LOG_TAG, infoData.toStringAudioPart());
            Log.i(LOG_TAG, infoData.toStringImagePart());
            Log.i(LOG_TAG, infoData.inform_detail);
        }
    }

    private MediaInfo mediaInfo = null;
    private boolean timeRecordMode = false;
    private boolean verboseMode = false;
    private boolean recursiveMode = false;
    private String[] excludesList = null;
}
