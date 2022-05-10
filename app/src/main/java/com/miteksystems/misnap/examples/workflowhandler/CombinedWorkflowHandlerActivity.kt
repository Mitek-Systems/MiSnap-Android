package com.miteksystems.misnap.examples.workflowhandler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.CombinedWorkflowHandler
import com.miteksystems.misnap.R
import com.miteksystems.misnap.databinding.ExampleActivityCombinedWorkflowHandlerBinding

/**
 * This is an advanced combined workflow integration example, it is strongly recommended to follow
 * the most simple and easiest combined workflow integration instead:
 * @see com.miteksystems.misnap.examples.activity.IntegrationActivity
 *
 * Use this example if you want a single activity based integration and you want to create a combined
 * workflow in your own activity while handing over the control of the navigation to the MiSnap SDK.
 */
class CombinedWorkflowHandlerActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityCombinedWorkflowHandlerBinding

    private val combinedWorkflowHandler: CombinedWorkflowHandler by lazy {
        CombinedWorkflowHandler(this, R.id.fragmentContainer)
    }

    private val combinedWorkflowResultsObserver =
        Observer<List<MiSnapWorkflowStep.Result>> { resultsList ->
            resultsList?.let { result ->
                // The results list indexes match the configured stepsList indexes.
                // If the workflow ends due to an unrecoverable error, the results list will be incomplete.
                result.forEach {
                    when (it) {
                        is MiSnapWorkflowStep.Result.Success -> {
                            // Access the results if the step was successful.
                            when (it.result) {
                                is MiSnapFinalResult.FaceSession -> {
                                }
                                is MiSnapFinalResult.DocumentSession -> {
                                }
                                is MiSnapFinalResult.BarcodeSession -> {
                                }
                            }
                        }
                        is MiSnapWorkflowStep.Result.Error -> {
                            // Access the specific error if the step failed.
                            when (it.errorResult.error) {
                                is MiSnapWorkflowError.CombinedWorkflow -> {
                                }
                                is MiSnapWorkflowError.CombinedWorkflowSkippedStep -> {
                                }
                                is MiSnapWorkflowError.Permission -> {
                                }
                            }
                        }
                    }
                }
                // All sessions done, continue with your flow
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleActivityCombinedWorkflowHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        // Create the list of steps in the order you want the workflow
        val stepsList = listOf<MiSnapWorkflowStep>(
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.FACE, license)),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license)),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
                analysis.barcode.type = MiSnapSettings.Analysis.Barcode.Type.QR_CODE
                analysis.document.barcodeExtractionRequirement =
                    MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
            })
        )

        // Observe the final list of results
        combinedWorkflowHandler.combinedWorkflowResults.observe(
            this,
            combinedWorkflowResultsObserver
        )

        // Optionally listen for the next step events
        combinedWorkflowHandler.combinedWorkflowNextStep.observe(this) {
            // Run your custom logic
        }

        // Start the combined workflow passing the list of steps
        if (!combinedWorkflowHandler.startCombinedWorkflow(stepsList)) {
            // Handle the case were the workflow couldn't start
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // Retrieve the collected results if the user aborts
        val resultsList = combinedWorkflowHandler.getCollectedResults()

        // Clean the combined workflow handler and the view models
        combinedWorkflowHandler.clean()
    }
}
