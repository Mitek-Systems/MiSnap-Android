package com.miteksystems.misnap.examples.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleActivityIntegrationBinding
import com.miteksystems.misnap.nfc.MiSnapNfcReader
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment

/**
 * This sample is the easiest way of integrating the MiSnap SDK and it's best suited for applications
 * with a multi-activity architecture.
 *
 * This integration method uses [ActivityResultContracts] and [registerForActivityResult] to launch
 * a new [MiSnapWorkflowActivity] that handles the session.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.fragment for examples on how to integrate the MiSnap SDK
 * using your own activity.
 */
class IntegrationActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityIntegrationBinding

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
                when (stepResult) {
                    is MiSnapWorkflowStep.Result.Success -> {
                        when (val result = stepResult.result) {
                            is MiSnapFinalResult.DocumentSession -> {
                                /**
                                 * Recover the session data from the results.
                                 * Please see [MiSnapFinalResult.DocumentSession] for more information on the available data.
                                 */
                                val jpegImageBytes = result.jpegImage
                                val licenseExpiredNotification = result.licenseExpired
                                val mibiData = result.misnapMibiData
                                val videoBytes = result.video
                                val sessionWarnings = result.warnings
                                val documentClassification = result.classification
                                val barcode = result.barcode
                                val mrz = result.extraction?.mrz
                                val documentExtraction = result.extraction?.extractedData
                                val extractedDataCorners = result.extraction?.extractedDataCorners
                                val rts = result.rts
                            }
                            is MiSnapFinalResult.FaceSession -> {
                                /**
                                 * Recover the session data from the results.
                                 * Please see [MiSnapFinalResult.FaceSession] for more information on the available data.
                                 */
                                val jpegImageBytes = result.jpegImage
                                val licenseExpiredNotification = result.licenseExpired
                                val mibiData = result.misnapMibiData
                                val videoBytes = result.video
                                val sessionWarnings = result.warnings
                                val rts = result.rts
                            }
                            is MiSnapFinalResult.NfcSession -> {
                                /**
                                 * Recover the session data from the results.
                                 * Please see [MiSnapFinalResult.NfcSession] for more information on the available data.
                                 */
                                val licenseExpiredNotification = result.licenseExpired
                                val mibiData = result.misnapMibiData
                                /**
                                 * Depending on the type of document, the [MiSnapNfcReader.ChipData] will be
                                 * either [MiSnapNfcReader.ChipData.EuDl] or [MiSnapNfcReader.ChipData.Icao].
                                 * Please see the documentation for more information on the available data.
                                 */
                                when (val nfcData = result.nfcData) {
                                    is MiSnapNfcReader.ChipData.EuDl -> {
                                        val jpegImageBytes = nfcData.photo
                                        val firstName = nfcData.firstName
                                        val lastName = nfcData.lastName
                                    }
                                    is MiSnapNfcReader.ChipData.Icao -> {
                                        val jpegImageBytes = nfcData.photo
                                        val firstName = nfcData.firstName
                                        val lastName = nfcData.lastName
                                    }
                                }
                            }
                            is MiSnapFinalResult.BarcodeSession -> {
                                /**
                                 * Recover the session data from the results.
                                 * Please see [MiSnapFinalResult.BarcodeSession] for more information on the available data.
                                 */
                                val jpegImageBytes = result.jpegImage
                                val licenseExpiredNotification = result.licenseExpired
                                val mibiData = result.misnapMibiData
                                val videoBytes = result.video
                                val sessionWarnings = result.warnings
                                val barcode = result.barcode
                                val rts = result.rts
                            }
                            is MiSnapFinalResult.VoiceSession -> {
                                /**
                                 * Recover the session data from the results.
                                 * Please see [MiSnapFinalResult.VoiceSession] for more information on the available data.
                                 */
                                val licenseExpiredNotification = result.licenseExpired
                                val mibiData = result.misnapMibiData
                                val phrase = result.phrase
                                val wavAudioListBytes = result.voiceSamples
                            }
                        }
                    }
                    is MiSnapWorkflowStep.Result.Error -> {
                        /**
                         * Handle the error, please see [MiSnapWorkflowError] for the different errors
                         * applicable to the target MiSnap session.
                         */
                        when (val errorResult = stepResult.errorResult.error) {
                            is MiSnapWorkflowError.Permission -> {
                            }
                            is MiSnapWorkflowError.Camera -> {
                            }
                            is MiSnapWorkflowError.Cancelled -> {
                            }
                        }
                    }
                }
            }

            // Once you're done handling the results, clear them before invoking a new session.
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
             * and passing the list of [MiSnapWorkflowStep]s with your custom configurations.
             *
             * If multiple steps are defined these will be handled in the order they were submitted.
             */
            registerForActivityResult.launch(
                MiSnapWorkflowActivity.buildIntent(
                    this,
                    MiSnapWorkflowStep(
                        MiSnapSettings(
                            MiSnapSettings.UseCase.PASSPORT,
                            license
                        ).apply {
                            analysis.document.documentExtractionRequirement =
                                MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
                            analysis.document.enableDocumentClassification = true
                            analysis.document.redactOptionalData = true
                            camera.videoRecord.recordSession =
                                false //Disabling video recording if enabling optional data redaction since the data is not redacted from videos
                        }),
                    MiSnapWorkflowStep(
                        MiSnapSettings(
                            MiSnapSettings.UseCase.CHECK_FRONT,
                            license
                        ).apply {
                            analysis.document.trigger =
                                MiSnapSettings.Analysis.Document.Trigger.MANUAL
                            analysis.document.enableEnhancedManual =
                                true // Enabling hints in manual mode

                            //Disabling the image review screen
                            workflow.add(
                                getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
                                DocumentAnalysisFragment.buildWorkflowSettings(reviewCondition = DocumentAnalysisFragment.ReviewCondition.NEVER)
                            )
                        })
                )
            )
        }
    }
}
