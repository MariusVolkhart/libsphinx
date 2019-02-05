cmake_minimum_required(VERSION 3.13)
project(pake C)

set(CMAKE_C_STANDARD 11)

add_library(pake src/pake.c)
target_include_directories(pake PUBLIC include)

target_link_libraries(pake decaf)