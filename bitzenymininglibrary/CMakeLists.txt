cmake_minimum_required(VERSION 3.4.1)

# Include sub directory
add_subdirectory(${CMAKE_SOURCE_DIR}/src/main/cpp/libcpuminer)

add_library(
    bitzenymininglibrary
    SHARED
    src/main/cpp/bitzenymininglibrary.cpp)

find_library(
    log-lib
    log)

target_link_libraries(
    bitzenymininglibrary
    ${log-lib}
    cpuminer)

