# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

add_library(crypto STATIC IMPORTED)
set_target_properties(crypto PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/openssl-1.1.1n/${ANDROID_ABI}/libcrypto.a")

add_library(ssl STATIC IMPORTED)
set_target_properties(ssl PROPERTIES
        IMPORTED_LOCATION "${CMAKE_SOURCE_DIR}/openssl-1.1.1n/${ANDROID_ABI}/libssl.a")

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/openssl-1.1.1n/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/enigma4/libcryptography/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/enigma4/libnet/include)
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/enigma4/libutil/include)

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.
project("enigma" VERSION 1.0.0)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_subdirectory(enigma4/libcryptography)
add_subdirectory(enigma4/libutil)
add_subdirectory(enigma4/libnet)
add_subdirectory(enigma4/libenigma4-client)


add_library( # Sets the name of the library.
        enigma

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        enigma.cc)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.



target_link_libraries( # Specifies the target library.
        enigma
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        enigma4-client1
        net
        )