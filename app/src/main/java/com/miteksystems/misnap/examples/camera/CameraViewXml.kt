package com.miteksystems.misnap.examples.camera

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.camera.view.CameraView
import com.miteksystems.misnap.R

class CameraViewXml : Fragment(R.layout.example_camera_view_xml) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<CameraView>(R.id.cameraView)
            .also { cameraView ->
                cameraView.previewFrames.observe(viewLifecycleOwner) {
                    // Preview frames from camera.
                    // Send these to MiSnapController.analyzeFrame()
                    // Please see /examples/science/ for full MiSnapController integration code
                }

                cameraView.pictureFrames.observe(viewLifecycleOwner) {
                    // Frames from camera for takePicture() call
                }

                cameraView.cameraEvents.observe(viewLifecycleOwner) { event ->
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

                cameraView.recordedVideo.observe(viewLifecycleOwner) { videoBytes ->
                    // non-empty video bytes if recording was requested in MiSnapSettings
                }
            }
    }
}
