package org.mediainfo.android.app;

import android.util.Log;

public class MediaInfoData {

	public static final String LOG_TAG = "MediaInfoData";
	
    public String general_title;
    public String general_album;
    public String general_genre;
    public String general_performer;

    public String general_format; // container format
    public String general_filename;
    public long general_filesize;
    public int general_videocount;
    public int general_audiocount;
    public String general_videoformatlist;
    public String general_fileextension;
    public String general_codecid;
    public double general_duration; // micro seconds
    public long general_overallbitrate;
    public long general_overallmaxbitrate;
    public String general_internetmediatype;
    public long general_date;
    // public String general_track;
    // public String general_label;
    // public String general_audioformatlist;
    // public String general_codec;

    public String video_format;
    public String video_formatprofile;
    public String video_codec;
    public long video_bitrate;
    public int video_width;
    public int video_height;
    public float video_framerate;
    public int video_bitdepth;
    public int video_scantype;  // unknown:0 interlaced:1 progressive:2
    public String video_aspectratio;
    // public String video_bitratemode;
    // public String video_colorspace;
    // public String video_chroma;
    // public long video_streamsize;

    public String audio_format;
    public String audio_formatversion;
    public String audio_formatprofile;
    public String audio_codec;
    public String audio_bitratemode;
    public long audio_bitrate;
    public int audio_channels;
    public int audio_resolution;
    public long audio_samplingrate;
    // public String audio_channelpositions;
    // public long audio_streamsize;

    public String image_format;
    public int image_width;
    public int image_height;
    public int image_bitdepth;

    // public String image_chroma;

    public String inform_detail;

    public MediaInfoData() {
        general_filename = "";
        general_fileextension = "";
        general_title = "";
        general_album = "";
        general_genre = "";
        general_performer = "";
        general_format = "";
        general_codecid = "";
        general_videocount = 0;
        general_audiocount = 0;
        general_videoformatlist = "";
        general_filesize = 0;
        general_duration = 0.0;
        general_overallbitrate = 0;
        general_overallmaxbitrate = 0;
        general_internetmediatype = "";
        general_date = 0;

        video_format = "";
        video_formatprofile = "";
        video_codec = "";
        video_bitrate = 0;
        video_bitdepth = 0;
        video_width = 0;
        video_height = 0;
        video_framerate = 0;
        video_aspectratio = "";
        video_scantype = 0;

        audio_format = "";
        audio_formatversion = "";
        audio_formatprofile = "";
        audio_codec = "";
        audio_bitratemode = "";
        audio_bitrate = 0;
        audio_channels = 0;
        audio_resolution = 0;
        audio_samplingrate = 0;

        image_format = "";
        image_width = 0;
        image_height = 0;
        image_bitdepth = 0;
        
        inform_detail = "";
    }

    public String toStringGeneralPart() {
        String str = "---general--------------------------------------------\n";
        str += "FileName:          " + general_filename + "\n";
        str += "FileExtension:     " + general_fileextension + "\n";
        str += "Title:             " + general_title + "\n";
        str += "Album:             " + general_album + "\n";
        str += "Genre:             " + general_genre + "\n";
        str += "Performer:         " + general_performer + "\n";
        str += "Format:            " + general_format + "\n";
        str += "CodecID:           " + general_codecid + "\n";
        str += "VideoCount:        " + general_videocount + "\n";
        str += "AudioCount:        " + general_audiocount + "\n";
        str += "VideoFormatList:   " + general_videoformatlist + "\n";
        str += "FileSize:          " + general_filesize + "\n";
        str += "Duration:          " + general_duration + "\n";
        str += "OverallBitRate:    " + general_overallbitrate + "\n";
        str += "OverallMaxBitRate: " + general_overallmaxbitrate + "\n";
        str += "InternetMediaType: " + general_internetmediatype + "\n";
        str += "Date:              " + general_date + "\n";
        return str;
    }

    public String toStringVideoPart() {
        String str = "---video---------------------------------------------\n";
        str += "Format:            " + video_format + "\n";
        str += "FormatProfile:     " + video_formatprofile + "\n";
        str += "Codec:             " + video_codec + "\n";
        str += "BitRate:           " + video_bitrate + "\n";
        str += "BitDepth:          " + video_bitdepth + "\n";
        str += "Width:             " + video_width + "\n";
        str += "Height:            " + video_height + "\n";
        str += "FrameRate:         " + video_framerate + "\n";
        str += "AspectRatio:       " + video_aspectratio + "\n";
        switch (video_scantype) {
        case 0: // unknowwn
            str += "ScanType:          Unknown\n"; break;
        case 1: // interlaced
            str += "ScanType:          Interlaced\n"; break;
        case 2: // progressive
            str += "ScanType:          Progressive\n"; break;
        }
        return str;
    }

    public String toStringAudioPart() {
        String str = "---audio---------------------------------------------\n";
        str += "Format:            " + audio_format + "\n";
        str += "FormatVersion:     " + audio_formatversion + "\n";
        str += "FormatProfile:     " + audio_formatprofile + "\n";
        str += "Codec:             " + audio_codec + "\n";
        str += "BitRateMode:       " + audio_bitratemode + "\n";
        str += "BitRate:           " + audio_bitrate + "\n";
        str += "Channels:          " + audio_channels + "\n";
        str += "Resolution:        " + audio_resolution + "\n";
        str += "SamplingRate:      " + audio_samplingrate + "\n";
        return str;
    }

    public String toStringImagePart() {
        String str = "---image---------------------------------------------\n";
        str += "Format:            " + image_format + "\n";
        str += "Width:             " + image_width + "\n";
        str += "Height:            " + image_height + "\n";
        str += "BitDepth:          " + image_bitdepth + "\n";
        
        return str;
    }

    public void print() {
        Log.d(LOG_TAG, toStringGeneralPart());
        Log.d(LOG_TAG, toStringVideoPart());
        Log.d(LOG_TAG, toStringAudioPart());
        Log.d(LOG_TAG, toStringImagePart());
    }
}
