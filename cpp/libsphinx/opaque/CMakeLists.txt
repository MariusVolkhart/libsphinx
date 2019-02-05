cmake_minimum_required(VERSION 3.13)
project(opaque C)

set(CMAKE_C_STANDARD 11)

add_library(opaque src/opaque.c)
target_include_directories(opaque PUBLIC include)

target_link_libraries(opaque decaf)