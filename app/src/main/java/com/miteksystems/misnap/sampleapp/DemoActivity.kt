package com.miteksystems.misnap.sampleapp

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.R

class DemoActivity : AppCompatActivity(R.layout.activity_demo) {

    override fun onCreate(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        super.onCreate(savedInstanceState)
    }
}
