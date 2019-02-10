cmake_minimum_required(VERSION 3.9.3)
project(opaque C)

find_library(sodium sodium)

function(add_opaque_library target linkage)
  add_library(${target} ${linkage} src/opaque.c)
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

add_opaque_library(opaque SHARED)
add_opaque_library(opaque-static STATIC)