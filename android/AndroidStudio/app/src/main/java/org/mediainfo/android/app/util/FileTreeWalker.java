package org.mediainfo.android.app.util;


import java.io.File;
import java.util.List;

// FIXME:
// If many files exist, this will be very slow.
// Have need to write this asynchronous or cancelable code.

// TODO: Use visitor pattern
//
public class FileTreeWalker {

    protected FileTreeWalker() {
    }

    public FileTreeWalker(List<String> list) {
        mFileList = list;
    }

    public void walk(File sth) {
        if (sth.isFile()) {
            mFileList.add(sth.getAbsolutePath());
        } else if (sth.isDirectory()) {
            // get a listing of all files in the directory
            File[] files = sth.listFiles();

            for (File f : files) {
                walk(f);
            }
        } else {
            // TODO : do something
        }
    }

    private List<String> mFileList;
}
