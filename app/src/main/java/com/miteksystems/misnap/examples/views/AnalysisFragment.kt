package com.miteksystems.misnap.examples.views

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.util.ViewBindingUtil
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.databinding.ExampleDocumentAnalysisBinding

class AnalysisFragment : Fragment(R.layout.example_document_analysis) {
    private val license = "your_sdk_license"

    private val binding by ViewBindingUtil.viewBinding(
        this,
        ExampleDocumentAnalysisBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license).apply {
            // Optionally set profile
            camera.profile = MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
            camera.videoRecord.recordSession = true
        }

        // Optionally define a Mibi session outside of the MiSnapView's lifecycle
        MibiData.startSession(this::class.java.name, settings)

        binding.guideView.drawableId = R.drawable.misnap_guide_passport

        binding.misnapView.feedbackResult.observe(viewLifecycleOwner) {
            // Map UserAction to appropriate hint message
            binding.hintView.text = it.userAction.toString()
        }

        binding.misnapView.finalFrame.observe(viewLifecycleOwner) {
            // stop video recording
            binding.misnapView.apply {
                stopRecording()
                feedbackResult.removeObservers(viewLifecycleOwner)
            }
            binding.recordingIconView.stop()

            binding.torchView.isVisible = false
            binding.misnapView.isVisible = false
            binding.guideView.isVisible = false
            binding.hintView.apply {
                clearText()
                visibility = View.INVISIBLE
            }

            // show SuccessView when finished
            binding.successView.start {
                // SuccessView finished, time to end the session.
            }
        }

        binding.misnapView.controllerErrors.observe(viewLifecycleOwner) {
            when (it) {
                is ErrorResult.DocumentDetection, is ErrorResult.DocumentAnalysis -> {
                    // Error occurred while analyzing frame. End session.
                }
            }
        }

        binding.misnapView.cameraEvents.observe(viewLifecycleOwner) {
            when (it) {
                is Event.InitializationError -> {
                    // Camera could not be started. End session.
                }
            }
        }

        // Bind torch events from Camera to TorchView
        binding.misnapView.torchEvents.observe(viewLifecycleOwner) {
            if (null != it) {
                binding.torchView.isTorchOn = it
            }
        }

        // Bind torch events from TorchView to Camera
        binding.torchView.torchEvents.observe(viewLifecycleOwner) {
            if (null != it) {
                binding.misnapView.setTorchEnabled(it)
            }
        }

        // start MiSnap session
        binding.misnapView.startMiSnapSession(settings, viewLifecycleOwner) {
            binding.recordingIconView.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Optionally end the Mibi session outside of the MiSnapView's lifecycle
        MibiData.releaseSession(this::class.java.name)
    }
}
