package com.miteksystems.misnap.examples.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleFragmentTransactionBinding
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment
import com.miteksystems.misnap.workflow.fragment.DocumentAnalysisFragment.ReviewCondition
import com.miteksystems.misnap.workflow.fragment.HelpFragment
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.workflow.fragment.NavigationError

/**
 * This example demonstrates the customization of the MiSnap SDK UI and behavior through the use
 * of a [Bundle] containing the fragment arguments. This type of customization is best suited for
 * developers that will use the built-in fragments directly.
 *
 * Please refer to the "buildFragmentArguments" method of the fragment you want to customize for the
 * full list of customization options.
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see com.miteksystems.misnap.examples.settings.WorkflowSettings for an example on how to customize
 * the UI and behavior using [MiSnapSettings] instead.
 */
class FragmentArguments : AppCompatActivity() {
    private val license = "your_sdk_license"
    private lateinit var binding: ExampleFragmentTransactionBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExampleFragmentTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        /**
         * Use the method "buildFragmentArguments" from the workflow fragment you want to customize
         * to create a [Bundle] that can be used in a standard [FragmentTransaction].
         */
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


        /**
         * Handle the incoming [NavigationError]s to determine the next destination and drive the navigation
         * using [FragmentTransaction]s. Apply the customized [Bundle] to the fragment before executing
         * the transaction.
         */
        viewModel.navigationErrors.observe(this) { navError ->
            navError?.navigationErrorInfo?.let { errorInfo ->
                when (errorInfo.fragmentClass) {
                    HelpFragment::class.java -> {
                        val analysisFragment = DocumentAnalysisFragment().apply {
                            arguments = documentAnalysisFragmentArguments
                        }

                        executeFragmentTransaction(analysisFragment)
                    }
                }
            }
        }

        /**
         * Create the fragment, apply the arguments bundle and start a transaction to the first
         * destination of the workflow, in this case the [HelpFragment].
         */
        val helpFragment = HelpFragment().apply {
            arguments = helpFragmentArguments
        }

        executeFragmentTransaction(helpFragment)
    }

    /**
     * Create and execute [FragmentTransaction]s to drive the navigation manually.
     */
    private fun executeFragmentTransaction(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(binding.fragmentContainer.id, fragment)
            }
            .commit()
    }
}
