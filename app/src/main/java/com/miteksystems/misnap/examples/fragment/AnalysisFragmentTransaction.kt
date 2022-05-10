package com.miteksystems.misnap.examples.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.fragment.*
import com.miteksystems.misnap.databinding.ExampleFragmentTransactionBinding

class AnalysisFragmentTransaction : AppCompatActivity() {
    private val license = "your_sdk_license"

    private lateinit var binding: ExampleFragmentTransactionBinding

    private var tutorialFragmentHashCode: Int? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java]
    }

    // Handle the NavigationErrors to determine the next destination
    private val navigationErrorsObserver = Observer<NavigationError?> { navError ->
        navError?.navigationErrorInfo?.let { errorInfo ->
            when (errorInfo.fragmentClass) {
                HelpFragment::class.java -> {
                    when (navError.action) {
                        is NavigationAction.Help.NavigateContinue -> {
                            // Help fragment is used as tutorial and help screen, the hashCode from
                            // the errorInfo is useful to differentiate between instances of the same
                            // fragment
                            if (tutorialFragmentHashCode == errorInfo.hashCode) {
                                // Handle a continue action from a Tutorial Screen
                                executeFragmentTransaction(BarcodeAnalysisFragment())
                            } else {
                                // Handle a continue action from a Help Screen
                            }
                        }
                    }
                }
                BarcodeAnalysisFragment::class.java -> {
                    when (navError.action) {
                        is NavigationAction.BarcodeAnalysis.NavigateHelp -> {
                            executeFragmentTransaction(HelpFragment())
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ExampleFragmentTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        viewModel.applySettings(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license))

        viewModel.navigationErrors.observe(this, navigationErrorsObserver)

        viewModel.results.observe(this) { result ->
            result?.let {
                when (it) {
                    is MiSnapFinalResult.BarcodeSession -> {
                    }
                }
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                when (it.error) {
                    is MiSnapWorkflowError.Permission -> {
                    }
                    is MiSnapWorkflowError.Camera -> {
                    }
                    is MiSnapWorkflowError.Cancelled -> {
                    }
                }
            }
        }

        startSession()
    }

    private fun startSession() {
        // Start with the tutorial
        val tutorialFragment = HelpFragment()
        tutorialFragmentHashCode = tutorialFragment.hashCode()
        executeFragmentTransaction(tutorialFragment)
    }

    private fun executeFragmentTransaction(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(binding.fragmentContainer.id, fragment)
            }
            .commit()
    }
}
