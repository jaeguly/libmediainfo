
### Issues

#### Android와 Ubuntu와 같은 다른 실행 환경과의 다른 점

***태그 정보가 다르게 나오는 경우***

`mbstowcs()` `wcstombs()` `wctomb()` 동작 차이로부터 비롯된다. 가령, ID3 tag 정보를 읽어서 처리하는 경우에 정보 필드에 저장된 encoding 정보에 따라 local encoding으로부터 unicode encoding으로 변환한다. 일반적인 Ubuntu 갈은 실행 환경에서는 사용자가 설정한 local encoding에 따라 이들 함수가 동작하게 되어있지만, android에서는 local encoding 개념을 위한 구현이 없어서 mediainfo를 포팅하면서 cp949로 동작하는 구현을 추가하였다. 이 차이로 인해서 태그 정보가 서로 다르게 나올 수 있게 된다.

그리고 태그 필드 일부에 쓰레기 값이 있는 경우도, encoding에 따라 다르게 동작할 수 있다. 일반적으로 사용되고 있는 utf-8 encoding으로 설정된 환경에서는 `mbstowcs()` 같은 함수들이 invalid char를 만나면 `-1` 반환한다. 그렇게 되면 MediaInfo의 Tag Parser는 특정 필드 정보가 존재하지만 값이 유효하지 않다고 보고 아예 수집을 하지 않는 반면, android에서 운좋게 cp949에는 처리되는 경우가 있어서 깨진 문자열 형태로 정보가 출력될 수도 있다.

*NOTE) bionic에 있는 wchar.cpp 코드는 버그가 있다. `mbsrtowcs()`의 `memcpy`를 사용하는 부분을 참고하길 바란다.*
