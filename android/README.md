### License

Read `../MediaInfoLib/License.html`

### Issues

#### Android와 Ubuntu와 같은 다른 실행 환경과의 다른 점

***태그 정보가 다르게 나오는 경우***

`mbstowcs()` `wcstombs()` `wctomb()` 동작 차이로부터 비롯된다. 가령, ID3 tag 정보를 읽어서 처리하는 경우에 정보 필드에 저장된 encoding 정보에 따라 local encoding으로부터 unicode encoding으로 변환한다. 일반적인 Ubuntu 갈은 실행 환경에서는 사용자가 설정한 local encoding에 따라 이들 함수가 동작하게 되어있지만, android에서는 local encoding 개념을 위한 구현이 없어서 mediainfo를 포팅하면서 cp949로 동작하는 구현을 추가하였다. 이 차이로 인해서 태그 정보가 서로 다르게 나올 수 있게 된다.

그리고 태그 필드 일부에 쓰레기 값이 있는 경우도, encoding에 따라 다르게 동작할 수 있다. 일반적으로 사용되고 있는 utf-8 encoding으로 설정된 환경에서는 `mbstowcs()` 같은 함수들이 invalid char를 만나면 `-1` 반환한다. 그렇게 되면 MediaInfo의 Tag Parser는 특정 필드 정보가 존재하지만 값이 유효하지 않다고 보고 아예 수집을 하지 않는 반면, android에서 운좋게 cp949에는 처리되는 경우가 있어서 깨진 문자열 형태로 정보가 출력될 수도 있다.

*NOTE) bionic에 있는 wchar.cpp 코드는 버그가 있다. `mbsrtowcs()`의 `memcpy`를 사용하는 부분을 참고하길 바란다.*

#### ostream::operator<<() problem

```
typedef basic_ostringstream<wchar_t> xStringStream
typedef signed char int8s;
...
xStringStream stream;
int8s n = 33;
stream << n;
```

Android ndk에 패키징되어있는 arm-gcc와 stlport을 이용해서 컴파일하게 되면, `int8s`에 대한 처리가 다르게 이루어진다. 코드는 *n* 이 숫자형으로 처리되길 기대하였으나 android compilation 환경에서는 *n* 을 문자형으로 간주하여 문자가 입력된 것으로 처리한다.

이렇게 된 이유로는 stlport가 wchar_t에 대한 구현이 미흡하기 때문이다. `char`, `singed char`, `unsigned char`에 관한 각각의 처리를 해주는 구현이 있으면 되는데 `basic_ostream<char>`와 달리 `basic_ostream<wchar_t>` 구현이 누락되었고 그러한 이유로 *implicit cast* 으로 `_Self&
basic_ostream:: operator<<(unsigned char __x)`가 수행되었다.

즉, *android-ndk/sources/cxx-stl/stlport/stlport/stl/_ostream.h* 헤더에 최소한 아래의 구현이 추가되어야한다.

```
template <class _CharT, class _Traits>
inline basic_ostream<_CharT, _Traits>& _STLP_CALL
operator<<(basic_ostream<_CharT, _Traits>& __os, singed char __c) {
  __os._M_put_char(__os.widen(__c));
  return __os;
}

template <class _CharT, class _Traits>
inline basic_ostream<_CharT, _Traits>& _STLP_CALL
operator<<(basic_ostream<_CharT, _Traits>& __os, unsinged char __c) {
  __os._M_put_char(__os.widen(__c));
  return __os;
}
```

그러나 이렇게 NDK를 수정해서 사용하게 되면 (일단은) 안드로이드용으로 mediainfo를 개발하는 환경마다 patch를 가해야하는 불편함이 있어서 아래와 같이 이 문제를 피해갈 수 있게 수정하였다.

```
stream << (signed int) n;
```
