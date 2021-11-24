package com.madiapps.test3d

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.hardware.display.DisplayManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity(), GLSurfaceView.Renderer, DisplayManager.DisplayListener {

    lateinit var surfacevw: ARView

    private external fun nativeDrawFrame()
    private external fun nativeSurfaceCreated()
    private external fun nativeSurfaceChanged(displayRotation: Int, w: Int, h: Int)

    private external fun nativeActivityPause()
    private external fun nativeActivityResume()

    private external fun loadAssets(assetManager: AssetManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfacevw = ARView(this)

        // Check if the system supports OpenGL ES 3.0.
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val supportsEs3 = activityManager.deviceConfigurationInfo.reqGlEsVersion >= 0x30000

        if(checkPermissions(permissions)){
            // Permission is not granted
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE)
        } else {
            permissionGranted = true
        }

        if(permissionGranted) {
            loadAssets(assets)
        }

        if (supportsEs3) {
            // Request an OpenGL ES 3.0 compatible context.
            surfacevw.preserveEGLContextOnPause = true
            surfacevw.setEGLContextClientVersion(3)
            surfacevw.setEGLConfigChooser(8, 8, 8, 8, 16, 0) // Alpha used for plane blending.
            surfacevw.setRenderer(this)
            surfacevw.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            surfacevw.setWillNotDraw(false)
        } else {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e(TAG, "Do not support ES3")
            return
        }

        setContentView(surfacevw)
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        var perm = true
        for (permission in permissions) {
            perm = perm && ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }
        return perm
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            permissionGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Write Permission Granted", Toast.LENGTH_SHORT).show()
                loadAssets(assets)
                nativeActivityResume()
                surfacevw.onResume()
                true
            } else {
                Toast.makeText(this, "Write Permission Denied", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    companion object {
        private const val PERMISSION_CODE = 568
        private val TAG = MainActivity::class.java.simpleName
        var permissionGranted = false
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

        init {
            System.loadLibrary("test3D")
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionGranted) {
            nativeActivityResume()
            surfacevw.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        surfacevw.onPause()
        nativeActivityPause()
    }

    // ===================================================== //
    //                      renderer                         //
    // ===================================================== //
    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        if(viewportChanged) {
            nativeSurfaceChanged(windowManager.defaultDisplay.rotation, viewportWidth, viewportHeight)
            viewportChanged = false
        }
        nativeSurfaceCreated()
    }

    var viewportWidth = 1
    var viewportHeight = 1

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        nativeSurfaceChanged(windowManager.defaultDisplay.rotation, p1, p2)
        viewportWidth = p1
        viewportHeight = p2
        viewportChanged = true
    }

    override fun onDrawFrame(p0: GL10?) {
        nativeDrawFrame()
    }

    override fun onDisplayAdded(p0: Int) {
    }

    override fun onDisplayRemoved(p0: Int) {
    }

    var viewportChanged = false
    override fun onDisplayChanged(p0: Int) {
        viewportChanged = true
    }
}


class ARView(ctx: Context) : GLSurfaceView(ctx), GestureDetector.OnGestureListener {

    companion object {
        private val TAG = ARView::class.java.simpleName
        init {
            System.loadLibrary("test3D")
        }
    }

    private external fun nativeRotation(eventX: Float, eventY: Float)

    var gestureDetector: GestureDetectorCompat = GestureDetectorCompat(ctx, this)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDown(event: MotionEvent): Boolean {
        Log.d(TAG, "onDown: $event")
        return true
    }

    override fun onShowPress(event: MotionEvent?) {
        Log.d(TAG, "onShowPress: $event")
    }

    override fun onSingleTapUp(event: MotionEvent?): Boolean {
        Log.d(TAG, "onSingleTapUp: $event")
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        Log.d(TAG, "onScroll: $e1 $e2")
        nativeRotation(distanceX, distanceY)
        return true
    }

    override fun onLongPress(event: MotionEvent?) {
        Log.d(TAG, "onLongPress: $event")
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        Log.d(TAG, "onFling: $e1 $e2")
        return true
    }

}