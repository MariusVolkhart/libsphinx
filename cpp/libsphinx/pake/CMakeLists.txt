cmake_minimum_required(VERSION 3.9.3)
project(pake C)

find_library(sodium sodium)

function(add_pake_library target linkage)
  add_library(${target} ${linkage} src/pake.c)
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

add_pake_library(pake SHARED)
add_pake_library(pake-static STATIC)