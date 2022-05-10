package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.camera.view.CameraView
import com.miteksystems.misnap.core.MiSnapSettings

class CameraViewCode : Fragment() {
    private val license = "your_sdk_license"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
            camera.profile = MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
        }

        return CameraView(requireContext())
            .apply {
                previewFrames.observe(viewLifecycleOwner) {
                    // Preview frames from camera.
                    // Send these to MiSnapController.analyzeFrame()
                    // Please see /examples/science/ for full MiSnapController integration code
                }

                pictureFrames.observe(viewLifecycleOwner) {
                    // Frames from camera for takePicture() call
                }

                cameraEvents.observe(viewLifecycleOwner) { event ->
                    // This is only a subset of events emitted by CameraView.
                    // Please see javadocs for full list of emitted events.
                    when (event) {
                        is Event.CameraInitialized -> {
                            // Camera initialized but not quite ready to use yet
                        }
                        Event.CameraReady -> {
                            // Camera is ready to use now
                        }
                        is Event.InitializationError.InsufficientCamera -> {
                            // Device doesn't have camera with the requested parameters
                        }
                        is Event.InitializationError.CameraNotAvailable -> {
                            // Device doesn't have camera or camera is already in use by other process
                        }
                    }
                }

                recordedVideo.observe(viewLifecycleOwner) { videoBytes ->
                    // non-empty video bytes if recording was requested in MiSnapSettings
                }
            }
            .also { cameraView ->
                // actually start camera preview
                cameraView.startCamera(settings.camera, viewLifecycleOwner) {
                    // camera preview started
                }
            }
    }
}
