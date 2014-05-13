package net.sourceforge.mediainfo

import org.mediainfo.android.MediaInfo;

import java.util.StringTokenizer;

/**
 * @author jaeguly
 */
public class MediaInfoDataBuilder {

    /**
     * 주어진 file path 나 mi 에 문제가 있거나 media parsing이 실패하는 경우 null 를 리턴한다.
     */
    public static MediaInfoData build(MediaInfo mi, String filepath) {

        MediaInfoData data = new MediaInfoData();

        if (data == null || mi == null || mi.open(filepath) == 0)
            return null; // error case

        // Determine a filename, file extension and title.
        //
        String fullfilename = null;
        String filename = null;
        String fileext = null;

        int lastIndex = filepath.lastIndexOf('/');
        if (lastIndex == -1)
            lastIndex = filepath.lastIndexOf('\\');

        if (lastIndex != -1)
            fullfilename = filepath.substring(lastIndex + 1); // next index of slash
        else
            fullfilename = filepath; // file path is same with filename

        int extIndex = fullfilename.lastIndexOf('.');
        if (extIndex != -1) {
            filename = fullfilename.substring(0, extIndex);
            fileext = fullfilename.substring(extIndex + 1);
        } else {
            filename = fullfilename;
            fileext = "";
        }

        String title = getGeneralInfo(mi, "Title");
        if (title.length() > 0) {
            // FIMXE: a bug of mediainfo. check the bug!
            if (filepath.startsWith(title)) {
                title = filename.trim();
            } else {
                title = title.trim();

                if (title.length() < 1)
                    title = filename.trim(); // title has only white-spaces!
            }
        } else {
            title = filename.trim();
        }

        //
        // general part
        //

        // XXX: mediainfo C++ library has a encoding problem from a non-ascii string.
        // So, uses a given 'file path' string.
        data.general_filename = filename; // GeneralInfo("FileName");
        data.general_fileextension = fileext; // GeneralInfo("FileExtension");
        data.general_title = title;
        data.general_album = adjustUnknownString(getGeneralInfo(mi, "Album"));
        data.general_genre = adjustUnknownString(getGeneralInfo(mi, "Genre"));
        data.general_performer = adjustUnknownString(getGeneralInfo(mi, "Performer"));
        data.general_format = getGeneralInfo(mi, "Format");
        data.general_codecid = getGeneralInfo(mi, "CodecID");
        data.general_videocount = parseInt(getGeneralInfo(mi, "VideoCount"));
        data.general_audiocount = parseInt(getGeneralInfo(mi, "AudioCount"));
        data.general_videoformatlist = getGeneralInfo(mi, "Video_Format_List");
        data.general_filesize = parseLong(getGeneralInfo(mi, "FileSize"));
        data.general_duration = parseDouble(getGeneralInfo(mi, "Duration"));
        data.general_overallbitrate = parseLong(getGeneralInfo(mi, "OverallBitRate"));
        data.general_overallmaxbitrate = parseLong(getGeneralInfo(mi, "OverallBitRate_Maximum"));
        data.general_internetmediatype = getGeneralInfo(mi, "InternetMediaType");

        //
        // video information
        //

        data.video_format = getVideoInfo(mi, "Format");

        if (data.video_format.length() > 0) {
            data.video_formatprofile = getVideoInfo(mi, "Format_Profile");
            data.video_codec = getVideoInfo(mi, "Codec/String");
            data.video_bitrate = parseLong(getVideoInfo(mi, "BitRate"));
            data.video_width = parseInt(getVideoInfo(mi, "Width"));
            data.video_height = parseInt(getVideoInfo(mi, "Height"));
            data.video_bitdepth = parseInt(getVideoInfo(mi, "BitDepth"));
            data.video_framerate = parseFloat(getVideoInfo(mi, "FrameRate"));
            data.video_aspectratio = getVideoInfo(mi, "DisplayAspectRatio/String");

            String scanType = getVideoInfo(mi, "ScanType");
            if (scanType.equals("Interlaced"))
                data.video_scantype = 1;
            else if (scanType.equals("Progressive"))
                data.video_scantype = 2;
        }

        //
        // audio information
        //

        data.audio_format = getAudioInfo(mi, "Format");

        if (data.audio_format.length() > 0) {
            data.audio_formatversion = getAudioInfo(mi, "Format_Version");
            data.audio_formatprofile = getAudioInfo(mi, "Format_Profile");
            data.audio_codec = getAudioInfo(mi, "Codec/String");
            data.audio_bitratemode = getAudioInfo(mi, "BitRate_Mode");
            data.audio_bitrate = parseLong(getAudioInfo(mi, "BitRate"));
            data.audio_resolution = parseInt(getAudioInfo(mi, "Resolution"));

            String value = getAudioInfo(mi, "Channels");
            if (value.length() > 0) {
                StringTokenizer miToken = new StringTokenizer(value, "/");
                // gaban 20110119 - 첫번째 channels 값만 사용한다.
                // HE-AACv2 오디오의 경우 mediainfo에서 3개의 channels 정보를 전달한다.
                while (miToken.hasMoreTokens()) {
                    data.audio_channels = parseInt(miToken.nextToken());
                    break;
                }
            }

            value = getAudioInfo(mi, "SamplingRate");
            if (value.length() > 0) {
                StringTokenizer miToken = new StringTokenizer(value, "/");
                // gaban 20110119 - 첫번째 SamplingRate 값만 사용한다.
                // HE-AAC, HE-AACv2 오디오의 경우 mediainfo에서 2, 3개의 samplingrate정보를 전달한다.
                while (miToken.hasMoreTokens()) {
                    data.audio_samplingrate = parseLong(miToken.nextToken());
                    break;
                }
            }
        }

        //
        // image information
        //

        data.image_format = getImageInfo(mi, "Format");
        if (data.image_format.length() > 0) {

            data.image_width = parseInt(getImageInfo(mi, "Width"));
            data.image_height = parseInt(getImageInfo(mi, "Height"));
            data.image_bitdepth = parseInt(getImageInfo(mi, "BitDepth"));
        }

        data.inform_detail = mi.inform();

        mi.close();

        return data;
    }

    private static String adjustUnknownString(String value) {
        if (value.length() > 0)
            return value;

        return UNKNOWN;
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    private static float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    private static String getGeneralInfo(MediaInfo parser, String what) {
        return parser.get(MediaInfo.StreamKind.GENERAL, 0, what);
    }

    private static String getVideoInfo(MediaInfo parser, String what) {
        return parser.get(MediaInfo.StreamKind.VIDEO, 0, what);
    }

    private static String getAudioInfo(MediaInfo parser, String what) {
        return parser.get(MediaInfo.StreamKind.AUDIO, 0, what);
    }

    private static String getImageInfo(MediaInfo parser, String what) {
        return parser.get(MediaInfo.StreamKind.IMAGE, 0, what);
    }

    private static final String UNKNOWN = "unknown";
}
