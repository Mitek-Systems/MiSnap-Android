package com.miteksystems.misnap.examples.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment.ReviewCondition
import com.miteksystems.misnap.workflow.fragment.HelpFragment
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.databinding.ExampleFragmentTransactionBinding

class FragmentArguments : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleFragmentTransactionBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExampleFragmentTransactionBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()

        // Use the method "buildFragmentArguments" from the workflow fragments to build
        // a bundle with the applicable settings, the method provides information on the available
        // settings and the expected inputs.
        val helpFragmentArguments =
            HelpFragment.buildFragmentArguments(showSkipCheckBox = false)
        val documentAnalysisFragmentArguments =
            DocumentAnalysisFragment.buildFragmentArguments(
                timeoutDuration = 15_000,
                manualButtonDrawableId = android.R.drawable.ic_menu_camera,
                guideViewShowVignette = true,
                successViewShouldVibrate = true,
                reviewCondition = ReviewCondition.ALWAYS
            )

        viewModel.applySettings(MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license))

        // Handle navigation through fragment transactions
        viewModel.navigationErrors.observe(this) { navError ->
            navError?.navigationErrorInfo?.let { errorInfo ->
                when (errorInfo.fragmentClass) {
                    HelpFragment::class.java -> {
                        // Create the analysis fragment, apply the arguments bundle and navigate
                        val analysisFragment = DocumentAnalysisFragment().apply {
                            arguments = documentAnalysisFragmentArguments
                        }

                        executeFragmentTransaction(analysisFragment)
                    }
                }
            }
        }

        // Create the help fragment, apply the arguments bundle and navigate
        val helpFragment = HelpFragment().apply {
            arguments = helpFragmentArguments
        }

        executeFragmentTransaction(helpFragment)
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
