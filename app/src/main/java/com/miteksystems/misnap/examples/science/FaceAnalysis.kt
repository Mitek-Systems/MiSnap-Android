package com.miteksystems.misnap.examples.science

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.controller.MiSnapController.ErrorResult
import com.miteksystems.misnap.controller.MiSnapController.FrameResult
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.MiSnapSettings

/**
 * This example demonstrates a direct integration with MiSnap SDK's face analysis science through
 * the [MiSnapController], this type of integration is best suited for developers that want to
 * interface with the science directly and that will take care of supplying the frames themselves.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.science for examples on how to directly interface with other
 * MiSnap SDK sciences.
 */
private class FaceAnalysis : Fragment() {
    private val license = "your_sdk_license"
    private lateinit var misnapController: MiSnapController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        misnapController = initFaceAnalysis()

        /**
         * Call [startAnalysis] once the controller is ready to analyze [Frame]s.
         */
    }

    private fun startAnalysis(frame: Frame) {
        misnapController.analyzeFrame(frame)
    }

    /**
     * Create a [MiSnapController] capable of analyzing frames for a selfie session, then observe
     * the different [LiveData] objects to get notified about feedback results, final results, errors,
     * etc.
     * Once the controller is ready and initialized create a [Frame] object and use the [MiSnapController.analyzeFrame]
     * method to analyze the frame.
     *
     * @see com.miteksystems.misnap.examples.science.FrameFromNativeCamera for examples on how to
     * build a [Frame] object from different camera APIs.
     */
    private fun initFaceAnalysis(): MiSnapController {
        val misnapSettings = MiSnapSettings(MiSnapSettings.UseCase.FACE, license).apply {
            analysis.face.trigger = MiSnapSettings.Analysis.Face.Trigger.AUTO
        }

        return MiSnapController.create(requireContext(), misnapSettings).apply {
            /**
             * Observe the [MiSnapController.feedbackResult] [LiveData] to handle the feedback from
             * the analyzed frames and handle them accordingly, e.g. by showing the corresponding
             * instructions on screen based on the [MiSnapController.FeedbackResult.userAction] or
             * showing the detected face bounding box corners using [MiSnapController.FeedbackResult.corners].
             */
            feedbackResult.observe(viewLifecycleOwner) { feedbackResult ->

            }

            /**
             * Observe the [MiSnapController.frameResult] [LiveData] to handle the successful results
             * of a session, e.g. by collecting the frame data in [FrameResult.FaceAnalysis.frame]
             * to send it to the server.
             */
            frameResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is FrameResult.FaceAnalysis -> {

                    }
                    else -> {}
                }
            }

            /**
             * Observe the [MiSnapController.errorResult] [LiveData] to handle errors during the
             * analysis of frames.
             *
             * @see [ErrorResult] for all the possible error types emitted.
             */
            errorResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ErrorResult.FaceAnalysis -> {
                    }
                    else -> {}
                }
            }
        }
    }
}
