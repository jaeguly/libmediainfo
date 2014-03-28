#include <assert.h>
#include <jni.h>

#if defined(WIN32) && defined(_DEBUG)
#   include <vld.h>
#endif

////////////////////////////////////////////////////////////////////////////
//  Platform specific parts
//
////////////////////////////////////////////////////////////////////////////

#if defined(ANDROID)

#   include "MediaInfo/MediaInfo.h"
    using namespace MediaInfoLib;

#   include <android/log.h>

#   define LOGW(...)  __android_log_print(ANDROID_LOG_WARN, "libmediainfo-jni", __VA_ARGS__)
#   define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "libmediainfo-jni", __VA_ARGS__)

#   if defined(_DEBUG) || defined(DEBUG)
#       define LOG(...)   __android_log_print(ANDROID_LOG_DEBUG, "libmediainfo-jni", __VA_ARGS__)
#   else
#       define LOG(...)   ((void)0)
#   endif

#   define FUNC        __func__

#elif defined(_WIN32)

#   include "MediaInfoDLL/MediaInfoDLL.h"
    using namespace MediaInfoDLL;

#   include <stdio.h>

#   define LOGW(...)  printf("[W|libmediainfo-jni] " __VA_ARGS__)
#   define LOGE(...)  printf("[E|libmediainfo-jni] " __VA_ARGS__)

#   if defined(_DEBUG) || defined(DEBUG)
#       define LOG(...)   printf("[D|libmediainfo-jni] " __VA_ARGS__)
#   else
#       define LOG(...)   ((void)0)
#   endif

#   define FUNC        __FUNCTION__

#else

#   error not support target!

#endif

static inline char*
_PrintableChars(const Char* chars, char* buf, size_t buflen)
{
    assert(buf);
    assert(buflen > 0);

    const char* pend = buf + buflen - 1; // 1 for '\0'
    char* p = buf;

    while (*chars && p < pend)
        *p++ = (char) *chars++;

    *p = '\0';

    return buf;
}

static inline char*
PrintableChars(const Char* chars)
{
#if defined(_DEBUG) || defined(DEBUG)
    static char buf[128];
    return _PrintableChars(chars, &buf[0], sizeof(buf)/sizeof(char));
#else
    return "";
#endif
}

static inline char*
PrintableChars2(const Char* chars)
{
#if defined(_DEBUG) || defined(DEBUG)
    static char buf[128];
    return _PrintableChars(chars, &buf[0], sizeof(buf)/sizeof(char));
#else
    return "";
#endif
}


class FuncCallLog {
public:
    FuncCallLog(const char* pszFuncName) {
        _pszFuncName = pszFuncName;
        LOG("%s() is called.\n", pszFuncName);
    }

    ~FuncCallLog() {
        LOG("%s() will be returned.\n", _pszFuncName);
    }

private:
    const char* _pszFuncName;
};


////////////////////////////////////////////////////////////////////////////
//  Declarations of exported functions 
//
////////////////////////////////////////////////////////////////////////////

extern "C" {
    JNIEXPORT jlong   JNICALL MediaInfo_create(JNIEnv* pEnv, jobject self);
    JNIEXPORT void    JNICALL MediaInfo_destroy(JNIEnv* pEnv, jobject self, jlong peer);
    JNIEXPORT jint    JNICALL MediaInfo_open(JNIEnv* pEnv, jobject self, jlong peer, jstring filename);
    JNIEXPORT void    JNICALL MediaInfo_close(JNIEnv* pEnv, jobject self, jlong peer);

    JNIEXPORT jstring JNICALL MediaInfo_getById(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jint parameter);
    JNIEXPORT jstring JNICALL MediaInfo_getByName(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jstring parameter);
    JNIEXPORT jstring JNICALL MediaInfo_getByIdDetail(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jint parameter, jint kindOfInfo);
    JNIEXPORT jstring JNICALL MediaInfo_getByNameDetail(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jstring parameter, jint kindOfInfo, jint kindOfSearch);
#if 0
    JNIEXPORT jstring JNICALL MediaInfo_getOption(JNIEnv* pEnv, jobject self, jlong peer, jstring option);
    JNIEXPORT jint    JNICALL MediaInfo_count(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum);
#endif
}


static const JNINativeMethod gMethods[] = {
    { "create", "()J", (void*) MediaInfo_create},
    { "destroy", "(J)V", (void*)MediaInfo_destroy},
    { "open", "(JLjava/lang/String;)I", (void*)MediaInfo_open},
    { "close", "(J)V", (void*)MediaInfo_close},
    { "getById", "(JIII)Ljava/lang/String;", (void*)MediaInfo_getById},
    { "getByName", "(JIILjava/lang/String;)Ljava/lang/String;", (void*)MediaInfo_getByName},
    { "getByIdDetail", "(JIIII)Ljava/lang/String;", (void*)MediaInfo_getByIdDetail},
    { "getByNameDetail", "(JIILjava/lang/String;II)Ljava/lang/String;", (void*)MediaInfo_getByNameDetail},
};

JNIEXPORT jint JNICALL
JNI_OnLoad (JavaVM * vm, void * reserved)
{
    FuncCallLog funclog(FUNC);

    JNIEnv *env;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) == JNI_OK) {
        LOG("JNI_VERSION_1_6 OK!\n");
    } else if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) == JNI_OK) {
        LOGW("JNI_VERSION_1_4 OK!\n");
    } else if (vm->GetEnv((void**) &env, JNI_VERSION_1_2) == JNI_OK) {
        LOGW("JNI_VERSION_1_2 OK!\n");
    } else {
        LOGE("JNI_VERSION_?_? FAIL!\n");
        return -1;
    }

    static const char* const kClassName = "net/sourceforge/mediainfo/MediaInfo";
    LOG("Registering %s natives\n", kClassName);

    jclass clazz = env->FindClass(kClassName);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'!\n", kClassName);
        return -1;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods)/sizeof(gMethods[0])) < 0) {
        LOGE("env->RegisterNatives)() fails for '%s'!\n", kClassName);
        env->DeleteLocalRef(clazz);
        return -1;
    }

    env->DeleteLocalRef(clazz);

    return JNI_VERSION_1_6;
} 


////////////////////////////////////////////////////////////////////////////
//  Internal implementations
//
////////////////////////////////////////////////////////////////////////////

static inline MediaInfo*
GetMediaInfo(jlong peer)
{
    if (!peer)
        LOGW("The mediainfo handle is invalid!\n");

    return (MediaInfo*) peer;
}

static inline const Char*
CastChars(const jchar* chars)
{
    //assert(sizeof(jchar) == sizeof(Char));

    return (const Char*) chars;
}

static inline const jchar*
CastChars(const Char* chars)
{
    //assert(sizeof(jchar) == sizeof(Char));

    return (const jchar*) chars;
}

static inline stream_t
CastStreamKind(jint streamKind)
{
    assert(streamKind < Stream_Max);

    return (stream_t) streamKind;
}

static inline info_t
CastInfoKind(jint infoKind)
{
    assert(infoKind < Info_Max);

    return (info_t) infoKind;
}

static inline jstring
NewJString(JNIEnv *pEnv, String str)
{
    jstring jstr = pEnv->NewString(CastChars(str.c_str()), str.size());

    if (!jstr)
        LOGW("env->NewString('%s', %d) fails!\n", PrintableChars(str.c_str()), str.size());

    return jstr;
}

class JStringHolder {
public:
    JStringHolder(JNIEnv* env, jstring jstr) : _env(env), _jstr(jstr), _jchars(NULL) {
        assert(env);
        assert(jstr);
    };

    ~JStringHolder() {
        if (_jchars)
            _env->ReleaseStringChars(_jstr, _jchars);
    }

    bool toString(String& str) {
        if (!_jchars) {
            _jchars = _env->GetStringChars(_jstr, NULL);
            if (_jchars == NULL) {
                LOGW("env->GetStringChars() fails!\n");
                return false;
            }
        }

        //
        // convert a wchar_t(maybe 4bytes) strings from a jstring(maybe 2bytes)
        //
        int jchars_len = _env->GetStringLength(_jstr);

        // allocate a actually buffer and set a given length
        str.resize(jchars_len);

        Char *cstr = (Char*) str.c_str();

        while (jchars_len-- > 0)
            *cstr++ = (Char) *_jchars++;

        return true;
    }

private:
    JNIEnv*       _env;
    jstring       _jstr;
    const jchar*  _jchars;
};


////////////////////////////////////////////////////////////////////////////
//  Implementations of exported functions
//
////////////////////////////////////////////////////////////////////////////


JNIEXPORT jlong JNICALL
MediaInfo_create(JNIEnv* pEnv, jobject self)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = new MediaInfo();
    if (!pMediaInfo)
        LOGW("MediaInfo->New() fails!\n");
//    else
//        LOGW("MediaInfo->New() ok!\n");

    return (jlong) pMediaInfo;
}

JNIEXPORT void JNICALL
MediaInfo_destroy(JNIEnv* pEnv, jobject self, jlong peer)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);

    if (pMediaInfo) {
        delete pMediaInfo;
//        LOGW("MediaInfo->Destroy() ok!\n");
    } else {
        LOGW("MediaInfo->Destroy() skip!\n");
    }
}

JNIEXPORT jint JNICALL
MediaInfo_open(JNIEnv* pEnv, jobject self, jlong peer, jstring filename)
{
    LOG("Char(%d bytes) wchar_t(%d bytes)\n", sizeof(Char), sizeof(wchar_t));
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (!pMediaInfo)
        return 0;   // error

    String strFilename;
    JStringHolder jstrHolder(pEnv, filename);
    if (!jstrHolder.toString(strFilename))
        return 0;

	// TEST
    //size_t res = pMediaInfo->Open(strFilename);
    size_t res = pMediaInfo->Open(L"/mnt/sdcard/Download/test/test-part1.avi");
    LOG("MediaInfo->Open('%s') returns %d\n", PrintableChars(strFilename.c_str()), res);

    if (res == 0)
        LOGW("MediaInfo->Open() fails!\n");

    return (jint) res;
}

JNIEXPORT void JNICALL
MediaInfo_close(JNIEnv* pEnv, jobject self, jlong peer)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (pMediaInfo)
        pMediaInfo->Close();
}

JNIEXPORT jstring JNICALL
MediaInfo_getById(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jint parameter)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (!pMediaInfo)
        return 0;

    String strInfo = pMediaInfo->Get(CastStreamKind(streamKind), streamNum, parameter);
    LOG("MediaInfo->Get(%d,%d,%d) returns '%s'\n",
        CastStreamKind(streamKind), streamNum, parameter, PrintableChars(strInfo.c_str()));

    return NewJString(pEnv, strInfo);
}

JNIEXPORT jstring JNICALL
MediaInfo_getByName(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jstring parameter)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (!pMediaInfo)
        return 0;

    String strParameter;
    JStringHolder jstrHolder(pEnv, parameter);
    if (!jstrHolder.toString(strParameter))
        return 0;

    String strInfo = pMediaInfo->Get(CastStreamKind(streamKind), streamNum, strParameter);
    LOG("MediaInfo->Get(%d,%d,'%s') returns '%s'\n",
        CastStreamKind(streamKind), streamNum, PrintableChars(strParameter.c_str()), PrintableChars2(strInfo.c_str()));

    return NewJString(pEnv, strInfo);
}

JNIEXPORT jstring JNICALL
MediaInfo_getByIdDetail(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jint parameter, jint kindOfInfo)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (!pMediaInfo)
        return 0;

    String strInfo = pMediaInfo->Get(CastStreamKind(streamKind), streamNum, parameter,
                                     CastInfoKind(kindOfInfo));
    LOG("MediaInfo->Get(%d,%d,%d,%d) returns '%s'\n",
        CastStreamKind(streamKind), streamNum, parameter, kindOfInfo, PrintableChars(strInfo.c_str()));

    return NewJString(pEnv, strInfo);
}

JNIEXPORT jstring JNICALL
MediaInfo_getByNameDetail(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum, jstring parameter, jint kindOfInfo, jint kindOfSearch)
{
    FuncCallLog funclog(FUNC);

    MediaInfo* pMediaInfo = GetMediaInfo(peer);
    if (!pMediaInfo)
        return 0;

    String strParameter;
    JStringHolder jstrHolder(pEnv, parameter);
    if (!jstrHolder.toString(strParameter))
        return 0;

    String strInfo = pMediaInfo->Get(CastStreamKind(streamKind), streamNum, strParameter,
                                     CastInfoKind(kindOfInfo), CastInfoKind(kindOfSearch));

    LOG("MediaInfo->Get(%d,%d,'%s',%d,%d) returns '%s'\n",
        CastStreamKind(streamKind), streamNum, PrintableChars(strParameter.c_str()),
        CastInfoKind(kindOfInfo), CastInfoKind(kindOfSearch), PrintableChars2(strInfo.c_str()));

    return NewJString(pEnv, strInfo);
}




// comment-out by jaeguly for unused functions
#if 0

JNIEXPORT jstring JNICALL
MediaInfo_getOption(JNIEnv* pEnv, jobject self, jlong peer, jstring option)
{
    MediaInfo* pMediaInfo = GetMediaInfo(peer);

    if (!pMediaInfo)
        return 0;

    const jchar* jchars = pEnv->GetStringChars(option, NULL);
    if (jchars == NULL) {
        LOGW("GetStringChars() fails.\n");
        return 0;
    }

    String strOption;
    strOption.assign(CastChars(jchars), pEnv->GetStringLength(option));

    LOG("called __getOption('%s')\n", PrintableChars(strOption.c_str()));

    String strInfo = pMediaInfo->Option(strOption);

    LOG("__getOption() returns '%s'.\n", PrintableChars(strInfo.c_str()));

    jstring res = pEnv->NewString(CastChars(strInfo.c_str()), strInfo.size());

    pEnv->ReleaseStringChars(option, jchars);

    return res;

}

JNIEXPORT jint JNICALL
MediaInfo_count(JNIEnv* pEnv, jobject self, jlong peer, jint streamKind, jint streamNum)
{
    MediaInfo* pMediaInfo = GetMediaInfo(peer);

    if (!pMediaInfo)
        return 0;

    LOG("called __count(%d,%d)\n", streamKind, streamNum);

    return pMediaInfo->Count_Get(CastStreamKind(streamKind), (size_t) streamNum);
}

#endif /* 0 */
