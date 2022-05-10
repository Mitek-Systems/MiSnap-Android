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

private class DocumentAnalysis : Fragment() {
    private val license = "your_sdk_license"
    private lateinit var misnapController: MiSnapController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        misnapController = initDocumentAnalysis()

        // when you get a frame from camera, call
        // startAnalysis()
    }

    private fun startAnalysis(frame: Frame) {
        misnapController.analyzeFrame(frame, forceFrameResult = false)
    }

    private fun initDocumentAnalysis(): MiSnapController {
        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.CHECK_FRONT, license).apply {
            analysis.document.check.geo = MiSnapSettings.Analysis.Document.Check.Geo.US

            // optionally enable Barcode Extraction
            analysis.document.barcodeExtractionRequirement = MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
        }

        // Optionally define a Mibi session outside of the controller's lifecycle
        MibiData.startSession(this::class.java.name, misnapSettings)

        return MiSnapController(requireContext(), misnapSettings).apply {
            feedbackResult.observe(viewLifecycleOwner) { result ->
                // Show hints corresponding to result.userAction
                // Optionally show detected document corners using result.fourCorners
                // Optionally show detected glare boxes using result.glareCorners
            }

            frameResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is FrameResult.DocumentAnalysis -> {
                        // Session complete. Use result.frame to send the image to server
                        // Use result.barcode to send to server
                    }
                }
            }

            errorResult.observe(viewLifecycleOwner) { result ->
                // Handle errors received for frame
                when (result) {
                    is ErrorResult.DocumentDetection -> {
                    }
                    is ErrorResult.DocumentAnalysis -> {
                    }
                    is ErrorResult.BarcodeDetection -> {
                    }
                    is ErrorResult.BarcodeAnalysis -> {
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
