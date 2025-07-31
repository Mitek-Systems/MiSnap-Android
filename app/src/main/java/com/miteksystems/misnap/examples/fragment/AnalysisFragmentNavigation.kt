package com.miteksystems.misnap.examples.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.miteksystems.misnap.apputil.LicenseFetcher
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapErrorResult
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel

/**
 * This example demonstrates an activity-less integration of the MiSnap SDK using Jetpack Navigation
 * navgraphs that is best suited for developers that follow the single-activity architecture and
 * don't want to launch another activity to invoke the MiSnap SDK.
 *
 * This example uses a [FragmentContainerView] that hosts a navgraph that in turn includes the built-in
 * [R.navigation.barcode_session_flow] navgraph to take care of the navigation for a barcode session
 * while making use of the [MiSnapWorkflowViewModel] to configure the session.
 *
 * NOTE: When working with the [MiSnapWorkflowViewModel] it is important to ensure that the view model
 *  is acquired through the activity's [ViewModelProvider].
 *
 * NOTE: Ensure that the provided license has all the necessary features enabled for the target
 *  MiSnap session.
 *
 * @see R.navigation.example_session_navigation for the navgraph definition and setup.
 *
 * @see com.miteksystems.misnap.examples.fragment.AnalysisFragmentTransaction for an activity-less
 * integration that doesn't use Jetpack Navigation and drives the navigation with [FragmentTransaction]s.
 */
class AnalysisFragmentNavigation : AppCompatActivity(R.layout.example_fragment_navigation) {

    /**
     * Fetch the Misnap SDK license.
     * Good practice: Handle the license in a way that it is remotely updatable.
     */
    private val license by lazy {  
        LicenseFetcher.fetch()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Build a [MiSnapSettings] object and apply it to the [MiSnapWorkflowViewModel] to use it
         * during the session.
         * The navigation in this example is handled by the [FragmentContainerView] in the XML layout
         * of this activity, it is important to apply the settings before navigating to the start
         * destination of the navgraph.
         */
        val settings = MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
            analysis.barcode.trigger = MiSnapSettings.Analysis.Barcode.Trigger.AUTO
        }

        ViewModelProvider(this)[MiSnapWorkflowViewModel::class.java].also { viewModel ->
            viewModel.applySettings(settings)

            /**
             * Observe the [MiSnapWorkflowViewModel.results] [LiveData] to receive the [MiSnapFinalResult]s
             * and handle them accordingly, e.g. by collecting the results or re-configuring the
             * [MiSnapWorkflowViewModel] for another session.
             */
            viewModel.results.observe(this) {
                it?.let { result ->
                    when (result) {
                        is MiSnapFinalResult.BarcodeSession -> {

                        }
                        else -> {}
                    }

                    // Once you're done handling the results, clear them before invoking a new session.
                    viewModel.clearResults()
                }
            }

            /**
             * Observe the [MiSnapWorkflowViewModel.error] [LiveData] to receive [MiSnapErrorResult]s
             * and handle them accordingly.
             */
            viewModel.error.observe(this) {
                it?.let {

                }
            }
        }
    }
}
