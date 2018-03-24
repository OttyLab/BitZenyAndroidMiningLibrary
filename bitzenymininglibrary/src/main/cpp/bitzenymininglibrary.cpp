#include <jni.h>
#include <string>
#include <android/log.h>
#include "libcpuminer/libcpuminer.h"

#define Log(...) \
    ((void)__android_log_print(ANDROID_LOG_INFO, "native: ", __VA_ARGS__))

JavaVM *g_jvm;
jclass g_class;

extern "C"
void vprintf_app(const char *format, va_list arg) {
    char msg[1024]; //TODO: may cause buffer overflow
    vsprintf(msg, format, arg);

    JNIEnv *env;
    int is_detach_requred = 0;

    g_jvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (!env) {
        g_jvm->AttachCurrentThread(&env, NULL);
        is_detach_requred = 1;
    }

    jstring smsg = env->NewStringUTF(msg);
    jmethodID mid = env->GetStaticMethodID(g_class, "output", "(Ljava/lang/String;)V");
    env->CallStaticVoidMethod(g_class, mid, smsg);

    if (is_detach_requred) {
        g_jvm->DetachCurrentThread();
    }
}

JNIEXPORT jint
JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    Log("JNI_OnLoad");
    g_jvm = vm;

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jclass clazz = env->FindClass("com/example/ottylab/bitzenymininglibrary/BitZenyMiningLibrary");
    g_class = (jclass) env->NewGlobalRef(clazz);

    init(vprintf_app);

    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_ottylab_bitzenymininglibrary_BitZenyMiningLibrary_initMining(
        JNIEnv *env,
        jobject /* this */) {
    Log("initMining");
    return init(vprintf_app);
}

extern "C"
JNIEXPORT jboolean
JNICALL
Java_com_example_ottylab_bitzenymininglibrary_BitZenyMiningLibrary_isMiningRunning(
        JNIEnv *env,
        jobject /* this */) {
    Log("isMiningRunning");
    return (jboolean) is_running();
}

extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_ottylab_bitzenymininglibrary_BitZenyMiningLibrary_startMining(
        JNIEnv *env,
        jobject /* this */,
        jstring url,
        jstring user,
        jstring password,
        jint n_threads) {
    Log("startMining");

    const char *c_url = env->GetStringUTFChars(url, NULL);
    const char *c_user = env->GetStringUTFChars(user, NULL);
    const char *c_password = env->GetStringUTFChars(password, NULL);

    int result = start(c_url, c_user, c_password, n_threads);

    env->ReleaseStringUTFChars(url, c_url);
    env->ReleaseStringUTFChars(user, c_user);
    env->ReleaseStringUTFChars(password, c_password);

    return result;
}
extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_ottylab_bitzenymininglibrary_BitZenyMiningLibrary_startBenchmark(
        JNIEnv *env,
        jobject /* this */,
        jint n_threads) {
    Log("startBenchmark");
    return start(NULL, NULL, NULL, n_threads);
}


extern "C"
JNIEXPORT jint
JNICALL
Java_com_example_ottylab_bitzenymininglibrary_BitZenyMiningLibrary_stopMining(
        JNIEnv *env,
        jobject /* this */) {
    Log("stopMining");
    return stop();
}
