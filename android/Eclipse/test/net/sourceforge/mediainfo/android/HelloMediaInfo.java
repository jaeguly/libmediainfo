package net.sourceforge.mediainfo.android;

import java.io.File;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HelloMediaInfo extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LinearLayout gr = new LinearLayout(this);
        gr.setOrientation(LinearLayout.VERTICAL);

        ScrollView so=new ScrollView(this);
        so.addView(gr);
        setContentView(so);

        android.util.Log.d("mediainfo-tester","started\n");

        // list up all files in a specific directory.
        File extPath = Environment.getExternalStorageDirectory();
        //String testMediaDir = extPath.getAbsolutePath() + "/mediainfo-tests";
        //String testMediaDir = extPath.getAbsolutePath() + "/www.dlnacontent.org";
        //String testMediaDir = extPath.getAbsolutePath() + "/www.dlnacontent.org/Image";
        //String testMediaDir = extPath.getAbsolutePath() + "/www.dlnacontent.org/Image/JPEG_LRG/B-JPEG_L-20.jpg";
        //String testMediaDir = "/mnt/sdcard/www.dlnacontent.org/Audio/AAC_ADTS_320/O-AAC_ADTS_320-stereo-44.1kHz-96k.adts";
        //String testMediaDir = "/mnt/sdcard/external_sd/SF_Reported_Contents";
        String testMediaDir = "/mnt/sdcard";

        
        //MediaInfoTest.doTest(testMediaDir, gr, this);        
        String[] args = { "-r", "-v", "/mnt/sdcard/Download/test" };
        
        MediaFormatTest.main(args);

        //setContentView(R.layout.main);
    }
}
