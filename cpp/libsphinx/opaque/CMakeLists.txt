cmake_minimum_required(VERSION 3.9.3)
project(opaque C)

set(CMAKE_C_STANDARD 11)

add_library(opaque src/opaque.c)
target_include_directories(opaque
        PUBLIC include
        PUBLIC ${CMAKE_BINARY_DIR}/goldilocks/src/GENERATED/include
        )
target_compile_options(opaque
        PUBLIC  -fPIC
        PRIVATE -Wall -O3 -g)

target_link_libraries(opaque decaf)