package com.miteksystems.misnap.examples.views

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.R
import com.miteksystems.misnap.camera.frameproducers.FrameProducer.Event
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.databinding.ExampleDocumentAnalysisBinding
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.workflow.util.ViewBindingUtil
import com.miteksystems.misnap.workflow.view.*

/**
 * This example demonstrates an advanced integration that makes use of the different android Views
 * included in the MiSnap SDK to fully customize the UI/UX by recreating an analysis screen.
 * This is an advanced integration example and as such is only suitable for developers whose customization
 * needs are not fulfilled through standard integration types customizations.
 *
 * This example uses the following android Views:
 * * [MiSnapView] to orchestrate the acquisition of frames from the device's camera and analyze them.
 * * [GuideView] to represent a visual guide for the document's alignment.
 * * [HintView] to display hint messages to help the user during the session.
 * * [TorchView] to facilitate the use of the device's camera torch light.
 * * [RecordingIconView] to facilitate the awareness of an ongoing video recording during the session.
 * * [SuccessView] to indicate that the session has concluded successfully.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.misnapview for an in-depth example on the usage of the
 * [MiSnapView] APIs.
 * @see com.miteksystems.misnap.workflow.view for more information on the android views used in this
 * example.
 * @see com.miteksystems.misnap.examples.settings for examples on how to easily customize the UI and
 * behavior of the SDK.
 */
class AnalysisFragment : Fragment(R.layout.example_document_analysis) {
    private val license = "your_sdk_license"

    /**
     * To accurately track the number of auto and manual mode session retries in [MibiData] it is
     * required to bind the session with [MibiData.bindSession] and to keep track of the fragment
     * resumed state.
     */
    private var resumed: Boolean = false
    private var mibiDataSession = MibiData.bindSession()

    private val binding by ViewBindingUtil.viewBinding(
        this,
        ExampleDocumentAnalysisBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * Determine if the fragment is being resumed for [MibiData] tracking purposes.
         */
        savedInstanceState?.let {
            resumed = true
        }

        val settings = MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license).apply {
            analysis.document.trigger = MiSnapSettings.Analysis.Document.Trigger.AUTO
            // Optionally set the camera profile to use.
            camera.profile = MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
            camera.videoRecord.recordSession = true
        }

        /**
         * Optionally define a [MibiData] session outside of the view's lifecycle.
         */
        MibiData.startSession(this::class.java.name, settings)

        /**
         * Customize the look and feel of the [GuideView], e.g. by setting a custom drawable.
         *
         * @see [GuideView] for more information on the available properties that can be customized.
         */
        binding.guideView.drawableId = R.drawable.misnap_guide_passport

        /**
         * Observe the [MiSnapView.feedbackResult] [LiveData] to handle the feedback from the analyzed
         * preview frames and handle them accordingly, e.g. by showing the corresponding instructions
         * on screen based on the [MiSnapController.FeedbackResult.userAction].
         */
        binding.misnapView.feedbackResult.observe(viewLifecycleOwner) {
            /**
             * The [MiSnapController.FeedbackResult.userAction] is an identifier that should be mapped
             * to an appropriate string message to use as hint.
             */
            binding.hintView.text = it.userAction.toString()
        }

        /**
         * Observe the [MiSnapView.finalFrame] [LiveData] to handle the successful results of a session
         * and to perform UI cleanup actions, e.g. by stopping the video recording, clearing the contents
         * of views and modifying their visibility.
         */
        binding.misnapView.finalFrame.observe(viewLifecycleOwner) {
            /**
             * Finalize the recording of the session and clear the appropriate observers to stop
             * receiving updates after a successful session.
             */
            binding.misnapView.apply {
                stopRecording()
                feedbackResult.removeObservers(viewLifecycleOwner)
            }
            binding.recordingIconView.stop()

            /**
             * Adjust the visibility of the relevant android Views to indicate the user that the
             * session has ended.
             */
            binding.torchView.isVisible = false
            binding.misnapView.isVisible = false
            binding.guideView.isVisible = false
            binding.hintView.apply {
                clearText()
                visibility = View.INVISIBLE
            }

            /**
             * Display the [SuccessView] animation and finalize the session.
             */
            binding.successView.start {
                // Animation finished, time to end the session.
            }
        }

        /**
         * Observe the [MiSnapView.controllerErrors] [LiveData] to handle errors during the analysis
         * of the preview frames, e.g. by aborting the session or addressing the error.
         */
        binding.misnapView.controllerErrors.observe(viewLifecycleOwner) {
            when (it) {
                is ErrorResult.DocumentDetection, is ErrorResult.DocumentAnalysis -> {

                }
                else -> {}
            }
        }

        /**
         * Observe the [MiSnapView.cameraEvents] [LiveData] to handle errors during the initialization
         * and operation of the camera, e.g. by aborting the session or addressing the error.
         */
        binding.misnapView.cameraEvents.observe(viewLifecycleOwner) {
            when (it) {
                is Event.InitializationError -> {

                }
                else -> {}
            }
        }

        /**
         * Observe the [MiSnapView.torchEvents] [LiveData] to handle torch state changes, in this case
         * by applying the new state to the [TorchView] so they're bound.
         */
        binding.misnapView.torchEvents.observe(viewLifecycleOwner) {
            if (null != it) {
                binding.torchView.isTorchOn = it
            }
        }

        /**
         * Observe the [TorchView.torchEvents] [LiveData] to handle torch state change requests, in
         * this case by applying the new state to the [MiSnapView] so that the request can reach the
         * camera.
         */
        binding.torchView.torchEvents.observe(viewLifecycleOwner) {
            if (null != it) {
                binding.misnapView.setTorchEnabled(it)
            }
        }

        // Start the session.
        binding.misnapView.startMiSnapSession(settings, viewLifecycleOwner) {
            binding.recordingIconView.start()

            /**
             * Increase the number of auto or manual session retries based on the [MibiData.sessionOwner]
             * and whether or not the fragment is being resumed.
             */
            if ((MibiData.sessionOwner == this::class.java.name ||
                    MibiData.sessionOwner == MiSnapWorkflowViewModel::class.java.name) && !resumed
            ) {
                if (settings.analysis.barcode.trigger == MiSnapSettings.Analysis.Barcode.Trigger.AUTO) {
                    mibiDataSession.metaData.autoTries += 1
                } else {
                    mibiDataSession.metaData.manualTries += 1
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        /**
         * Optionally end the [MibiData] session outside of the view's lifecycle.
         */
        MibiData.releaseSession(this::class.java.name)
    }
}
