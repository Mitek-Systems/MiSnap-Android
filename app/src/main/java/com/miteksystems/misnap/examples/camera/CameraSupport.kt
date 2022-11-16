package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.camera.util.CameraUtil
import com.miteksystems.misnap.core.MiSnapSettings

/**
 * This example demonstrates how to query the device's camera support in advance before invoking the
 * MiSnap SDK. This is a recommended best practice for every MiSnap SDK integration as it makes the
 * help screen asset selection faster.
 */
class CameraSupport : Fragment() {
    private val license = "your_sdk_license"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Keep in mind that the camera requirements may be different depending on the [MiSnapSettings.UseCase];
         * a standard face session would evaluate front facing cameras while a document session would
         * evaluate back facing cameras.
         * If you plan to integrate a combined workflow that uses both front and back facing cameras
         * make sure to query the support for both.
         */
        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license)

        // Request the camera support and register a listener to get the results.
        CameraUtil.findSupportedCamera(requireContext(), viewLifecycleOwner, settings.camera) {
            when (it) {
                is CameraUtil.CameraSupportResult.Success -> {
                    // Preset the trigger mode to simplify the help screen asset selection.
                    settings.analysis.document.trigger =
                        if (it.cameraInfo.supportsAutoAnalysis) {
                            MiSnapSettings.Analysis.Document.Trigger.AUTO
                        } else {
                            MiSnapSettings.Analysis.Document.Trigger.MANUAL
                        }
                }
                is CameraUtil.CameraSupportResult.Error -> {
                    /**
                     * The device's camera is not supported for the requested [MiSnapSettings.UseCase].
                     */
                }
            }
        }
    }
}
