package com.miteksystems.misnap.examples.serverconnection

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.serverconnection.MiSnapTransactionResult
import com.miteksystems.misnap.core.serverconnection.MobileVerifyV3Request
import com.miteksystems.misnap.databinding.ExampleActivityIntegrationBinding
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.toServerResult

/**
 * This example demonstrates how to handle the results from the MiSnap SDK to build an
 * HTTP request payload that can be used with the Mobile Verify V3 API.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see [MobileVerifyV3Request] for the full list of properties used to build the payload.
 */
class MobileVerifyV3DocumentRequestActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityIntegrationBinding

    /**
     * Register a request to start an activity along with a callback to handle the results of
     * the launched activity once they're available to form the request payload.
     */
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val request = MobileVerifyV3Request()

            /**
             * Once the [ActivityResult] is available, get the available session results from
             * [MiSnapWorkflowActivity.Result.results] and handle them accordingly. To add the results
             * to the request payload builder first convert the [MiSnapWorkflowActivity.Result] into a
             * [MiSnapTransactionResult].
             */
            MiSnapWorkflowActivity.Result.results.iterator().forEach {
                when (it) {
                    is MiSnapWorkflowStep.Result.Success -> {
                        when (it.result) {
                            is MiSnapFinalResult.DocumentSession, is MiSnapFinalResult.BarcodeSession -> {
                                // Add the barcode and/or document session results.
                                request.addDocumentResult(it.result.toServerResult() as MiSnapTransactionResult.DocumentResult)
                            }
                            else -> {}
                        }
                    }
                    else -> {}
                }
            }

            /**
             * Prepare the request payload once all the results are added, then use it in an HTTP
             * request and send it to the Mobile Verify V3 API server.
             */
            val requestString = request.getRequest()

            // Once you're done handling the results, clear them before calling for a new session.
            MiSnapWorkflowActivity.Result.clearResults()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleActivityIntegrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.startSession.setOnClickListener {
            /**
             * Create an [Intent] to launch the [MiSnapWorkflowActivity] by calling [MiSnapWorkflowActivity.buildIntent]
             * and passing the list of [MiSnapWorkflowStep]s with your custom [MiSnapSettings].
             *
             * If multiple steps are defined these will be handled in the order they were submitted.
             */
            registerForActivityResult.launch(
                MiSnapWorkflowActivity.buildIntent(
                    this,
                    MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license)),
                    MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.FACE, license)),
                    MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license))
                )
            )
        }
    }
}
