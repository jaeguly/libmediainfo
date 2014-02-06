package net.sourceforge.mediainfo;


/**
 * Give information about a lot of media format.
 */
public class MediaInfo {

    // @remark Don't change it carelessly.
    // This order is from MediaInfo_Const.h
    public enum StreamKind {
        GENERAL,
        VIDEO,
        AUDIO,
        TEXT,
        CHAPTERS,
        IMAGE,
        MENU
    }
    
    // @remark Don't change it carelessly.
    // This order is from MediaInfo_Const.h
    public enum InfoKind {
        NAME,           // Unique name of parameter
        TEXT,           // Value of parameter
        MEASURE,        // Unique name of measure unit of parameter
        OPTIONS,        // See InfoOptionKind
        NAME_TEXT,      // Translated name of parameter
        MEASURE_TEXT,   // Translated name of measure unit
        INFO,           // More information about the parameter
        HOWTO,          // How this parameter is supported, could be N(No), B(Beta), R(Read only),
                        // W (Read/Write)
        DOMAIN          // Domain of this piece of information
    }

    public MediaInfo() {
        peer = create();
    }

    public void destroy() {
        if (peer != 0) {
            destroy(peer);
            peer = 0;
        }
    }

    //
    // private, protected, static
    //
    
    private long peer;

    private native long create();

    private native void destroy(long peer);

    private native int open(long peer, String filename);

    private native void close(long peer);

    private native String getById(long peer, int streamKind, int streamNum, int parameter);

    private native String getByIdDetail(long peer, int streamKind, int streamNum, int parameter,
            int kindOfInfo);

    private native String getByName(long peer, int streamKind, int streamNum, String parameter);

    private native String getByNameDetail(long peer, int streamKind, int streamNum, String parameter,
            int kindOfInfo, int kindOfSearch);

//    private native String getOption(long peer, String option);

//    private native int count(long peer, int streamKind, int streamNum);


    protected void finalize() {
        destroy();
    }

    static {
        try {
            System.loadLibrary("mediainfo");
        } catch (Exception e) {
            System.out.println("can't load mediainfo libraries!");
        }
    }
}

