#!/bin/bash

#
# Your configurations
#

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

LIBS="-lc"


if [ ! -e $GCC ]; then
	echo "ERROR: gcc not found '$GCC'"	
	exit 1;
fi

if [ ! -e $PLATFORM ]; then
	echo "ERROR: sysroot not found '$PLATFORM'"	
	exit 1;
fi


#
# Build a libzen for android native library.
#
pushd ../ZenLib/Project/GNU/Library
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


#
# Build a libmediainfo for android native library.
#
pushd ../MediaInfoLib/Project/GNU/Library
	autoreconf -i

	CFLAGS="-DWSTRING_MISSING -DNO_EXCEPTIONS $CFLAGS"
	CXXFLAGS="-DWSTRING_MISSING $CXXFLAGS"

	## added stlport
	CXXFLAGS="$CXXFLAGS -I$NDK/sources/cxx-stl/stlport/stlport"
	LDFLAGS="$LDFLAGS -L$NDK/sources/cxx-stl/stlport/libs/armeabi"
	LIBS="$LIBS -lstlport_shared"

	export CC CXX
	export CFLAGS CXXFLAGS LDFLAGS LIBS

	./configure --enable-shared --enable-wstring_missing --host=$HOST

    make clean
    make -j 2
popd


exit

