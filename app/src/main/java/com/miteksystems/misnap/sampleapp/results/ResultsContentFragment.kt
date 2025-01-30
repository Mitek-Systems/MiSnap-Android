package com.miteksystems.misnap.sampleapp.results

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.miteksystems.misnap.R
import com.miteksystems.misnap.apputil.ViewPageAdapter
import com.miteksystems.misnap.apputil.util.ViewUtil
import com.miteksystems.misnap.apputil.view.MiSnapAudioView
import com.miteksystems.misnap.apputil.view.MiSnapVideoView
import com.miteksystems.misnap.apputil.databinding.FragmentResultContentBinding
import com.miteksystems.misnap.apputil.util.ResultsUtil
import com.miteksystems.misnap.nfc.MiSnapNfcReader
import com.miteksystems.misnap.nfc.MiSnapNfcReader.ChipData.AuthenticationData
import com.miteksystems.misnap.voice.AudioUtil
import com.miteksystems.misnap.workflow.MiSnapFinalResult
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.ViewBindingUtil

class ResultsContentFragment : Fragment(R.layout.fragment_result_content) {
    private val binding by ViewBindingUtil.viewBinding(
        this,
        FragmentResultContentBinding::bind
    )

    private val viewPagerHandler = Handler(Looper.myLooper() ?: Looper.getMainLooper())

    override fun onResume() {
        super.onResume()

        val sampleAppViewModel =
            ViewModelProvider(requireActivity())[SampleAppViewModel::class.java]

        val adapter = ViewPageAdapter()

        sampleAppViewModel.results.getOrNull(requireArguments().getInt(RESULTS_INDEX))
            ?.let { miSnapWorkflowStepResult ->
                when (miSnapWorkflowStepResult) {
                    is MiSnapWorkflowStep.Result.Success -> {
                        when (val misnapResult = miSnapWorkflowStepResult.result) {
                            is MiSnapFinalResult.DocumentSession -> {
                                adapter.addView(
                                    ViewUtil.getFinalFrameView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        ViewUtil.getMiSnapVideoView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        ViewUtil.getWarningsView(
                                            misnapResult.warnings,
                                            requireContext()
                                        ),
                                        getString(R.string.misnapAppUtilResultsWarningsTabTitle)
                                    )
                                }

                                misnapResult.extraction?.let {
                                    adapter.addView(
                                        ViewUtil.getExtractionView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsExtractionTabTitle)
                                    )
                                }

                                misnapResult.classification?.let {
                                    adapter.addView(
                                        ViewUtil.getGenericTextView(
                                            it.documentType.name,
                                            requireContext()
                                        ),
                                        getString(R.string.misnapAppUtilResultsDocumentClassificationTabTitle)
                                    )
                                }

                                misnapResult.barcode?.let {
                                    adapter.addView(
                                        ViewUtil.getBarcodeView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsBarcodeTabTitle)
                                    )
                                }

                                adapter.addView(
                                    ViewUtil.getMiBiDataView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                )
                            }

                            is MiSnapFinalResult.BarcodeSession -> {
                                adapter.addView(
                                    ViewUtil.getFinalFrameView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        ViewUtil.getMiSnapVideoView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        ViewUtil.getWarningsView(
                                            misnapResult.warnings,
                                            requireContext()
                                        ),
                                        getString(R.string.misnapAppUtilResultsWarningsTabTitle)
                                    )
                                }

                                misnapResult.barcode?.let {
                                    adapter.addView(
                                        ViewUtil.getBarcodeView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsBarcodeTabTitle)
                                    )
                                }

                                adapter.addView(
                                    ViewUtil.getMiBiDataView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                )
                            }

                            is MiSnapFinalResult.FaceSession -> {
                                adapter.addView(
                                    ViewUtil.getFinalFrameView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        ViewUtil.getMiSnapVideoView(it, requireContext()),
                                        getString(R.string.misnapAppUtilResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        ViewUtil.getWarningsView(
                                            misnapResult.warnings,
                                            requireContext()
                                        ),
                                        getString(R.string.misnapAppUtilResultsWarningsTabTitle)
                                    )
                                }

                                adapter.addView(
                                    ViewUtil.getMiBiDataView(
                                        misnapResult.jpegImage,
                                        requireContext()
                                    ),
                                    getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                )
                            }

                            is MiSnapFinalResult.NfcSession -> {
                                adapter.addView(
                                    getNfcResultsView(misnapResult.nfcData),
                                    getString(R.string.misnapAppUtilResultsNfcResultsTabTitle)
                                )

                                val jpeg = getNfcImageByteArray(misnapResult.nfcData)

                                // In cases where the portrait image is skipped we get MiBI from the raw string.
                                if (jpeg.isNotEmpty()) {
                                    adapter.addView(
                                        ViewUtil.getMiBiDataView(jpeg, requireContext()),
                                        getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                    )
                                } else {
                                    adapter.addView(
                                        ViewUtil.getMiBiDataView(misnapResult.misnapMibiData.mibiData, requireContext()),
                                        getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                    )
                                }
                            }

                            is MiSnapFinalResult.VoiceSession -> {
                                misnapResult.voiceSamples.forEachIndexed { index, audio ->
                                    val voiceLayoutBinding =
                                        FragmentResultContentBinding.inflate(layoutInflater)

                                    voiceLayoutBinding.viewPager.also { pager ->
                                        pager.adapter = ViewPageAdapter().also { adapter ->
                                            adapter.addView(
                                                ViewUtil.getMiSnapAudioView(
                                                    // NOTE: This is for demo purposes only. Please do not boost wav file audio before sending to MobileVerify server.
                                                    AudioUtil.boostWavVolume(
                                                        audio,
                                                        25,
                                                        misnapResult.misnapMibiData[index]
                                                    ),
                                                    requireContext()
                                                ),
                                                getString(R.string.misnapAppUtilResultsVoiceAudioTabTitle)
                                            )
                                            adapter.addView(
                                                ViewUtil.getMiBiDataView(
                                                    misnapResult.misnapMibiData[index].mibiData,
                                                    requireContext()
                                                ),
                                                getString(R.string.misnapAppUtilResultsMibiTabTitle)
                                            )
                                        }

                                        pager.addOnPageChangeListener(
                                            object : SimpleOnPageChangeListener() {
                                                override fun onPageSelected(position: Int) {
                                                    super.onPageSelected(position)

                                                    onPageSelected(pager, position)
                                                }
                                            }
                                        )
                                    }

                                    voiceLayoutBinding.tabLayout.setupWithViewPager(
                                        voiceLayoutBinding.viewPager
                                    )

                                    adapter.addView(
                                        voiceLayoutBinding.root,
                                        String.format(
                                            getString(R.string.misnapAppUtilResultsVoiceTabTitle),
                                            index + 1
                                        )
                                    )
                                }
                            }
                        }
                    }

                    is MiSnapWorkflowStep.Result.Error -> {
                        adapter.addView(
                            ViewUtil.getGenericTextView(
                                getString(ResultsUtil.getErrorMessageId(miSnapWorkflowStepResult)),
                                requireContext(),
                                Gravity.CENTER
                            ),
                            getString(R.string.misnapAppUtilResultsErrorTabTitle)
                        )
                        adapter.addView(
                            ViewUtil.getMiBiDataView(
                                miSnapWorkflowStepResult.errorResult.misnapMibiData.mibiData,
                                requireContext()
                            ),
                            getString(R.string.misnapAppUtilResultsMibiTabTitle)
                        )
                    }
                }
            }

        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(
            object : SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    onPageSelected(binding.viewPager, position)
                }
            }
        )
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        viewPagerHandler.post {
            onPageSelected(binding.viewPager, 0)
        }
    }

    private fun onPageSelected(viewPager: ViewPager, position: Int) {
        viewPager.children.forEach { page ->
            page.findViewById<LinearLayout>(ViewPageAdapter.CONTENT_VIEW_ID)
                ?.getChildAt(0)
                ?.let { view ->
                    when (view) {
                        is MiSnapVideoView -> {
                            if (view.isPlaying) {
                                view.seekTo(0)
                                view.pause()
                            }
                        }

                        is MiSnapAudioView -> {
                            if (view.isPlaying) {
                                view.seekTo(0)
                                view.pause()
                            }
                            view.hideMediaControls()
                        }

                        else -> {
                            // search for inner viewpager and look for MiSnapVideoView to pause
                            view.findViewById<ViewPager>(R.id.viewPager)
                                ?.children?.forEach { viewPagerPage ->
                                    viewPagerPage.findViewById<LinearLayout>(ViewPageAdapter.CONTENT_VIEW_ID)
                                        ?.getChildAt(0)
                                        ?.let {
                                            if (it is MiSnapVideoView && it.isPlaying) {
                                                it.seekTo(0)
                                                it.pause()
                                            } else if (it is MiSnapAudioView) {
                                                if (it.isPlaying) {
                                                    it.seekTo(0)
                                                    it.pause()
                                                }
                                                it.hideMediaControls()
                                            }
                                        }
                                }
                        }
                    }
                }
        }

        viewPager.getChildAt(position)
            ?.findViewById<LinearLayout>(ViewPageAdapter.CONTENT_VIEW_ID)
            ?.getChildAt(0)
            ?.let { view ->
                when (view) {
                    is MiSnapVideoView -> {
                        view.start()
                    }

                    is MiSnapAudioView -> {
                        view.start()
                    }

                    is ViewGroup -> {
                        // search for inner viewpager and look for MiSnapVideoView to play
                        view.children.filterIsInstance<ViewPager>().toList()
                            .firstOrNull()?.let {
                                if (it.currentItem == 0) {
                                    onPageSelected(it, 0)
                                }
                            }
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
    }

    private fun getNfcImageByteArray(nfcResult: MiSnapNfcReader.ChipData) =
        when (nfcResult) {
            is MiSnapNfcReader.ChipData.Icao -> nfcResult.photo
            is MiSnapNfcReader.ChipData.EuDl -> nfcResult.photo
        }

    private fun getNfcResultsView(nfcResult: MiSnapNfcReader.ChipData): View {
        val nfcResultsView = layoutInflater.inflate(R.layout.nfc_results_page, null)

        when (nfcResult) {
            is MiSnapNfcReader.ChipData.Icao -> {
                setIcaoResults(nfcResult, nfcResultsView)
            }

            is MiSnapNfcReader.ChipData.EuDl -> {
                setEuDlResults(nfcResult, nfcResultsView)
            }
        }

        return nfcResultsView
    }

    private fun setIcaoResults(nfcData: MiSnapNfcReader.ChipData.Icao, nfcResultsView: View) {
        //Set the name and photo
        nfcResultsView.findViewById<TextView>(R.id.fullName).apply {
            text = "${nfcData.firstName} ${nfcData.lastName}"
        }

        if (nfcData.photo.isEmpty()) {
            nfcResultsView.findViewById<TextView>(R.id.nfcBioPhotoNotExtractedLabel).visibility = View.VISIBLE
            nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).visibility = View.GONE
        } else {
            nfcResultsView.findViewById<TextView>(R.id.nfcBioPhotoNotExtractedLabel).visibility = View.GONE
            nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).apply {
                setImageBitmap(BitmapFactory.decodeByteArray(nfcData.photo, 0, nfcData.photo.size))
            }
        }

        //Set the nfc data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcChipDataContainer).apply {
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcFirstNameLabel),
                nfcData.firstName
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcLastNameLabel),
                nfcData.lastName
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDocumentNumberLabel),
                nfcData.documentNumber
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDocumentCodeLabel),
                nfcData.documentCode
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcMrzLabel),
                nfcData.mrz
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcIssuingCountryLabel),
                nfcData.issuingCountry
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcNationalityLabel),
                nfcData.nationality
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcGenderLabel),
                nfcData.gender
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPlaceOfBirthLabel),
                nfcData.placeOfBirth
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDateOfBirthLabel),
                nfcData.dateOfBirth
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcExpirationDateLabel),
                nfcData.dateOfExpiry
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDateOfIssueLabel),
                nfcData.dateOfIssue
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcIssuingAuthorityLabel),
                nfcData.issuingAuthority
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcOtherNamesLabel),
                nfcData.otherNames?.joinToString()
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcAddressLabel),
                nfcData.address?.joinToString()
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcTelephoneLabel),
                nfcData.telephone
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPersonalNumberLabel),
                nfcData.personalNumber
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcProfessionLabel),
                nfcData.profession
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcTitleLabel),
                nfcData.title
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPersonalSummaryLabel),
                nfcData.personalSummary
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcOtherTravelDocNumbersLabel),
                nfcData.otherTravelDocumentNumbers?.joinToString()
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcCustodyInfoLabel),
                nfcData.custodyInfo
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDataGroupsReadLabel),
                nfcData.dataGroupsRead.joinToString()
            )
        }

        //Set the auth data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcAuthDataContainer).apply {
            val authData: AuthenticationData = nfcData.authenticationData
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcSODPresentLabel),
                (authData.sod.isNotEmpty()).toString()
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDataGroupsForPassiveAuthenticationLabel),
                authData.dataGroups.keys.joinToString(separator = "\n")
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcActiveAuthenticationSupportedLabel),
                (authData.activeAuthInfo != null).toString()
            )
        }
    }

    private fun setEuDlResults(nfcData: MiSnapNfcReader.ChipData.EuDl, nfcResultsView: View) {
        //Set the name and photo
        nfcResultsView.findViewById<TextView>(R.id.fullName).apply {
            text = "${nfcData.firstName} ${nfcData.lastName}"
        }

        if (nfcData.photo.isEmpty()) {
            nfcResultsView.findViewById<TextView>(R.id.nfcBioPhotoNotExtractedLabel).visibility = View.VISIBLE
            nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).visibility = View.GONE
        } else {
            nfcResultsView.findViewById<TextView>(R.id.nfcBioPhotoNotExtractedLabel).visibility = View.GONE
            nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).apply {
                setImageBitmap(BitmapFactory.decodeByteArray(nfcData.photo, 0, nfcData.photo.size))
            }
        }

        //Set the nfc data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcChipDataContainer).apply {
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcFirstNameLabel),
                nfcData.firstName
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcLastNameLabel),
                nfcData.lastName
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDocumentNumberLabel),
                nfcData.documentNumber
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDocumentCodeLabel),
                nfcData.documentCode
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcMrzLabel),
                nfcData.mrz
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcIssuingCountryLabel),
                nfcData.issuingCountry
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcNationalityLabel),
                nfcData.nationality
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcGenderLabel),
                nfcData.gender
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPlaceOfBirthLabel),
                nfcData.placeOfBirth
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDateOfBirthLabel),
                nfcData.dateOfBirth
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcExpirationDateLabel),
                nfcData.dateOfExpiry
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDateOfIssueLabel),
                nfcData.dateOfIssue
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcIssuingAuthorityLabel),
                nfcData.issuingAuthority
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPermanentPlaceOfResidenceLabel),
                nfcData.permanentPlaceOfResidence
            )
            ViewUtil.addNfcDataEntry(requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcVehicleCategoriesLabel),
                nfcData.vehicleCategories?.joinToString(separator = "\n") { it.toString() })
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcPersonalNumberLabel),
                nfcData.personalNumber
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDataGroupsReadLabel),
                nfcData.dataGroupsRead.joinToString()
            )
        }

        //Set the auth data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcAuthDataContainer).apply {
            val authData: AuthenticationData = nfcData.authenticationData
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcSODPresentLabel),
                (authData.sod.isNotEmpty()).toString()
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcDataGroupsForPassiveAuthenticationLabel),
                authData.dataGroups.keys.joinToString(separator = "\n")
            )
            ViewUtil.addNfcDataEntry(
                requireContext(),
                this,
                getString(R.string.misnapAppUtilResultsNfcActiveAuthenticationSupportedLabel),
                (authData.activeAuthInfo != null).toString()
            )
        }
    }

    override fun onStop() {
        super.onStop()
        cleanup()
    }

    private fun cleanup() {
        viewPagerHandler.removeCallbacksAndMessages(null)
        binding.viewPager.removeAllViews()
        binding.tabLayout.removeAllTabs()
        binding.tabLayout.setOnClickListener(null)
        binding.viewPager.setOnClickListener(null)
        binding.viewPager.clearOnPageChangeListeners()
    }

    companion object {
        const val RESULTS_INDEX = "RESULTS_INDEX"
    }
}
