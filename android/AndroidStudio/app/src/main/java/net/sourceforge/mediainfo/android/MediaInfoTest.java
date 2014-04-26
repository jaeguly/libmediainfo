package net.sourceforge.mediainfo.android;

import java.io.File;
import java.util.Arrays;

import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.sourceforge.mediainfo.MediaInfo;
import net.iharder.Base64;

public class MediaInfoTest {

	public static String LOG_TAG = "mediainfo_test";

    //    public static void main(String[] args) {
    public static void doTest(String targetMedia, LinearLayout gr, Activity me) {
        try {
            File targetFile = new File(targetMedia);

            if (targetFile.isDirectory()) {

                File[] fileNames = targetFile.listFiles();

                if (fileNames == null) {
                    Log.w(LOG_TAG, "have no test files for mediainfo.\n");
                    return;
                }

                Arrays.sort(fileNames);

                for (int i = 0; i < fileNames.length; i++)
                    doTest(fileNames[i] + "", gr, me);

            } else if (targetFile.isFile()) {

                MediaInfo mi = new MediaInfo();

                testGetInfo(mi, targetMedia, gr, me);

                mi.destroy();

            } else {

                // error case
                String filename = targetMedia.substring(targetMedia.lastIndexOf("/") + 1);
                Log.e(LOG_TAG, "The '" + filename + "' is invalid!\n");
            }

        } catch (Exception e) {
            
            String msg = "Exception: " + e.getMessage() + "\n";
            StackTraceElement[] els = e.getStackTrace();

            for (int i = 0; i < els.length; i++) {
                StackTraceElement stk = els[i];
                msg += stk.getFileName() + ", Line Number:  " + stk.getLineNumber() + ", "
                        + stk.getClassName() + " Class, " + stk.getMethodName() + "Method\n";
            }

            Log.e(LOG_TAG, msg);
        }

    }

    public static void testGetInfo(MediaInfo mi, String fullpath, LinearLayout gr, Activity me) {
        TextView tv = new TextView(me);
        tv.append("========================================\n");
        tv.append("Test File: " + fullpath + "\n");
        tv.append("MediaInfo-------------------------\n");

        // test for MediaInfo.open()
        //
        int fh = mi.open(fullpath);
        tv.append("open() returns: " + fh + "\n");
        if (fh != 1) {
            Log.e(LOG_TAG, "mi.open() failed\n");
            return;
        }

        // test for MediaInfo.option()
        //
        //String option = mi.getOption("Complete");
        //tv.append("option('Complete','1') returns: " + option + "\n");

        // TODO:
        //tv.append("Inform: " + mi.getGeneralInfo( 0, "Inform") + "\n");

        // test for MediaInfo.getGeneralInfo()
        //
        String[] generalInfos = { "StreamKind", "Title", "InternetMediaType", "Format",
                "Format/Url", "Format_Commercial", "Format_Commercial_IfAny", "Format_Profile",
                "Format_Settings", "Lyrics", "Image_Format_List", "Album", "Track", "Label",
                "Genre", "File_Created_Date", "Performer", "DotsPerInch", "Lightness",
                "Played_Count", "Duration", "BitsRage/String", "StreamSize", "FileSize", "CodecID",
                "CodecID/Info", "Duration/String3", "File_Modified_Date", "Cover", "Cover_Type",
                "Cover_Mime" }; // too many "Cover_Data"

        tv.append("General-------------------------\n");

        for (String s : generalInfos)
            tv.append(s + ": " + mi.get(MediaInfo.StreamKind.GENERAL, 0, s) + "\n");

        gr.addView(tv);

        if (mi.get(MediaInfo.StreamKind.GENERAL, 0, "Cover").equals("Yes")) {
            String coverData = mi.get(MediaInfo.StreamKind.GENERAL, 0, "Cover_Data");
            try {
                byte[] bytes = Base64.decode(coverData, Base64.NO_OPTIONS);
                ImageView iv = new ImageView(me);
                iv.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                gr.addView(iv);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        TextView vi = new TextView(me);

        // test for MediaInfo.getVideoInfo()
        //
        String[] videoInfos = { "StreamKind", "StreamCount", "Format", "Format/Info",
                "Format_Profile", "InternetMediaType", "Duration", "BitRate_Mode", "BitRate",
                "Width", "Height", "Width_Original", "Height_Original", "Rotation",
                "FrameRate_Mode", "FrameRate", "Compression_Mode", "StreamSize",
                "Compression_Mode", "Title", "Encoded_Library", "Language" };

        vi.append("Video-------------------------\n");

        for (String s : videoInfos)
            vi.append(s + ": " + mi.get(MediaInfo.StreamKind.VIDEO, 0, s) + "\n");

        // test for MediaInfo.getAudioInfo()
        //
        String[] audioInfos = { "StreamKind", "ID", "Format", "Format/Info", "CodecID", "Duration",
                "BitRate_Mode", "BitRate", "Channel(s)", "SamplingRate", "Compression_Mode",
                "StreamSize", "Title", "Language" };

        vi.append("Audio-------------------------\n");

        for (String s : audioInfos)
            vi.append(s + ": " + mi.get(MediaInfo.StreamKind.AUDIO, 0, s) + "\n");

        // test for MediaInfo.getImageInfo()
        //
        String[] imageInfos = { "StreamKind", "ID", "Title", "Format", "InternetMediaType",
                "Width", "Height", "Resolution", "Encoded_Library", "Language" };

        vi.append("Image-------------------------\n");

        for (String s : imageInfos)
            vi.append(s + ": " + mi.get(MediaInfo.StreamKind.IMAGE, 0, s) + "\n");

        gr.addView(vi);

        // test for MediaInfo.close()
        mi.close();
    }

}
