package com.miteksystems.misnap.examples.settings

import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment.ReviewCondition
import com.miteksystems.misnap.workflow.fragment.HelpFragment
import com.miteksystems.misnap.R

class WorkflowSettings : AppCompatActivity() {
    private val license = "your_sdk_license"

    override fun onStart() {
        super.onStart()

        // Use the method "buildWorkflowSettings" from the workflow fragments to build
        // a preformatted JSON string with the applicable settings, the method provides information
        // on the available settings and the expected inputs.
        val helpFragmentWorkflowSettings =
            HelpFragment.buildWorkflowSettings(showSkipCheckBox = false)
        val documentAnalysisFragmentWorkflowSettings = DocumentAnalysisFragment.buildWorkflowSettings(
            timeoutDuration = 15_000,
            manualButtonDrawableId = android.R.drawable.ic_menu_camera,
            guideViewShowVignette = true,
            hintViewShouldShowBackground = true,
            successViewShouldVibrate = true,
            reviewCondition = ReviewCondition.ALWAYS
        )

        // Create a settings object and add the preformatted workflow settings per fragment
        val settings = MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
            // Link the fragment label identifier to the preformatted settings using the "add" function
            // of MiSnapSettings.workflow, the label should correspond to the one used in the fragment
            // element in the navigation graph.
            workflow.add(
                getString(R.string.misnapWorkflowDocumentAnalysisFlowHelpFragmentLabel),
                helpFragmentWorkflowSettings
            )
            workflow.add(
                getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
                documentAnalysisFragmentWorkflowSettings
            )
        }

        // Apply the built settings as per your preferred integration type
    }
}
