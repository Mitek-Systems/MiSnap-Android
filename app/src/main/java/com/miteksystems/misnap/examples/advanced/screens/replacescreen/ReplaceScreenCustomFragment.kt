package com.miteksystems.misnap.examples.advanced.screens.replacescreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.ExampleReplaceScreenCustomFragmentBinding
import com.miteksystems.misnap.workflow.fragment.MiSnapWorkflowViewModel
import com.miteksystems.misnap.workflow.util.ViewBindingUtil

/**
 * This advanced example demonstrates the use of a custom fragment with a custom UI that can be used
 * as a replacement of a screen in an existing flow through a custom navgraph.
 *
 * This Fragment is a replacement for the default FailoverFragment which:
 * * Obtains the current settings applied to the MiSnap session from the [MiSnapWorkflowViewModel].
 * * Handles the "retry in auto/manual" by updating the session trigger.
 * * Applies the updated settings to the [MiSnapWorkflowViewModel].
 * * Handles the navigation action for the MiSnap SDK to resume the session in the DocumentAnalysisFragment.
 *
 * @see R.navigation.example_replace_screen_navigation.xml for the navgraph definition and setup.
 */
class ReplaceScreenCustomFragment : Fragment(R.layout.example_replace_screen_custom_fragment) {
    private val binding by ViewBindingUtil
        .viewBinding(this, ExampleReplaceScreenCustomFragmentBinding::bind)

    private val misnapWorkflowViewModel: MiSnapWorkflowViewModel by lazy {
        ViewModelProvider(requireActivity())[MiSnapWorkflowViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get a reference to the current MiSnapSettings object
        misnapWorkflowViewModel.settings.value?.let { settings ->
            binding.retryAutoButton.setOnClickListener {
                // Apply the AUTO trigger to the settings object for a Document session
                settings.analysis.document.trigger =
                    MiSnapSettings.Analysis.Document.Trigger.AUTO

                // Apply the updated settings to the current session
                misnapWorkflowViewModel.applySettings(settings)

                // Handle the navigation action
                findNavController().navigate(R.id.navigateAutoSession)
            }


            binding.retryManualButton.setOnClickListener {
                // Apply the MANUAL trigger to the settings object for a Document session
                settings.analysis.document.trigger =
                    MiSnapSettings.Analysis.Document.Trigger.MANUAL

                // Apply the updated settings to the current session
                misnapWorkflowViewModel.applySettings(settings)

                // Handle the navigation action
                findNavController().navigate(R.id.navigateManualSession)
            }
        }
    }
}
