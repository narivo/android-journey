
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("test3d")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wall")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++17 -fno-rtti -Wall")

# Import the ARCore (Google Play Services for AR) library.
add_library(arcore SHARED IMPORTED)
set_target_properties(arcore PROPERTIES IMPORTED_LOCATION
        ${ARCORE_LIBPATH}/${ANDROID_ABI}/libarcore_sdk_c.so
        INTERFACE_INCLUDE_DIRECTORIES ${ARCORE_INCLUDE}
        )

include_directories(include)

file(GLOB SOURCES "*.cpp")

add_library(
        assimp
        SHARED
        IMPORTED)

set_target_properties(
        assimp
        PROPERTIES IMPORTED_LOCATION
        /Users/madiapps/android-native-libs/assimp/${ANDROID_ABI}/libassimp.so)

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
add_library( # Specifies the name of the library.
        test3D

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SOURCES})

# Links your native library against one or more other native libraries.
target_link_libraries( # Specifies the target library.
        test3D
        assimp
        GLESv3
        android
        EGL
        log
        arcore
        m)