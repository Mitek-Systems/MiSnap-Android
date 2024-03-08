package com.miteksystems.misnap.examples.misnapview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.R
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.controller.MiSnapController.FrameResult
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.MibiData
import com.miteksystems.misnap.workflow.view.MiSnapView

/**
 * This example demonstrates an integration with the MiSnap SDK that does not use the built-in
 * fragments and as such allows developers to roll a completely custom UI/UX.
 *
 * This example demonstrates how to programmatically create a [MiSnapView] to simplify the orchestration
 * of setting up a camera, a [MiSnapController], and the science to analyze preview frames.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.settings for examples on how to customize the UI/UX before
 * deciding to use this example.
 * @see com.miteksystems.misnap.examples.misnapview.MiSnapViewXml for an example on how to use the
 * [MiSnapView] from an XML layout.
 */
class MiSnapViewCode : Fragment() {
    private val license = "your_sdk_license"
    private lateinit var settings: MiSnapSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
            analysis.document.trigger = MiSnapSettings.Analysis.Document.Trigger.AUTO
        }
        /**
         * Optionally define a [MibiData] session outside of the view's lifecycle.
         */
        MibiData.startSession(this::class.java.name, settings)

        /**
         * Create an instance of a [MiSnapView] and observe the various [LiveData]s to get
         * feedback results, final results, recorded videos, errors, camera events, etc.
         *
         * @see [MiSnapView] for the full list of available APIs.
         */
        return MiSnapView(requireActivity()).apply {
            /**
             * Observe the [MiSnapView.feedbackResult] [LiveData] to handle the feedback from the
             * analyzed preview frames and handle them accordingly, e.g. by showing the corresponding
             * instructions on screen based on the [MiSnapController.FeedbackResult.userAction],
             * showing the detected document corners using [MiSnapController.FeedbackResult.corners]
             * or the detected glare corners in [MiSnapController.FeedbackResult.glareCorners].
             */
            feedbackResult.observe(viewLifecycleOwner) { feedbackResult ->

            }

            /**
             * Observe the [MiSnapView.finalFrame] [LiveData] to handle the successful results of a
             * session, e.g. by collecting the captured frame bytes in [FrameResult.DocumentAnalysis.frame]
             * to send the image to the server.
             *
             * @see [FrameResult] for all the possible result types emitted.
             */
            finalFrame.observe(viewLifecycleOwner) { result ->
                // Handle the result depending on the session type.
                when (result) {
                    is FrameResult.DocumentAnalysis -> {

                    }
                    else -> {}
                }
            }

            /**
             * Observe the [MiSnapView.controllerErrors] [LiveData] to handle errors during the
             * analysis of the preview frames.
             *
             * @see [ErrorResult] for all the possible error types emitted.
             */
            controllerErrors.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ErrorResult.DocumentDetection -> {
                    }
                    is ErrorResult.DocumentAnalysis -> {
                    }
                    else -> {}
                }
            }

            /**
             * Observe the [MiSnapView.recordedVideo] [LiveData] to receive a [ByteArray] with the
             * contents of the recorded video if requested in [MiSnapSettings].
             */
            recordedVideo.observe(viewLifecycleOwner) { videoBytes ->

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

    override fun onResume() {
        super.onResume()

        /**
         * Start the camera preview and register a callback to know when the preview has
         * started.
         */
        (requireView() as MiSnapView).startMiSnapSession(settings, viewLifecycleOwner) {

        }
    }
}
