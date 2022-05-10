package com.miteksystems.misnap.sampleapp

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes
import com.miteksystems.misnap.BuildConfig
import com.miteksystems.misnap.R

class DemoActivity : AppCompatActivity(R.layout.activity_demo) {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Only used to report crashes in Demo app. Not to be used in production apps
        //noinspection ConstantConditions
        if (BuildConfig.MISNAP_APP_AC_KEY.isNotEmpty()) {
            AppCenter.start(
                application, BuildConfig.MISNAP_APP_AC_KEY,
                Crashes::class.java
            )
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        super.onCreate(savedInstanceState)
    }
}
