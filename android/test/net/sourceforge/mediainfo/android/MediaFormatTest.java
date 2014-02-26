package net.sourceforge.mediainfo.android;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Arrays;

import android.util.Log;

import net.sourceforge.mediainfo.MediaInfo;

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
	
    public static void main(String[] args) {

        // check given arguments
        int argIndex = 0;
        int itemIndex = 0;
        String[] items = new String[args.length];

        while (argIndex < args.length) {
            if (args[argIndex].equals("-v")) {
                sVerboseMode = true;
            } else if (args[argIndex].equals("-r")) {
                sRecursiveMode = true;
            } else if (args[argIndex].equals("--time")) {
                sTimeRecordMode = true;
            } else if (args[argIndex].equals("--excludes")) {
                argIndex++;
                sExcludesList = args[argIndex].toUpperCase().split(";");
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
            doMatchingFormat(items[i]);

        // release all objects
        sMediaInfo = null;
        sExcludesList = null;
        items = null;

        Runtime r = Runtime.getRuntime();
        r.gc();
    }

    private static void doMatchingFormat(String path) {
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

    private static void checkFileFormat(String path) {
        //if (sVerboseMode)
            Log.i(LOG_TAG, "'" + path + "'\n");

        // ignore case: if filename started with '_'.
        if (path.substring(path.lastIndexOf("\\") + 1).startsWith("_")) {
            Log.w(LOG_TAG, "(skipped)\n");
            return;
        }

        // ignore case: if filename's extension is in a excluded list.
        if (sExcludesList != null) {
            String upperPath = path.toUpperCase();
            for (String extension : sExcludesList) {

                if (upperPath.endsWith('.' + extension)) {
                    Log.w(LOG_TAG, "(skipped)\n");
                    return;
                }
            }
        }

        Calendar cal1 = Calendar.getInstance();
        MediaInfoData infoData = MediaInfoDataBuilder.build(sMediaInfo, path);
        if (infoData == null) {
            Log.w(LOG_TAG, "(can't parse)\n");
            return ;
        }
        
         // media detection on native environment
        Calendar cal2 = Calendar.getInstance();

        // DLNA profile detection on JVM
        Calendar cal3 = Calendar.getInstance();

        String profileName = "-";
        String mimeName = infoData.general_internetmediatype;

        if (sVerboseMode) {
            String filename = path.substring(path.lastIndexOf("\\") + 1);
            Log.i(LOG_TAG, filename + " '" + profileName + "' '" + mimeName + "'\n");
        }

        File file = new File(path);

        if (sTimeRecordMode) {
            Log.i(LOG_TAG, "[RESULT]\t" + path + "\t" + profileName + "\t" + mimeName + "\t" + file.length()
                    + "bytes\t" + (cal2.getTimeInMillis() - cal1.getTimeInMillis()) + "ms\t"
                    + (cal3.getTimeInMillis() - cal2.getTimeInMillis()) + "ms\n");
        } else {
            Log.i(LOG_TAG, "[RESULT]\t" + path + "\t" + profileName + "\t" + mimeName + "\t" + file.length()
                    + " bytes\n");
        }

        if (sVerboseMode) {
            Log.i(LOG_TAG, infoData.toStringGeneralPart());
            Log.i(LOG_TAG, infoData.toStringVideoPart());
            Log.i(LOG_TAG, infoData.toStringAudioPart());
            Log.i(LOG_TAG, infoData.toStringImagePart());
        }
    }

    private static MediaInfo sMediaInfo = new MediaInfo();
    private static boolean sTimeRecordMode = false;
    private static boolean sVerboseMode = false;
    private static boolean sRecursiveMode = false;
    private static String[] sExcludesList;
}
