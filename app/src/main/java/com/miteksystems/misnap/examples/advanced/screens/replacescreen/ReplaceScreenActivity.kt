package com.miteksystems.misnap.examples.advanced.screens.replacescreen

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleReplaceScreenActivityBinding
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep

/**
 * This advanced example demonstrates how to replace a screen in an existing flow by creating a
 * custom navgraph. This example uses the "document_session_flow" default navgraph as baseline to
 * create a custom flow that replaces the HelpFragment with [ReplaceScreenCustomFragment].
 *
 * This integration method uses [ActivityResultContracts] and [registerForActivityResult] to launch
 * a new [MiSnapWorkflowActivity] that handles the session.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see R.navigation.example_replace_sceen_navigation for the navgraph definition and setup.
 * @see ReplaceScreenCustomFragment for the custom fragment definition.
 */
class ReplaceScreenActivity: AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleReplaceScreenActivityBinding

    /**
     * Register a request to start an activity along with a callback to handle the results of
     * the launched activity once they're available.
     */
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            /**
             * Once the [ActivityResult] is available, get the available session results from
             * [MiSnapWorkflowActivity.Result.results] and handle them accordingly.
             *
             * The list of results will match the order in which the [MiSnapWorkflowStep]s
             * were defined at the time of creating the launch [Intent].
             */
            MiSnapWorkflowActivity.Result.results.forEachIndexed { index, stepResult ->
                /**
                 * @see [com.miteksystems.misnap.examples.activity.IntegrationActivity] for more
                 * information on how to handle results.
                 */
            }

            // Once you're done handling the results, clear them before invoking a new session.
            MiSnapWorkflowActivity.Result.clearResults()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleReplaceScreenActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.startSession.setOnClickListener {
            registerForActivityResult.launch(
                MiSnapWorkflowActivity.buildIntent(
                    this,
                    /**
                     * Build the [MiSnapWorkflowStep] providing a navGaphId to use during the
                     * MiSnap SDK session.
                     */
                    MiSnapWorkflowStep(
                        MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license),
                        R.navigation.example_replace_sceen_navigation
                    )
                )
            )
        }
    }
}
