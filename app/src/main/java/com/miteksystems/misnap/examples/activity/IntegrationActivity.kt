package com.miteksystems.misnap.examples.activity

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.databinding.ExampleActivityIntegrationBinding

class IntegrationActivity : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleActivityIntegrationBinding

    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            MiSnapWorkflowActivity.Result.results.forEach {
                when (it) {
                    is MiSnapWorkflowStep.Result.Success -> {
                        when (it.result) {
                            is MiSnapFinalResult.DocumentSession -> {
                            }
                            is MiSnapFinalResult.FaceSession -> {
                            }
                        }
                    }
                    is MiSnapWorkflowStep.Result.Error -> {
                        when (it.errorResult.error) {
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
                    MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.FACE, license))
                )
            )
        }
    }
}
