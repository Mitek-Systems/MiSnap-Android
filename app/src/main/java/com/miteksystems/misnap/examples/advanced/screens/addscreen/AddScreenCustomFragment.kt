package com.miteksystems.misnap.examples.advanced.screens.addscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.databinding.ExampleAddScreenCustomFragmentBinding
import com.miteksystems.misnap.workflow.util.ViewBindingUtil

/**
 * This advanced example demonstrates the use of a custom fragment with a custom UI that can be used
 * to add an additional screen to an existing flow through a custom navgraph.
 * This Fragment executes the "navigateContinue" action defined in the custom navgraph to move to the
 * next screen, in this case, the DocumentAnalysisFragment.
 *
 * @see R.navigation.example_add_screen_navigation.xml for the navgraph definition and setup.
 */
class AddScreenCustomFragment : Fragment(R.layout.example_add_screen_custom_fragment) {
    private val binding by ViewBindingUtil
        .viewBinding(this, ExampleAddScreenCustomFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.navigateContinue)
        }
    }
}
