cmake_minimum_required(VERSION 3.9.3)
project(jni)

function(add_java_library target libraryToLink sources)

  add_library(${target} SHARED ${sources})
  target_link_libraries(${target} PRIVATE ${libraryToLink})
  set_target_properties(${target} PROPERTIES C_STANDARD 11)
  target_include_directories(${target}
      PUBLIC $ENV{JAVA_HOME}/include
      PRIVATE $<$<PLATFORM_ID:Darwin>:$ENV{JAVA_HOME}/include/darwin> $<$<PLATFORM_ID:Windows>:$ENV{JAVA_HOME}/include/win32> $<$<PLATFORM_ID:Linux>:$ENV{JAVA_HOME}/include/linux>
      )

  if (MSVC)
    set(LIB_PATH $ENV{PKTHIRDPARTY}/lib/${CMAKE_VS_PLATFORM_TOOLSET}/$<IF:$<STREQUAL:$MSVC_CXX_ARCHITECTURE_ID,X86>,Win32,x64>)
    set(BOOST_SUFFIX vc$<IF:$<STREQUAL:$CMAKE_VS_PLATFORM_TOOLSET,v140>,140,141>-mt-s-$<IF:$<STREQUAL:$MSVC_CXX_ARCHITECTURE_ID,X86>,x32,x64>-1_66.lib)
    set(BOOST_SUFFIX_DEBUG vc$<IF:$<STREQUAL:$CMAKE_VS_PLATFORM_TOOLSET,v140>,140,141>-mt-sgd-$<IF:$<STREQUAL:$MSVC_CXX_ARCHITECTURE_ID,X86>,x32,x64>-1_66.lib)

    target_link_libraries(metaClientJni
        PRIVATE ${LIB_PATH}/libboost_thread-${BOOST_SUFFIX}
        PRIVATE ${LIB_PATH}/libboost_date_time-${BOOST_SUFFIX}
        PRIVATE ${LIB_PATH}/libboost_chrono-${BOOST_SUFFIX}
        PRIVATE ${LIB_PATH}/libeay32.lib
        $<$<BOOL:TEST_MODE>:${LIB_PATH}/gtest.lib>
        )
  else ()
    target_compile_options(${target} PRIVATE -fvisibility-inlines-hidden -Wall -Werror -fPIC)
    target_link_libraries(${target} PRIVATE $<$<STREQUAL:${CMAKE_CXX_COMPILER_ID},GNU>:-static-libgcc>)
  endif ()
endfunction()

if (DEFINED ENV{JAVA_HOME})
  message(STATUS "Found JAVA_HOME at $ENV{JAVA_HOME}")

  add_java_library(opaqueJni opaque-static src/NativeOpaque.c)
  add_java_library(pakeJni pake-static src/NativePake.c)
  add_java_library(sphinxJni sphinx-static src/NativeSphinx.c)
else ()
  message(STATUS "Environment variable JAVA_HOME was not found. Skipping JNI build.")
endif ()