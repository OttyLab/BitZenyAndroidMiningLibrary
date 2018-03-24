cmake_minimum_required(VERSION 3.4.1)

enable_language(ASM)

set(CMAKE_ASM_FLAGS "-g -O2")
set(CMAKE_C_FLAGS " ${CMAKE_C_FLAGS} -g -O2")

add_definitions(
    -D__STDC_LIMIT_MACROS
    -D__STDC_CONSTANT_MACROS
    -D__linux
)

add_library(
    cpuminer
    STATIC

    scrypt.c
    scrypt-arm.S
    scrypt-x64.S
    scrypt-x86.S
    sha2.c
    sha2-arm.S
    sha2-x64.S
    sha2-x86.S
    util.c
    yescrypt.c
    libcpuminer.c
)


# Prebuilt libraries

if (${TARGET} STREQUAL "KITKAT")
    add_library(z STATIC IMPORTED)

    set_target_properties(
        z
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/zlib/lib19/${ANDROID_ABI}/libz.a
    )

    add_library(curl STATIC IMPORTED)

    set_target_properties(
        curl
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/curl/lib19/${ANDROID_ABI}/libcurl.a
    )

    add_library(jansson STATIC IMPORTED)

    set_target_properties(
        jansson
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/jansson/lib19/${ANDROID_ABI}/libjansson.a
    )
else()
    add_library(z STATIC IMPORTED)

    set_target_properties(
        z
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/zlib/lib/${ANDROID_ABI}/libz.a
    )

    add_library(curl STATIC IMPORTED)

    set_target_properties(
        curl
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/curl/lib/${ANDROID_ABI}/libcurl.a
    )

    add_library(jansson STATIC IMPORTED)

    set_target_properties(
        jansson
        PROPERTIES
        IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../distribution/jansson/lib/${ANDROID_ABI}/libjansson.a
    )
endif()

target_include_directories(
    cpuminer PRIVATE
    ${CMAKE_SOURCE_DIR}/../distribution/zlib/include
    ${CMAKE_SOURCE_DIR}/../distribution/curl/include
    ${CMAKE_SOURCE_DIR}/../distribution/jansson/include)

target_link_libraries(
    cpuminer
    curl
    z
    jansson
)
