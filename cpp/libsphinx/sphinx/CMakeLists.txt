cmake_minimum_required(VERSION 3.9.3)
project(sphinx C)

find_library(sodium sodium)

function(add_sphinx_library target linkage)
  add_library(${target} ${linkage} src/sphinx.c)
  target_include_directories(${target}
      PUBLIC include
      )
  target_compile_options(${target}
      PUBLIC -fPIC
      PRIVATE -Wall -g -Werror
      )
  target_link_libraries(${target}
      PRIVATE decaf-static
      PRIVATE sodium
      )
  set_target_properties(${target} PROPERTIES C_STANDARD 11)
endfunction()

add_sphinx_library(sphinx SHARED)
add_sphinx_library(sphinx-static STATIC)

add_executable(sphinx-test test/test.c)
target_link_libraries(sphinx-test sphinx)