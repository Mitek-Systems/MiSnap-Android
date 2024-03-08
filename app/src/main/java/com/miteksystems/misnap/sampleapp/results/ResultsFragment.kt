package com.miteksystems.misnap.sampleapp.results

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.ViewBindingUtil
import com.miteksystems.misnap.R
import com.miteksystems.misnap.apputil.util.ResultsUtil
import com.miteksystems.misnap.apputil.databinding.FragmentResultsRootBinding

class ResultsFragment : Fragment(R.layout.fragment_results_root) {
    private val binding by ViewBindingUtil.viewBinding(
        this,
        FragmentResultsRootBinding::bind
    )
    private val sampleAppViewModel by lazy { ViewModelProvider(requireActivity())[SampleAppViewModel::class.java] }

    override fun onResume() {
        super.onResume()

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.navigateContinue)
        }

        binding.viewPager.adapter = FragmentResultsPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private inner class FragmentResultsPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {
        private val titles = sampleAppViewModel.results.map {
            getTitleForMiSnapResult(it)
        }

        override fun getCount(): Int = sampleAppViewModel.results.size

        override fun getItem(position: Int): Fragment {
            return ResultsContentFragment().apply {
                arguments = Bundle().apply {
                    putInt(ResultsContentFragment.RESULTS_INDEX, position)
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles.getOrNull(position) ?: super.getPageTitle(position)
        }

        private fun getTitleForMiSnapResult(miSnapWorkflowStepResult: MiSnapWorkflowStep.Result) =
            when (miSnapWorkflowStepResult) {
                is MiSnapWorkflowStep.Result.Success -> {
                    when (val misnapResult = miSnapWorkflowStepResult.result) {
                        is MiSnapFinalResult.BarcodeSession -> {
                            ResultsUtil.getUseCaseName(
                                misnapResult.misnapMibiData.mibiData,
                                requireContext()
                            )
                        }

                        is MiSnapFinalResult.DocumentSession -> {
                            ResultsUtil.getUseCaseName(
                                misnapResult.misnapMibiData.mibiData,
                                requireContext()
                            )
                        }

                        is MiSnapFinalResult.FaceSession -> {
                            ResultsUtil.getUseCaseName(
                                misnapResult.misnapMibiData.mibiData,
                                requireContext()
                            )
                        }

                        is MiSnapFinalResult.NfcSession -> {
                            ResultsUtil.getUseCaseName(
                                misnapResult.misnapMibiData.mibiData,
                                requireContext()
                            )
                        }

                        is MiSnapFinalResult.VoiceSession -> {
                            ResultsUtil.getUseCaseName(
                                misnapResult.misnapMibiData.first().mibiData,
                                requireContext()
                            )
                        }
                    }
                }

                is MiSnapWorkflowStep.Result.Error -> {
                    ResultsUtil.getUseCaseName(
                        miSnapWorkflowStepResult.errorResult.misnapMibiData.mibiData,
                        requireContext()
                    )
                }
            }
    }

    override fun onStop() {
        super.onStop()
        cleanup()
    }

    private fun cleanup() {
        binding.viewPager.removeAllViews()
        binding.tabLayout.removeAllTabs()
        binding.continueButton.setOnClickListener(null)
        binding.viewPager.clearOnPageChangeListeners()
    }
}
