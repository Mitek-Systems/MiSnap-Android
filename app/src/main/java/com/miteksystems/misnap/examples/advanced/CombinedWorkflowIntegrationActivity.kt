package com.miteksystems.misnap.examples.advanced

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleActivityCombinedWorkflowIntegrationBinding
import com.miteksystems.misnap.workflow.MiSnapErrorResult
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.workflow.util.CombinedWorkflowHandler

/**
 * This is an advanced combined workflow integration example, it is strongly recommended to follow
 * the most simple and easiest combined workflow integration instead:
 * @see com.miteksystems.misnap.examples.activity.IntegrationActivity
 *
 * If the aforementioned example doesn't fit your needs you can integrate the combined workflow in
 * your own activity and delegate the navigation to the MiSnap SDK following this integration
 * example instead:
 * @see com.miteksystems.misnap.examples.workflowhandler.CombinedWorkflowHandlerActivity
 *
 * It is recommended to use this example only when none of the other combined workflow integrations fit
 * your needs. This example is targeted to developers who want to create a combined workflow without
 * delegating the work to the [MiSnapWorkflowActivity] i.e. developers that want to integrate the
 * combined workflow in their own activity while retaining full control of the navigation and logic
 * between steps.
 *
 * The overall process is as follows:
 * 1. Get access to both the [MiSnapWorkflowViewModel] and the [CombinedWorkflowHandler.CombinedViewModel].
 * 2. Subscribe to the [MiSnapWorkflowViewModel] [LiveData]s to handle results and errors.
 * 3. Build the list of [MiSnapWorkflowStep]s and setup the [CombinedWorkflowHandler.CombinedViewModel],
 * set the [MiSnapSettings] of the first step to the [MiSnapWorkflowViewModel].
 * 4. Navigate to start the workflow either by selecting the appropriate navigation graph if using
 * Jetpack Navigation or using [FragmentTransaction]s.
 * 5. Handle the [MiSnapFinalResult]s and the [MiSnapErrorResult]s by adding them as [MiSnapWorkflowStep.Result.Success]
 * or [MiSnapWorkflowStep.Result.Error] respectively to the [CombinedWorkflowHandler.CombinedViewModel].
 * 6. Determine the next [MiSnapWorkflowStep] to use with [CombinedWorkflowHandler.CombinedViewModel.combinedWorkflowStateMachine]
 * and apply its settings to the [MiSnapWorkflowViewModel], repeat steps 4-6 until there are no more steps to handle.
 * 7. Get all the results from [CombinedWorkflowHandler.CombinedViewModel.combinedWorkflowStepResultsList].
 * 8. Clear both [MiSnapWorkflowViewModel] and [CombinedWorkflowHandler.CombinedViewModel].
 *
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
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

        /**
         * Create the list of [MiSnapWorkflowStep]s in the order you want them to appear in the
         * combined workflow.
         */
        val stepsList = listOf(
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license).apply {
                analysis.document.trigger = MiSnapSettings.Analysis.Document.Trigger.AUTO
            }),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
                analysis.barcode.type = MiSnapSettings.Analysis.Barcode.Type.QR_CODE
                analysis.document.barcodeExtractionRequirement =
                    MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
                analysis.barcode.trigger = MiSnapSettings.Analysis.Barcode.Trigger.AUTO
            }),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license))
        )

        /**
         * Observe [MiSnapWorkflowViewModel.results] for [MiSnapFinalResult]s and add them as
         * [MiSnapWorkflowStep.Result.Success]. There should be exactly one result per step
         * regardless of the result type.
         */
        misnapWorkflowViewModel.results.observe(this) {
            it?.let {
                combinedWorkflowViewModel.addCombinedWorkflowStepResult(
                    MiSnapWorkflowStep.Result.Success(it)
                )

                // Once collected, evaluate the steps to determine the next action.
                processNextStep()
            }
        }

        /**
         * Observe [MiSnapWorkflowViewModel.error] for [MiSnapErrorResult]s and add them as
         * [MiSnapWorkflowStep.Result.Error]. There should be exactly one result per step
         * regardless of the result type.
         */
        misnapWorkflowViewModel.error.observe(this) {
            it?.let {
                combinedWorkflowViewModel.addCombinedWorkflowStepResult(
                    MiSnapWorkflowStep.Result.Error(it)
                )

                // Once collected, evaluate the steps to determine the next action.
                processNextStep()
            }
        }

        /**
         * Configure the [CombinedWorkflowHandler.CombinedViewModel] by applying the list of [MiSnapWorkflowStep]s
         * and verifying its configuration. Then set the [MiSnapSettings] of the initial step to the
         * [MiSnapWorkflowViewModel] to complete the start setup.
         *
         * Once ready, handle the navigation to start the workflow.
         */
        combinedWorkflowViewModel.applyCombinedWorkflowSteps(stepsList)
        if (combinedWorkflowViewModel.isCombinedWorkflowConfigured()) {
            misnapWorkflowViewModel.applySettings(stepsList.first().settings)

            navigateToStep(stepsList.first().settings.useCase)
        } else {
            // Handle an invalid combined workflow configuration.
        }
    }

    private fun processNextStep() {
        // Use the combined workflow state machine to determine the next action.
        combinedWorkflowViewModel.combinedWorkflowStateMachine?.let { workflowStateMachine ->
            val results = combinedWorkflowViewModel.getCombinedWorkflowStepResultsList()
            val steps = combinedWorkflowViewModel.combinedWorkflowStepsList

            /**
             * Determine the next and previous step, as well as the last results.
             * At the end of all steps there should be one result per step.
             */
            val latestResults = results.last()
            val latestStep = steps[results.size - 1]
            val nextStep = steps.getOrNull(results.size)

            /**
             * Determine the next [CombinedWorkflowHandler.StateMachine.Action].
             */
            when (val nextAction =
                workflowStateMachine.processNextState(latestResults, latestStep, nextStep)) {
                is CombinedWorkflowHandler.StateMachine.Action.Next -> {
                    // Continue to the next step.
                    val nextStepSettings = nextAction.step.settings

                    // Clear the viewmodel between steps.
                    misnapWorkflowViewModel.clearResults()

                    /**
                     * Handle your custom logic, e.g. by mutating the settings based on other results
                     * or skipping steps based on business rules.
                     */
                    misnapWorkflowViewModel.applySettings(nextStepSettings)

                    // Navigate to the next step.
                    navigateToStep(nextStepSettings.useCase)
                }
                is CombinedWorkflowHandler.StateMachine.Action.Skip -> {
                    /**
                     * Handle steps that should be skipped, e.g. by adding them as an error result
                     * before moving to the next step.
                     */
                }
                is CombinedWorkflowHandler.StateMachine.Action.Finish -> {
                    /**
                     * Handle the end of the workflow e.g. by collecting all the results and
                     * cleaning the view models.
                     */
                    val allResults = combinedWorkflowViewModel.getCombinedWorkflowStepResultsList()

                    combinedWorkflowViewModel.resetViewModel()
                    misnapWorkflowViewModel.clearResults()
                }
            }
        }
    }

    /**
     * This example handles the navigation using Jetpack Navigation and the built-in navgraphs.
     * Alternatively, you can handle the navigation with your own custom navgraphs or manually by
     * using [FragmentTransaction]s.
     *
     * @see com.miteksystems.misnap.examples.fragment.AnalysisFragmentTransaction for an example on
     * how to drive a workflow using [FragmentTransaction]s.
     */
    private fun navigateToStep(useCase: MiSnapSettings.UseCase) {
        // Select the most appropriate navgraph based on the use case.
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

        // Set the navigation graph.
        findNavController(R.id.fragmentContainer).setGraph(navGraph)
    }
}
