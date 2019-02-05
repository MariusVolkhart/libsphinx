cmake_minimum_required(VERSION 3.9.3)
project(pake C)

set(CMAKE_C_STANDARD 11)

add_library(pake src/pake.c)
target_include_directories(pake
        PUBLIC include
        PUBLIC ${CMAKE_BINARY_DIR}/goldilocks/src/GENERATED/include
        )
target_compile_options(pake
        PUBLIC -fPIC
        PRIVATE -Wall -O3 -g
        )

target_link_libraries(pake decaf)