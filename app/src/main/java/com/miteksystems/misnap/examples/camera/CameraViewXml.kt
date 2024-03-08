package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.R
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.camera.view.CameraView
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.MiSnapSettings

/**
 * This example demonstrates how to use a [CameraView] defined in an XML layout to get access to the
 * device's hardware camera and interact with various camera APIs such as preview frames,
 * picture frames, torch controls, focus controls, video recording, etc.
 *
 * @see com.miteksystems.misnap.examples.camera.CameraViewCode for an example on how to use the [CameraView]
 * programmatically.
 */
class CameraViewXml : Fragment(R.layout.example_camera_view_xml) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Get a [CameraView] by the XML element ID and observe the various [LiveData]s to get
         * preview frames, picture frames, recorded videos, camera events etc.
         *
         * See [R.layout.example_camera_view_xml] for the specific XML attributes used to configure
         * the [CameraView] used in this example.
         */
        view.findViewById<CameraView>(R.id.cameraView)
            .also { cameraView ->
                /**
                 * Observe the [CameraView.previewFrames] [LiveData] to handle the camera preview [Frame]s,
                 * e.g. by analyzing them using the [MiSnapController.analyzeFrame] method.
                 *
                 * @see com.miteksystems.misnap.examples.science for examples on how to analyze [Frame]s
                 * with different sciences.
                 */
                cameraView.previewFrames.observe(viewLifecycleOwner) {

                }

                /**
                 * Observe the [CameraView.pictureFrames] [LiveData] to handle the camera pictures
                 * produced by the [CameraView.takePicture] method.
                 */
                cameraView.pictureFrames.observe(viewLifecycleOwner) {

                }

                /**
                 * Observe the [CameraView.cameraEvents] [LiveData] to handle the various camera
                 * related [Event]s.
                 *
                 * @see com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event for the
                 * full list of emitted events.
                 */
                cameraView.cameraEvents.observe(viewLifecycleOwner) { event ->
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
                cameraView.recordedVideo.observe(viewLifecycleOwner) { videoBytes ->

                }
            }
    }
}
