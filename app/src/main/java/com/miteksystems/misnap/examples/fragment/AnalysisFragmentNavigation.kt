package com.miteksystems.misnap.examples.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.R

class AnalysisFragmentNavigation : AppCompatActivity(R.layout.example_fragment_navigation) {
    private val license = "your_sdk_license"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // call MiSnapViewModel.applySettings() before navigating to graph's startDestination
        val settings = MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license)

        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java].also { viewModel ->
            viewModel.applySettings(settings)

            viewModel.results.observe(this) {
                it?.let { result ->
                    when (result) {
                        is MiSnapFinalResult.BarcodeSession -> {
                            // Session complete. Continue your flow.
                        }
                    }
                }
            }

            viewModel.error.observe(this) {
                it?.let {
                    // Handle errors
                }
            }
        }
    }
}
