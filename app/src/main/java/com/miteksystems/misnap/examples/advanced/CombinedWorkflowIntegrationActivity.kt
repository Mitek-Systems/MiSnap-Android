package com.miteksystems.misnap.examples.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.workflow.util.CombinedWorkflowHandler
import com.miteksystems.misnap.R
import com.miteksystems.misnap.databinding.ExampleActivityCombinedWorkflowIntegrationBinding

/**
 * This is an advanced combined workflow integration example, it is strongly recommended to follow
 * the most simple and easiest combined workflow integration instead:
 * @see com.miteksystems.misnap.examples.activity.IntegrationActivity
 *
 * If the aforementioned example doesn't fit your needs you can integrate the combined workflow in
 * your own activity and delegate the navigation to the MiSnap SDK following this integration
 * instead:
 * @see com.miteksystems.misnap.examples.workflowhandler.CombinedWorkflowHandlerActivity
 *
 * Use this example if you want a single activity based integration and you want to create a combined
 * workflow in your own activity while retaining full control of the navigation.
 */
class CombinedWorkflowIntegrationActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityCombinedWorkflowIntegrationBinding
    private val misnapWorkflowViewModel: MiSnapWorkflowViewModel by lazy {
        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java]
    }
    private val combinedWorkflowViewModel: CombinedWorkflowHandler.CombinedViewModel by lazy {
        ViewModelProvider(this)[CombinedWorkflowHandler.CombinedViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleActivityCombinedWorkflowIntegrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // Create the list of steps in the order you want the workflow.
        val stepsList = listOf(
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license)),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
                analysis.barcode.type = MiSnapSettings.Analysis.Barcode.Type.QR_CODE
                analysis.document.barcodeExtractionRequirement =
                    MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
            }),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license))
        )

        // Observe the final results of each step to collect them.
        misnapWorkflowViewModel.results.observe(this) {
            it?.let {
                combinedWorkflowViewModel.addCombinedWorkflowStepResult(
                    MiSnapWorkflowStep.Result.Success(it)
                )

                // Once collected, process to the next step.
                processNextStep()
            }
        }

        // Observe the errors to collect them as results if necessary.
        misnapWorkflowViewModel.error.observe(this) {
            it?.let {
                combinedWorkflowViewModel.addCombinedWorkflowStepResult(
                    MiSnapWorkflowStep.Result.Error(it)
                )

                // Once collected, process to the next step.
                processNextStep()
            }
        }

        // Configure the combined workflow ViewModel and validate its configuration.
        combinedWorkflowViewModel.applyCombinedWorkflowSteps(stepsList)
        if (combinedWorkflowViewModel.isCombinedWorkflowConfigured()) {
            // Kickoff the combined workflow.
            misnapWorkflowViewModel.applySettings(stepsList.first().settings)

            // Handle the step navigation.
            navigateToStep(stepsList.first().settings.useCase)
        } else {
            // Handle the invalid configuration.
        }
    }

    private fun processNextStep() {
        // Use the combined workflow state machine to determine the next action
        combinedWorkflowViewModel.combinedWorkflowStateMachine?.let { workflowStateMachine ->
            val results = combinedWorkflowViewModel.getCombinedWorkflowStepResultsList()
            val steps = combinedWorkflowViewModel.combinedWorkflowStepsList
            // Determine the next and previous step, as well as the last results.
            // At the end of all steps there should be one result for each step.
            val latestResults = results.last()
            val latestStep = steps[results.size - 1]
            val nextStep = steps.getOrNull(results.size)

            // Determine the next action to take in the workflow.
            when (val nextAction =
                workflowStateMachine.processNextState(latestResults, latestStep, nextStep)) {
                is CombinedWorkflowHandler.StateMachine.Action.Next -> {
                    // Continue to the next step.
                    val nextStepSettings = nextAction.step.settings

                    // Clear the viewmodel between steps
                    misnapWorkflowViewModel.clearResults()

                    // Handle your custom logic, e.g. by mutating the settings based on other results
                    // or skipping steps based on business rules.
                    misnapWorkflowViewModel.applySettings(nextStepSettings)

                    // Navigate to the next step.
                    navigateToStep(nextStepSettings.useCase)
                }
                is CombinedWorkflowHandler.StateMachine.Action.Skip -> {
                    // Handle steps that should be skipped, e.g. by adding them as an error result
                    // before moving to the next step.
                }
                is CombinedWorkflowHandler.StateMachine.Action.Finish -> {
                    // Handle the end of the workflow e.g. by collecting all the results and
                    // cleaning the view models.
                    val allResults = combinedWorkflowViewModel.getCombinedWorkflowStepResultsList()

                    combinedWorkflowViewModel.resetViewModel()
                    misnapWorkflowViewModel.clearResults()
                }
            }
        }
    }

    private fun navigateToStep(useCase: MiSnapSettings.UseCase) {
        // Navigate to the appropriate flow according to the use case
        // in this case replacing the current navgraph.
        val navGraph = when (useCase) {
            MiSnapSettings.UseCase.PASSPORT -> {
                R.navigation.document_session_flow
            }
            MiSnapSettings.UseCase.BARCODE -> {
                R.navigation.barcode_session_flow
            }
            MiSnapSettings.UseCase.NFC -> {
                R.navigation.nfc_reader_flow
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        findNavController(R.id.fragmentContainer).setGraph(navGraph)
    }
}