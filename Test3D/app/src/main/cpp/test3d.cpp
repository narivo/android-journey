#include "jni.h"

#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>

#include "shader.h"
#include "asset_extractor.h"
#include <optional>
#include <util.h>
#include "logger.h"

#include <unordered_map>

#include <model.h>

#include "arcore_c_api.h"
#include "viewfinder_renderer.h"
#include "object_renderer.h"
//
// Created by MadiApps on 27/09/2021.
//

AAssetManager *asset_manager_ = NULL;

ArSession *ar_session_ = NULL;
ArFrame *ar_frame_ = NULL;

int display_rotation_ = 0;
int width_ = 1;
int height_ = 1;

constexpr bool kUseSingleImage = false;

ViewFinder viewFinder;
ObjectRenderer objectRenderer;

ArAugmentedImageDatabase *CreateAugmentedImageDatabase() {
    ArAugmentedImageDatabase *ar_augmented_image_database = nullptr;
    // There are two ways to configure a ArAugmentedImageDatabase:
    // 1. Add Bitmap to DB directly
    // 2. Load a pre-built AugmentedImageDatabase
    // Option 2) has
    // * shorter setup time
    // * doesn't require images to be packaged in apk.
    if (kUseSingleImage) {

    } else {
        std::string database_buffer;
        LoadFileFromAssetManager(asset_manager_, "sample_database.imgdb",
                                 &database_buffer);

        uint8_t *raw_buffer = reinterpret_cast<uint8_t *>(&database_buffer.front());
        const ArStatus status = ArAugmentedImageDatabase_deserialize(
                ar_session_, raw_buffer, database_buffer.size(),
                &ar_augmented_image_database);
        CHECK(status == AR_SUCCESS);
    }

    return ar_augmented_image_database;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_loadAssets(JNIEnv *env, jobject thiz,
                                               jobject asset_manager) {
    asset_manager_ = AAssetManager_fromJava(env, asset_manager);
    AssetExtractor extractor(env, thiz, asset_manager);
    extractor.ExtractToCache();
    stbi_set_flip_vertically_on_load(true);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_nativeSurfaceCreated(JNIEnv *env, jobject thiz) {
    viewFinder = ViewFinder(ar_session_, ar_frame_);
    viewFinder.Prepare();

    objectRenderer = ObjectRenderer(ar_session_, ar_frame_,
                                    "vertex.glsl", "fragment.glsl",
                                    "ballerina/Ballerina_chapeau.dae");

    objectRenderer.animator.m_CurrentAnimation = &objectRenderer.danceAnimation;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_nativeDrawFrame(JNIEnv *env, jobject thiz) {

// ============================================================ //
//                       OpenGL options                         //
// ============================================================ //
    glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    glEnable(GL_CULL_FACE);
    glEnable(GL_DEPTH_TEST);
    glEnable(GL_BLEND);

// Textures are loaded with premultiplied alpha
// (https://developer.android.com/reference/android/graphics/BitmapFactory.Options#inPremultiplied),
// so we use the premultiplied alpha blend factors.
    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

// ============================================================ //
//                  ARCore rendering capture                    //
// ============================================================ //
    if (ar_session_ == nullptr) return;

// Update session to get current frame and render camera background.
    if (ArSession_update(ar_session_, ar_frame_) != AR_SUCCESS) {
        LOGE("AugmentedImageApplication::OnDrawFrame ArSession_update error");
    }

// ============================================================ //
//                 Camera ViewFinder picture                    //
// ============================================================ //
    viewFinder.Draw();

// ============================================================ //
//                     3D augmented Image                       //
// ============================================================ //
    objectRenderer.Draw();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_nativeSurfaceChanged(JNIEnv *env, jobject thiz,
                                                         jint display_rotation, jint w, jint h) {
    viewFinder.Change(display_rotation, w, h);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_nativeActivityPause(JNIEnv *env, jobject thiz) {
    if (ar_session_ != nullptr) {
        ArSession_pause(ar_session_);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_MainActivity_nativeActivityResume(JNIEnv *env, jobject thiz) {

// ================================================= //
//                  create session                   //
// ================================================= //
    if (ar_session_ == NULL) {
        CHECKANDTHROW(ArSession_create(env, thiz, &ar_session_) == AR_SUCCESS, env,
                      "Failed to create AR session.");
        ArConfig *ar_config = NULL;
        ArConfig_create(ar_session_, &ar_config);
        CHECK(ar_config);

        ArAugmentedImageDatabase *ar_augmented_image_database = CreateAugmentedImageDatabase();
        ArConfig_setAugmentedImageDatabase(ar_session_, ar_config, ar_augmented_image_database);

        ArConfig_setFocusMode(ar_session_, ar_config, AR_FOCUS_MODE_AUTO);
        CHECKANDTHROW(ArSession_configure(ar_session_, ar_config) == AR_SUCCESS, env,
                      "Failed to configure AR session");

        ArAugmentedImageDatabase_destroy(ar_augmented_image_database);
        ArConfig_destroy(ar_config);

        ArFrame_create(ar_session_, &ar_frame_);

        ArSession_setDisplayGeometry(ar_session_, display_rotation_, width_, height_);
    }

    const ArStatus status = ArSession_resume(ar_session_);
    CHECKANDTHROW(status == AR_SUCCESS, env, "Failed to resume AR session.");
}
extern "C"
JNIEXPORT void JNICALL
Java_com_madiapps_test3d_ARView_nativeRotation(JNIEnv *env, jobject thiz,
                                               jfloat event_x, jfloat event_y) {
    objectRenderer.addAngle(-event_x);
}