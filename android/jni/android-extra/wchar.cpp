#include <stdlib.h>
#include <wchar.h>

extern "C" {


extern size_t mbstowcs(wchar_t* wcstr, const char* mbstr, size_t max);
extern size_t wcstombs(char* mbstr, const wchar_t* wcstr, size_t max);
extern int wctomb(char *pmb, wchar_t character);
extern size_t wcslen(const wchar_t *);
extern wchar_t *wcsncpy(wchar_t *, const wchar_t *, size_t);
extern int wcscmp(const wchar_t *, const wchar_t *);
extern size_t wcrtomb(char *, wchar_t, mbstate_t *);



//
// Bellow lines from converters.h
//
typedef unsigned int ucs4_t;
typedef struct conv_struct * conv_t;
typedef struct {
  unsigned short indx; /* index into big table */
  unsigned short used; /* bitmask of used entries */
} Summary16;
/* Return code if invalid input after a shift sequence of n bytes was read.
   (xxx_mbtowc) */
#define RET_SHIFT_ILSEQ(n)  (-1-2*(n))
/* Return code if invalid. (xxx_mbtowc) */
#define RET_ILSEQ           RET_SHIFT_ILSEQ(0)
/* Return code if only a shift sequence of n bytes was read. (xxx_mbtowc) */
#define RET_TOOFEW(n)       (-2-2*(n))
/* Return code if invalid. (xxx_wctomb) */
#define RET_ILUNI      -1
/* Return code if output buffer is too small. (xxx_wctomb, xxx_reset) */
#define RET_TOOSMALL   -2

//#define NULL (0)
#define MB_MAX_LEN      (8)

#include "../../libiconv/lib/ascii.h"
#include "../../libiconv/lib/ksc5601.h"
#include "../../libiconv/lib/cp949.h"

#include <android/log.h>

#define LOG(...)  __android_log_print(ANDROID_LOG_DEBUG, "android-extra", __VA_ARGS__)
//#define LOG(...)  ((void)0)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARN, "android-extra", __VA_ARGS__)



/**
 * Convert multibyte character to wide character.
 * The C multibyte character string @p mbstr is interpreted character by character
 * and translated to its @c wchar_t equivalent, which is stored in the location
 * pointed by @p wcstr. The length in characters of the resulting string, not
 * including the ending null-character, is returned.
 *
 * @param   wcstr Pointer to an array of @c wchar_t elements long enough to store
 *                a wide string @p max characters long.
 * @param   mbstr C multibyte character string to be interpreted.
 * @param   max Maximum number of @c wchar_t characters to be interpreted.
 * @return  The number of characters translated, not including the ending
 *          null-character.If an invalid multibyte character is encountered,
 *          a @c -1 value is returned.
 */
size_t mbstowcs(wchar_t* wcstr, const char* mbstr, size_t max)
{
    LOG("mbstowcs()\n");

    if (!mbstr)
        return (size_t) -1;

	// TODO
	return (size_t) -1;
}

/**
 * Convert wide-character string to multibyte string.
 * The C @c wchar_t string @p wcstr is interpreted character by character and
 * translated to its multibyte equivalent, which is stored in the location
 * pointed by @p mbstr. The length in bytes of the resulting multibyte string,
 * not including the ending null-character, is returned.
 *
 * @param   mbstr Pointer to an array of char elements at least max bytes long.
 * @param   wcstr C @c wchar_t string to be translated.
 * @param   max Maximum number of bytes to be written to @p mbstr. 
 * @return  The number of bytes (not characters) translated and written to
 *          @p mbstr, not including the ending null-character. If an invalid
 *          multibyte character is encountered, a @c -1 value is returned.
 */
size_t wcstombs(char* mbstr, const wchar_t* wcstr, size_t max)
{
    LOG("wcstombs()\n");

    if (!wcstr)
        return (size_t) -1;

	// TODO
    return (size_t) -1;
}

/**
 * Convert wide character to multibyte character.
 * The wide @p character specified as second argument is translated to its
 * multibyte equivalent and stored in the array pointed by @p pmb. The length
 * in bytes of the equivalent multibyte character pointed by @p pmb is returned.
 * @c wctomb has its own internal @p shift state, which is altered as necessary
 * only by calls to this function.
 *
 * @param   pmb Pointer to an array large enough to hold a multibyte character,
 *          which at most is @c MB_CUR_MAX. Alternativelly, the function may be
 *          called with a null pointer, in which case the function resets its
 *          internal shift state to the initial value and returns whether
 *          multibyte characters have state-dependent encodings or not.
 *
 * @param   character Wide character of type @c wchar_t.
 * @return  If the argument passed as @p pmb is not a null pointer, the size in
 *          bytes of the character pointed by @p pmb is returned when it forms
 *          a valid multibyte character and is not the terminating null character.
 *          If it is the terminating null character, the function returns zero,
 *          and in the case they do not form a valid multibyte character,
 *          @c -1 is returned. If the argument passed as @p pmb is a null pointer,
 *          the function returns a nonzero value if multibyte character encodings
 *          are state-dependent, and zero otherwise.
 */
int wctomb(char *pmb, wchar_t character)
{
    LOG("wctomb()\n");

    if (!pmb) {
        LOGW("wctomb(NULL, wchar_t %d) returns %d", character, 0);
        return 0;
    }

	// TODO
	return -1;
}


size_t wcslen(const wchar_t *s)
{
    LOG("wcslen()\n");

    const wchar_t *p = s;

    while (*++p)
        ;

    LOG("wcslen(%s) returns %d (wchar_t is %d bytes)\n", s, (p - s), sizeof(wchar_t));
    return p - s;
}


wchar_t *wcsncpy(wchar_t *dst, const wchar_t *src, size_t n)
{
    LOG("wcsncpy()\n");

    if (n != 0) {
        wchar_t *d = dst;
        const wchar_t *s = src;
        do {
            if ((*d++ = *s++) == L'\0') {
                /* NUL pad the remaining n-1 bytes */
                while (--n != 0)
                    *d++ = L'\0';

                break;
            }
        } while (--n != 0);
    }

    LOG("wcsncpy(dst, %s, %d) returns 0x%u", src, n, (unsigned*)dst);
    return (dst);
}

int wcscmp(const wchar_t *s1, const wchar_t *s2)
{
    if (!s1)
        return s2 ? -1 : 0;

    if (!s2)
        return 1;

    while (*s1 && (*s1 == *s2))
        ++s1, ++s2;

    return (int) (*s1 - *s2);
}

// convert a wide-character code to a character (restartable) 
size_t wcrtomb(char *s, wchar_t wc, mbstate_t *ps)
{
    return wctomb(s, wc);
}


} /* extern "C" */

