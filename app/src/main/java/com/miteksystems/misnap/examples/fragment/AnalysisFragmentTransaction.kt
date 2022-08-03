package com.miteksystems.misnap.examples.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleFragmentTransactionBinding
import com.miteksystems.misnap.workflow.MiSnapErrorResult
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.fragment.*

/**
 * This example demonstrates an activity-less integration of the MiSnap SDK using [FragmentTransaction]s
 * that is best suited for developers that follow the single-activity architecture, that don't want
 * to launch another activity to invoke the MiSnap SDK and that can't use Jetpack Navigation.
 *
 * Internally, the MiSnap SDK will always use Jetpack Navigation navgraphs to orchestrate the navigation
 * between fragments. When it's not possible to use Jetpack Navigation, the SDK will emit a
 * [NavigationError] through the [MiSnapWorkflowViewModel.navigationErrors] [LiveData] with the appropriate
 * context so that the navigation can be handled manually, in this case, through [FragmentTransaction]s.
 *
 * @see com.miteksystems.misnap.workflow.fragment for the list of available fragments included in the
 * MiSnap SDK.
 * @see com.miteksystems.misnap.examples.fragment.AnalysisFragmentNavigation for a simpler activity-less
 * integration that doesn't use [FragmentTransaction]s.
 */
class AnalysisFragmentTransaction : AppCompatActivity() {
    private val license = "your_sdk_license"

    private lateinit var binding: ExampleFragmentTransactionBinding

    private var tutorialFragmentHashCode: Int? = null

    private val viewModel by lazy {
        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java]
    }


    /**
     * Handle the incoming [NavigationError]s to determine the next destination and drive the navigation
     * using [FragmentTransaction]s.
     *
     * Use the [NavigationErrorInfo.fragmentClass] to determine which fragment the navigation error
     * and [NavigationError.action] originated from. This provides the context for the specific action
     * that triggered the navigation. Then, create the appropriate [FragmentTransaction] to manually
     * drive the navigation.
     *
     * In some situations, such as when there are multiple instances of the same fragment, you might
     * need an extra context to differentiate the specific instance that generated the [NavigationError],
     * if that's the case, use the [NavigationErrorInfo.hashCode] and store it temporarily to compare it
     * when another navigation error from the same type of fragment is received. E.g. when handling
     * [NavigationError]s from the [HelpFragment] which is used as tutorial/help screen.
     */
    private val navigationErrorsObserver = Observer<NavigationError?> { navError ->
        navError?.navigationErrorInfo?.let { errorInfo ->
            when (errorInfo.fragmentClass) {
                HelpFragment::class.java -> {
                    when (navError.action) {
                        is NavigationAction.Help.NavigateContinue -> {
                            if (tutorialFragmentHashCode == errorInfo.hashCode) {
                                // Handle a continue action from a tutorial screen.
                                executeFragmentTransaction(BarcodeAnalysisFragment())
                            } else {
                                // Handle a continue action from a help screen.
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

        /**
         * Build a [MiSnapSettings] object and apply it to the [MiSnapWorkflowViewModel] to use it
         * during the session. The settings must be applied before moving to the start destination of
         * the workflow.
         */
        viewModel.applySettings(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license))

        /**
         * Observe the [MiSnapWorkflowViewModel.navigationErrors] [LiveData] for [NavigationError]s and
         * handle them accordingly.
         */
        viewModel.navigationErrors.observe(this, navigationErrorsObserver)

        /**
         * Observe the [MiSnapWorkflowViewModel.results] [LiveData] for [MiSnapFinalResult]s and handle
         * them accordingly.
         */
        viewModel.results.observe(this) { result ->
            result?.let {
                when (it) {
                    is MiSnapFinalResult.BarcodeSession -> {
                    }
                }
            }
        }

        /**
         * Observe the [MiSnapWorkflowViewModel.error] [LiveData] for [MiSnapErrorResult]s and handle
         * them accordingly.
         */
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

    /**
     * Start a transaction to the first destination of the workflow, in this case the [HelpFragment].
     */
    private fun startSession() {
        val tutorialFragment = HelpFragment()
        tutorialFragmentHashCode = tutorialFragment.hashCode()
        executeFragmentTransaction(tutorialFragment)
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
