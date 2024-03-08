package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.camera.view.CameraView
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.MiSnapSettings

/**
 * This example demonstrates how to programmatically use a [CameraView] to get access to the device's
 * hardware camera and interact with various camera APIs such as preview frames, picture frames, torch
 * controls, focus controls, video recording, etc.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.camera.CameraViewXml for an example on how to use the [CameraView]
 * from an XML layout.
 */
class CameraViewCode : Fragment() {
    private val license = "your_sdk_license"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /**
         * The [CameraView] needs a [MiSnapSettings.Camera.Profile] to select the correct camera which
         * is defined based on the [MiSnapSettings.UseCase], you can also set it explicitly to match your
         * specific needs.
         */
        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
            camera.profile = MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
            analysis.document.trigger = MiSnapSettings.Analysis.Document.Trigger.AUTO
        }

        /**
         * Instantiate a [CameraView] and observe the various [LiveData]s to get preview frames, picture
         * frames, recorded videos, camera events etc.
         */
        return CameraView(requireContext())
            .apply {
                /**
                 * Observe the [CameraView.previewFrames] [LiveData] to handle the camera preview [Frame]s,
                 * e.g. by analyzing them using the [MiSnapController.analyzeFrame] method.
                 *
                 * @see com.miteksystems.misnap.examples.science for examples on how to analyze [Frame]s
                 * with different sciences.
                 */
                previewFrames.observe(viewLifecycleOwner) {

                }

                /**
                 * Observe the [CameraView.pictureFrames] [LiveData] to handle the camera pictures
                 * produced by the [CameraView.takePicture] method.
                 */
                pictureFrames.observe(viewLifecycleOwner) {

                }

                /**
                 * Observe the [CameraView.cameraEvents] [LiveData] to handle the various camera
                 * related [Event]s.
                 *
                 * @see com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event for the
                 * full list of emitted events.
                 */
                cameraEvents.observe(viewLifecycleOwner) { event ->
                    when (event) {
                        is Event.CameraInitialized -> {

                        }
                        is Event.CameraReady -> {
                            //Start to interact with the camera
                        }
                        is Event.InitializationError.InsufficientCamera -> {

                        }
                        is Event.InitializationError.CameraNotAvailable -> {

                        }
                        else -> {
                            //Nothing to do
                        }
                    }
                }

                /**
                 * Observe the [CameraView.recordedVideo] [LiveData] to receive a [ByteArray] with
                 * the contents of the recorded video if requested in [MiSnapSettings].
                 */
                recordedVideo.observe(viewLifecycleOwner) { videoBytes ->

                }
            }
            .also { cameraView ->
                /**
                 * Start the camera preview and register a callback to know when the preview has
                 * started.
                 */
                cameraView.startCamera(settings.camera, viewLifecycleOwner) {

                }
            }
    }
}
