package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.controller.MiSnapController.FrameResult
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData

private class FaceAnalysis : Fragment() {
    private val license = "your_sdk_license"
    private lateinit var misnapController: MiSnapController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        misnapController = initFaceAnalysis()

        // when you get a frame from camera,
        // call startAnalysis()
    }

    private fun startAnalysis(frame: Frame) {
        misnapController.analyzeFrame(frame)
    }

    private fun initFaceAnalysis(): MiSnapController {
        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.FACE, license).apply {
            analysis.face.trigger = MiSnapSettings.Analysis.Face.Trigger.AUTO
        }

        // Optionally define a Mibi session outside of the controller's lifecycle
        MibiData.startSession(this::class.java.name, misnapSettings)

        return MiSnapController(requireContext(), misnapSettings).apply {
            feedbackResult.observe(viewLifecycleOwner) { result ->
                // Show hints corresponding to result.userAction
                // Optionally show detected face bounding box using result.fourCorners
            }

            frameResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is FrameResult.FaceAnalysis -> {
                        // Session complete. Use result.frame to send the image to server
                    }
                }
            }

            errorResult.observe(viewLifecycleOwner) { result ->
                // Handle errors received for frame
                when (result) {
                    is ErrorResult.FaceAnalysis -> {
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Optionally end the Mibi session outside of the controller's lifecycle
        MibiData.releaseSession(this::class.java.name)
    }
}
