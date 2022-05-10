package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.camera.util.CameraUtil
import com.miteksystems.misnap.core.MiSnapSettings

/**
 * Demonstrates how to pre query the device's camera support.
 */
class CameraSupport : Fragment() {
    private val license = "your_sdk_license"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license)

        CameraUtil.findSupportedCamera(requireContext(), viewLifecycleOwner, settings.camera) {
            when (it) {
                is CameraUtil.CameraSupportResult.Success -> {
                    // Preset the trigger mode to smooth over help screen asset selection
                    settings.analysis.document.trigger =
                        if (it.cameraInfo.supportsAutoAnalysis) {
                            MiSnapSettings.Analysis.Document.Trigger.AUTO
                        } else {
                            MiSnapSettings.Analysis.Document.Trigger.MANUAL
                        }
                }
                is CameraUtil.CameraSupportResult.Error -> {
                    // Device's camera is not supported
                }
            }
        }
    }
}