cmake_minimum_required(VERSION 3.9.3)
project(sphinx C)

set(CMAKE_C_STANDARD 11)

add_library(sphinx src/sphinx.c)
target_include_directories(sphinx
        PUBLIC include
        PRIVATE ${CMAKE_BINARY_DIR}/goldilocks/src/GENERATED/include
        )
target_compile_options(sphinx
        PUBLIC -fPIE
        PRIVATE -Wall -O3 -g
        )

target_link_libraries(sphinx decaf)