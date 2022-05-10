package com.miteksystems.misnap.examples.serverconnection

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.core.serverconnection.MobileVerifyV2Request
import com.miteksystems.misnap.databinding.ExampleActivityIntegrationBinding
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.toServerResult

class MobileVerifyV2RequestActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityIntegrationBinding

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val request = MobileVerifyV2Request(
                configuration = MobileVerifyV2Request.Configuration()
            )

            MiSnapWorkflowActivity.Result.results.forEach {
                when (it) {
                    is MiSnapWorkflowStep.Result.Success -> {
                        when (it.result) {
                            is MiSnapFinalResult.DocumentSession, is MiSnapFinalResult.BarcodeSession -> {
                                request.addDocumentResult(it.result.toServerResult())
                            }
                            is MiSnapFinalResult.FaceSession -> {
                                request.setFaceResult(it.result.toServerResult())
                            }
                            is MiSnapFinalResult.NfcSession -> {
                                request.setNfcResult(it.result.toServerResult())
                            }
                        }
                    }
                }
            }

            val requestString = request.getRequest()
            // send requestString to MobileVerify server

            // When finished with the SDK results, clear them to free up space
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
            // It is best practice to query the camera's support before starting the session.
            // See the sample app for how to handle it
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
