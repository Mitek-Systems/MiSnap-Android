package com.miteksystems.misnap.sampleapp.results

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.ViewBindingUtil
import com.miteksystems.misnap.R
import com.miteksystems.misnap.databinding.FragmentResultsRootBinding
import org.json.JSONException
import org.json.JSONObject

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
                            getUseCaseName(misnapResult.mibiData)
                        }
                        is MiSnapFinalResult.DocumentSession -> {
                            getUseCaseName(misnapResult.mibiData)
                        }
                        is MiSnapFinalResult.FaceSession -> {
                            getUseCaseName(misnapResult.mibiData)
                        }
                        is MiSnapFinalResult.NfcSession -> {
                            getUseCaseName(misnapResult.mibiData)
                        }
                        is MiSnapFinalResult.VoiceSession -> {
                            getUseCaseName(misnapResult.mibiData.first())
                        }
                    }
                }
                is MiSnapWorkflowStep.Result.Error -> {
                    getString(R.string.misnapSampleAppResultsUseCaseUnknownTabTitle)
                }
            }
    }

    private fun getUseCaseName(mibiData: String) =
        try {
            val useCase = MiSnapSettings.UseCase.valueOf(
                JSONObject(mibiData).getJSONObject("PlatformPrivate").getJSONObject("OriginalSettings")
                    .getString("useCase")
            )

            when (useCase) {
                MiSnapSettings.UseCase.PASSPORT -> {
                    getString(R.string.misnapSampleAppResultsUseCasePassportTabTitle)
                }
                MiSnapSettings.UseCase.ID_FRONT -> {
                    getString(R.string.misnapSampleAppResultsUseCaseIdFrontTabTitle)
                }
                MiSnapSettings.UseCase.ID_BACK -> {
                    getString(R.string.misnapSampleAppResultsUseCaseIdBackTabTitle)
                }
                MiSnapSettings.UseCase.CHECK_FRONT -> {
                    getString(R.string.misnapSampleAppResultsUseCaseCheckFrontTabTitle)
                }
                MiSnapSettings.UseCase.CHECK_BACK -> {
                    getString(R.string.misnapSampleAppResultsUseCaseCheckBackTabTitle)
                }
                MiSnapSettings.UseCase.GENERIC_DOCUMENT -> {
                    getString(R.string.misnapSampleAppResultsUseCaseGenericDocumentTabTitle)
                }
                MiSnapSettings.UseCase.BARCODE -> {
                    getString(R.string.misnapSampleAppResultsUseCaseBarcodeTabTitle)
                }
                MiSnapSettings.UseCase.FACE -> {
                    getString(R.string.misnapSampleAppResultsUseCaseFaceTabTitle)
                }
                MiSnapSettings.UseCase.NFC -> {
                    getString(R.string.misnapSampleAppResultsUseCaseNfcTabTitle)
                }
                MiSnapSettings.UseCase.VOICE -> {
                    getString(R.string.misnapSampleAppResultsUseCaseVoiceTabTitle)
                }
            }
        } catch (e: JSONException) {
            getString(R.string.misnapSampleAppResultsUseCaseUnknownTabTitle)
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
