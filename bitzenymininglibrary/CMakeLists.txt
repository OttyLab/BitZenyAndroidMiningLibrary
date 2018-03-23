cmake_minimum_required(VERSION 3.4.1)

if (${ANDROID_ABI} STREQUAL "x86")
    set(CMAKE_SHARED_LINKER_FLAGS "-Wall -v -Wl,--no-warn-shared-textrel")
endif()

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

