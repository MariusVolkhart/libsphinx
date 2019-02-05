cmake_minimum_required(VERSION 3.13)
project(sphinx C)

set(CMAKE_C_STANDARD 11)

add_library(sphinx src/sphinx.c)
target_include_directories(sphinx PUBLIC include)

target_link_libraries(sphinx decaf)