package com.miteksystems.misnap.examples.workflowhandler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleActivityCombinedWorkflowHandlerBinding
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.CombinedWorkflowHandler

/**
 * This is an advanced combined workflow integration example, it is strongly recommended to follow
 * the most simple and easiest combined workflow integration instead:
 * @see com.miteksystems.misnap.examples.activity.IntegrationActivity
 *
 * This example is best suited for developers that have an application following the single-activity
 * architecture pattern and that want to create a combined workflow in their own activity while handing
 * over the control of the navigation to the MiSnap SDK.
 *
 * @see com.miteksystems.misnap.examples.advanced.CombinedWorkflowIntegrationActivity for a more
 * advanced combined workflow integration that allows to retain control on the navigation.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 */
class CombinedWorkflowHandlerActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityCombinedWorkflowHandlerBinding

    private val combinedWorkflowHandler: CombinedWorkflowHandler by lazy {
        CombinedWorkflowHandler(this, R.id.fragmentContainer)
    }

    /**
     * Register an [Observer] to handle the full list of results once all the steps in a combined
     * workflow have finished.
     * Under normal circumstances the results list should contain exactly one [MiSnapWorkflowStep.Result]
     * per configured [MiSnapWorkflowStep] and the indexes of the results list would match the configured
     * steps list, however, if the workflow ends due to an unrecoverable error, the results list will
     * be incomplete.
     *
     * The results of each session can be either successful or result in an error and both should
     * be handled accordingly.
     */
    private val combinedWorkflowResultsObserver =
        Observer<List<MiSnapWorkflowStep.Result>> { resultsList ->
            resultsList?.let { result ->
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
                                else -> {}
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
                                else -> {}
                            }
                        }
                    }
                }
                // All sessions done, continue with your flow.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleActivityCombinedWorkflowHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        /**
         * Create the list of [MiSnapWorkflowStep]s in the order you want them to appear in the
         * combined workflow.
         */
        val stepsList = listOf<MiSnapWorkflowStep>(
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.FACE, license).apply {
                analysis.face.trigger = MiSnapSettings.Analysis.Face.Trigger.AUTO
            }),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license).apply {
                analysis.document.trigger = MiSnapSettings.Analysis.Document.Trigger.AUTO
            }),
            MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
                analysis.barcode.type = MiSnapSettings.Analysis.Barcode.Type.QR_CODE
                analysis.document.barcodeExtractionRequirement =
                    MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
                analysis.barcode.trigger = MiSnapSettings.Analysis.Barcode.Trigger.AUTO
            })
        )

        /**
         * Observe the [CombinedWorkflowHandler.combinedWorkflowResults] [LiveData] to get notified when
         * the combined workflow has finished.
         */
        combinedWorkflowHandler.combinedWorkflowResults.observe(
            this,
            combinedWorkflowResultsObserver
        )

        /**
         * Optionally observe the [CombinedWorkflowHandler.combinedWorkflowNextStep] [LiveData] to get
         * notified each time the combined workflow moves from one step to another.
         */
        combinedWorkflowHandler.combinedWorkflowNextStep.observe(this) {

        }

        /**
         * Start the combined workflow with the list of steps using [CombinedWorkflowHandler.startCombinedWorkflow],
         * the method's return value indicates if the combined workflow was started successfully or not.
         */
        if (!combinedWorkflowHandler.startCombinedWorkflow(stepsList)) {

        }
    }

    /**
     * The [CombinedWorkflowHandler] will notify through the [CombinedWorkflowHandler.combinedWorkflowResults]
     * [LiveData] that the combined workflow has finished and provide the list of results, however, if
     * a user presses the back button, it's the developer's responsibility to collect the partial
     * results with [CombinedWorkflowHandler.getCollectedResults].
     */
    override fun onBackPressed() {
        super.onBackPressed()

        // Retrieve the collected results if the user aborts.
        val resultsList = combinedWorkflowHandler.getCollectedResults()

        // Clean the combined workflow handler and the view models.
        combinedWorkflowHandler.clean()
    }
}
