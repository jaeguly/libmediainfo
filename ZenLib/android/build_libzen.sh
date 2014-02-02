#!/bin/bash


## Your configurations

NDK=/opt/android-ndk
HOST=arm-linux-androideabi
PREBUILT=$NDK/toolchains/${HOST}-4.6/prebuilt/darwin-x86_64
PLATFORM=$NDK/platforms/android-9/arch-arm


GCC=$PREBUILT/bin/${HOST}-gcc
GXX=$PREBUILT/bin/${HOST}-g++

CC="$GCC --sysroot=$PLATFORM"
CXX="$GXX --sysroot=$PLATFORM"

CFLAGS=
CFLAGS="$CFLAGS -fpic -ffunction-sections -funwind-tables -fstack-protector -no-canonical-prefixes"
CFLAGS="$CFLAGS -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64"
CFLAGS="$CFLAGS -fvisibility=hidden -fdata-sections"
CFLAGS="$CFLAGS -I${PLATFORM}/usr/include"
CFLAGS="$CFLAGS -DANDROID"
CFLAGS="$CFLAGS -Wa,--noexecstack -Wformat -Werror=format-security"
CFLAGS="$CFLAGS -march=armv5te -mtune=xscale -msoft-float -mthumb"
CFLAGS="$CFLAGS -Os"

CFLAGS="-g -DNDEBUG $CFLAGS"
CXXFLAGS="-fno-exceptions -fno-rtti $CFLAGS"

LDFLAGS=
LDFLAGS="$LDFLAGS -Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib -nostdlib"
#LDFLAGS="$LDFLAGS $PREBUILT/lib/gcc/arm-linux-androideabi/4.6/crtbegin.o"
#LDFLAGS="$LDFLAGS $PREBUILT/lib/gcc/arm-linux-androideabi/4.6/crtend.o"

LIBS="-lc"


if [ ! -e $GCC ]; then
	echo "ERROR: gcc not found '$GCC'"	
	exit 1;
fi

if [ ! -e $PLATFORM ]; then
	echo "ERROR: sysroot not found '$PLATFORM'"	
	exit 1;
fi



pushd ../Project/GNU/Library
	autoreconf -i

	## added stlport
	CXXFLAGS="$CXXFLAGS -I$NDK/sources/cxx-stl/stlport/stlport"
	LDFLAGS="$LDFLAGS -L$NDK/sources/cxx-stl/stlport/libs/armeabi"
	LIBS="$LIBS -lstlport_shared"

	export CC CXX
	export CFLAGS CXXFLAGS LDFLAGS LIBS

	./configure --enable-shared --enable-wstring_missing --enable-lpthread_missing --host=$HOST

    make clean
    make -j 2
popd


exit

	#./configure --enable-shared --enable-wstring_missing --enable-lpthread_missing --host=$HOST CXXFLAGS="-I$STLINC -fvisibility=hidden -ffunction-sections -fdata-sections -fno-exceptions -fno-rtti -nostdlib" LDFLAGS="-Wl,-rpath-link=$SYSROOT/usr/lib/ -L$SYSROOT/usr/lib/ -L$STLLIB" LIBS="-lc  -lstlport_shared" 
    #./configure --disable-static --enable-shared --host=arm-eabi CXXFLAGS="-I/opt/android-ndk/sources/cxx-stl/stlport/stlport/ -fvisibility=hidden -ffunction-sections -fdata-sections"



CC=/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-gcc
CXX=/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++
CFLAGS
CXXFLAGS

--- flags

-fpic -ffunction-sections -funwind-tables -fstack-protector -no-canonical-prefixes -march=armv5te -mtune=xscale -msoft-float -mthumb -Os -g -DNDEBUG -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 -Ijni -DANDROID  -Wa,--noexecstack -Wformat -Werror=format-security    -I/opt/android-ndk-r9d/platforms/android-9/arch-arm/usr/include 

-fpic -ffunction-sections -funwind-tables -fstack-protector -no-canonical-prefixes -march=armv5te -mtune=xscale -msoft-float -mthumb -Os -g -DNDEBUG -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 -I./  -DANDROID  -Wa,--noexecstack -Wformat -Werror=format-security  -I/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/include

-I/opt/android-ndk-r9d/sources/cxx-stl/stlport/stlport -I/opt/android-ndk-r9d/sources/cxx-stl//gabi++/include 

--- lflags
/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++ -Wl,-soname,libnative-audio-jni.so --sysroot=/opt/android-ndk-r9d/platforms/android-9/arch-arm ./obj/local/armeabi/objs/native-audio-jni/native-audio-jni.o -lgcc -no-canonical-prefixes  -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now  -L/opt/android-ndk-r9d/platforms/android-9/arch-arm/usr/lib -lOpenSLES -llog -landroid -lc -lm -o ./obj/local/armeabi/libnative-audio-jni.so 
-shared
/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++ -Wl,--gc-sections -Wl,-z,nocopyreloc --sysroot=/opt/android-ndk-r9d/platforms/android-3/arch-arm -Wl,-rpath-link=/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/lib -Wl,-rpath-link=./obj/local/armeabi ./obj/local/armeabi/objs/test-libstl/test-libstl.o ./obj/local/armeabi/libstdc++.a -lgcc -no-canonical-prefixes  -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now  -L/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/lib -lstdc++ -lc -lm -o ./obj/local/armeabi/test-libstl

--- 

/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-gcc -MMD -MP -MF ./obj/local/armeabi/objs/native-audio-jni/native-audio-jni.o.d -fpic -ffunction-sections -funwind-tables -fstack-protector -no-canonical-prefixes -march=armv5te -mtune=xscale -msoft-float -mthumb -Os -g -DNDEBUG -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 -Ijni -DANDROID  -Wa,--noexecstack -Wformat -Werror=format-security    -I/opt/android-ndk-r9d/platforms/android-9/arch-arm/usr/include -c  jni/native-audio-jni.c -o ./obj/local/armeabi/objs/native-audio-jni/native-audio-jni.o

/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++ -Wl,-soname,libnative-audio-jni.so -shared --sysroot=/opt/android-ndk-r9d/platforms/android-9/arch-arm ./obj/local/armeabi/objs/native-audio-jni/native-audio-jni.o -lgcc -no-canonical-prefixes  -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now  -L/opt/android-ndk-r9d/platforms/android-9/arch-arm/usr/lib -lOpenSLES -llog -landroid -lc -lm -o ./obj/local/armeabi/libnative-audio-jni.so 

/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-strip --strip-unneeded  ./libs/armeabi/libnative-audio-jni.so


/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++ -MMD -MP -MF ./obj/local/armeabi/objs/test-libstl/test-libstl.o.d -fpic -ffunction-sections -funwind-tables -fstack-protector -no-canonical-prefixes -march=armv5te -mtune=xscale -msoft-float -fno-exceptions -fno-rtti -mthumb -Os -g -DNDEBUG -fomit-frame-pointer -fno-strict-aliasing -finline-limit=64 -I/opt/android-ndk-r9d/sources/cxx-stl/system/include -Ijni -DANDROID  -Wa,--noexecstack -Wformat -Werror=format-security      -I/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/include -c  jni/test-libstl.cpp -o ./obj/local/armeabi/objs/test-libstl/test-libstl.o

/opt/android-ndk-r9d/toolchains/arm-linux-androideabi-4.6/prebuilt/darwin-x86_64/bin/arm-linux-androideabi-g++ -Wl,--gc-sections -Wl,-z,nocopyreloc --sysroot=/opt/android-ndk-r9d/platforms/android-3/arch-arm -Wl,-rpath-link=/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/lib -Wl,-rpath-link=./obj/local/armeabi ./obj/local/armeabi/objs/test-libstl/test-libstl.o ./obj/local/armeabi/libstdc++.a -lgcc -no-canonical-prefixes  -Wl,--no-undefined -Wl,-z,noexecstack -Wl,-z,relro -Wl,-z,now  -L/opt/android-ndk-r9d/platforms/android-3/arch-arm/usr/lib -lstdc++ -lc -lm -o ./obj/local/armeabi/test-libstl

