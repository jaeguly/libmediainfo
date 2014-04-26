package org.mediainfo.android.app;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.io.File;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        test();
    }

    private void test() {
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
        String[] args = { "-r", "-v", "/mnt/sdcard/Download/cds-data" };

        MediaFormatTest.main(args);

        android.util.Log.d("mediainfo-tester","ended\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
