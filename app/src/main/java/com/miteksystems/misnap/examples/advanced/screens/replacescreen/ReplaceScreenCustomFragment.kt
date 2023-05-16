package com.miteksystems.misnap.examples.advanced.screens.replacescreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.databinding.ExampleReplaceScreenCustomFragmentBinding
import com.miteksystems.misnap.workflow.util.ViewBindingUtil

/**
 * This advanced example demonstrates the use of a custom fragment with a custom UI that can be used
 * as a replacement of a screen in an existing flow through a custom navgraph.
 * This Fragment executes the "navigateContinue" action defined in the custom navgraph to move to the
 * next screen, in this case, the DocumentAnalysisFragment.
 *
 * @see R.navigation.example_replace_screen_navigation.xml for the navgraph definition and setup.
 */
class ReplaceScreenCustomFragment : Fragment(R.layout.example_replace_screen_custom_fragment) {
    private val binding by ViewBindingUtil
        .viewBinding(this, ExampleReplaceScreenCustomFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.navigateContinue)
        }
    }
}
