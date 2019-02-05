cmake_minimum_required(VERSION 3.13)
project(sphinx-parent)

set(CMAKE_C_STANDARD 11)

add_subdirectory(opaque)
add_subdirectory(pake)
add_subdirectory(sphinx)
add_subdirectory(goldilocks)