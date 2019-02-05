cmake_minimum_required(VERSION 3.9.3)
project(sphinx-parent)

set(CMAKE_C_STANDARD 11)

add_subdirectory(opaque)
add_subdirectory(pake)
add_subdirectory(sphinx)
add_subdirectory(goldilocks)