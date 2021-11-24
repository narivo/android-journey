//
// Created by MadiApps on 27/09/2021.
//

#ifndef LOGGER_H
#define LOGGER_H

#include <android/log.h>

#define  LOG_TAG    "NATIVE_LIB"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //LOGGER_H
