package com.miteksystems.misnap.examples.misnapview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.controller.MiSnapController.FrameResult
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.view.MiSnapView
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MibiData

class MiSnapViewXml : Fragment(R.layout.example_misnap_view_xml) {
    private val license = "your_sdk_license"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license)

        // Optionally define a Mibi session outside of the view's lifecycle
        MibiData.startSession(this::class.java.name, settings)

        val misnapView = view.findViewById<MiSnapView>(R.id.misnapView)
            .also { misnapView ->
                misnapView.feedbackResult.observe(viewLifecycleOwner) { result ->
                    // Show hints corresponding to result.userAction
                    // Optionally show detected document corners using result.fourCorners
                    // Optionally show detected glare boxes using result.glareCorners
                }

                misnapView.finalFrame.observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is FrameResult.DocumentAnalysis -> {
                            // Session complete. Use result.frame to send the image to server
                        }
                    }
                }

                misnapView.controllerErrors.observe(viewLifecycleOwner) { result ->
                    // Handle errors received for frame
                    when (result) {
                        is ErrorResult.DocumentDetection -> {
                        }
                        is ErrorResult.DocumentAnalysis -> {
                        }
                    }
                }

                misnapView.recordedVideo.observe(viewLifecycleOwner) { videoBytes ->
                    // non-empty video bytes if recording was requested in MiSnapSettings
                }
            }

        // actually start camera preview and frame analysis
        misnapView.startMiSnapSession(settings, viewLifecycleOwner) {
            // misnap session started
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Optionally end the Mibi session outside of the view's lifecycle
        MibiData.releaseSession(this::class.java.name)
    }
}
