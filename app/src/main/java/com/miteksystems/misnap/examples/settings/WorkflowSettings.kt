package com.miteksystems.misnap.examples.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment.ReviewCondition
import com.miteksystems.misnap.workflow.fragment.HelpFragment

/**
 * This example demonstrates the customization of the MiSnap SDK UI and behavior through the use
 * of a [MiSnapSettings] object. This is the most straight-forward way of customizing the workflow
 * as it uses the main configuration object.
 *
 * Please refer to the "buildWorkflowSettings" method of the fragment you want to customize for the
 * full list of customization options.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.settings.FragmentArguments for an example on how to customize
 * the UI and behavior using [Bundle]s instead.
 */
class WorkflowSettings : AppCompatActivity() {
    private val license = "your_sdk_license"

    override fun onStart() {
        super.onStart()

        /**
         * Use the method "buildWorkflowSettings" from the workflow fragment you want to customize to
         * create a JSON string that can be added to [MiSnapSettings.workflow] to apply your customizations.
         */
        val helpFragmentWorkflowSettings =
            HelpFragment.buildWorkflowSettings(showSkipCheckBox = false)
        val documentAnalysisFragmentWorkflowSettings =
            DocumentAnalysisFragment.buildWorkflowSettings(
                timeoutDuration = 15_000,
                manualButtonDrawableId = android.R.drawable.ic_menu_camera,
                guideViewShowVignette = true,
                hintViewShouldShowBackground = true,
                successViewShouldVibrate = true,
                reviewCondition = ReviewCondition.ALWAYS
            )

        /**
         * Identify the label of the fragment you want to customize from the strings, then use it along the
         * JSON formatted workflow settings from the "buildWorkflowSettings" method to add them to the
         * [MiSnapSettings.workflow] map.
         * The label corresponds to the one used in the fragment element of the navigation graph in
         * which is included.
         * Use the built settings with your preferred integration type.
         *
         * NOTE: See the different navgraphs included in the Workflow module for the full list of fragment
         *  labels used.
         */
        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
            workflow.add(
                getString(R.string.misnapWorkflowDocumentAnalysisFlowHelpFragmentLabel),
                helpFragmentWorkflowSettings
            )
            workflow.add(
                getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
                documentAnalysisFragmentWorkflowSettings
            )
        }
    }
}
