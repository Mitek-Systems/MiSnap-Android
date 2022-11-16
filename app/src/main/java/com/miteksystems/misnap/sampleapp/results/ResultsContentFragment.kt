package com.miteksystems.misnap.sampleapp.results

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.view.children
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.miteksystems.misnap.R
import com.miteksystems.misnap.controller.MiSnapController.FeedbackResult.UserAction
import com.miteksystems.misnap.core.Barcode
import com.miteksystems.misnap.core.Mrz
import com.miteksystems.misnap.core.Mrz1Line
import com.miteksystems.misnap.core.MrzData
import com.miteksystems.misnap.databinding.FragmentResultContentBinding
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
                                    getTouchImageView(misnapResult.jpegImage),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        getMiSnapVideoView(it),
                                        getString(R.string.misnapSampleAppResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        getWarningsView(misnapResult.warnings),
                                        getString(R.string.misnapSampleAppResultsWarningsTabTitle)
                                    )
                                }

                                misnapResult.mrz?.let {
                                    adapter.addView(
                                        getMrzTextView(it),
                                        getString(R.string.misnapSampleAppResultsMrzTabTitle)
                                    )
                                }

                                misnapResult.barcode?.let {
                                    adapter.addView(
                                        getBarcodeView(it),
                                        getString(R.string.misnapSampleAppResultsBarcodeTabTitle)
                                    )
                                }

                                adapter.addView(
                                    getMiBiDataView(misnapResult.mibiData),
                                    getString(R.string.misnapSampleAppResultsMibiTabTitle)
                                )
                            }
                            is MiSnapFinalResult.BarcodeSession -> {
                                adapter.addView(
                                    getTouchImageView(misnapResult.jpegImage),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        getMiSnapVideoView(it),
                                        getString(R.string.misnapSampleAppResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        getWarningsView(misnapResult.warnings),
                                        getString(R.string.misnapSampleAppResultsWarningsTabTitle)
                                    )
                                }

                                misnapResult.barcode?.let {
                                    adapter.addView(
                                        getBarcodeView(it),
                                        getString(R.string.misnapSampleAppResultsBarcodeTabTitle)
                                    )
                                }

                                adapter.addView(
                                    getMiBiDataView(misnapResult.mibiData),
                                    getString(R.string.misnapSampleAppResultsMibiTabTitle)
                                )
                            }
                            is MiSnapFinalResult.FaceSession -> {
                                adapter.addView(
                                    getTouchImageView(misnapResult.jpegImage),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.video?.let {
                                    adapter.addView(
                                        getMiSnapVideoView(it),
                                        getString(R.string.misnapSampleAppResultsVideoTabTitle)
                                    )
                                }

                                if (misnapResult.warnings.isNotEmpty()) {
                                    adapter.addView(
                                        getWarningsView(misnapResult.warnings),
                                        getString(R.string.misnapSampleAppResultsWarningsTabTitle)
                                    )
                                }

                                adapter.addView(
                                    getMiBiDataView(misnapResult.mibiData),
                                    getString(R.string.misnapSampleAppResultsMibiTabTitle)
                                )
                            }
                            is MiSnapFinalResult.NfcSession -> {
                                adapter.addView(
                                    getNfcResultsView(misnapResult.nfcData),
                                    getString(R.string.misnapSampleAppResultsNfcResultsTabTitle)
                                )

                                adapter.addView(
                                    getMiBiDataView(misnapResult.mibiData),
                                    getString(R.string.misnapSampleAppResultsMibiTabTitle)
                                )
                            }
                            is MiSnapFinalResult.VoiceSession -> {
                                misnapResult.voiceSamples.forEachIndexed { index, audio ->
                                    val voiceLayoutBinding =
                                        FragmentResultContentBinding.inflate(layoutInflater)

                                    voiceLayoutBinding.viewPager.also { pager ->
                                        pager.adapter = ViewPageAdapter().also { adapter ->
                                            adapter.addView(
                                                getMiSnapAudioView(
                                                    // NOTE: This is for demo purposes only. Please do not boost wav file audio before sending to MobileVerify server.
                                                    AudioUtil.boostWavVolume(audio, 25, misnapResult.mibiData[index])
                                                ),
                                                getString(R.string.misnapSampleAppResultsVoiceAudioTabTitle)
                                            )
                                            adapter.addView(
                                                getMiBiDataView(misnapResult.mibiData[index]),
                                                getString(R.string.misnapSampleAppResultsMibiTabTitle)
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
                                            getString(R.string.misnapSampleAppResultsVoiceTabTitle),
                                            index + 1
                                        )
                                    )
                                }
                            }
                        }
                    }
                    is MiSnapWorkflowStep.Result.Error -> {
                        adapter.addView(
                            getGenericTextView(
                                getString(getErrorMessageId(miSnapWorkflowStepResult)),
                                Gravity.CENTER
                            ),
                            getString(R.string.misnapSampleAppResultsErrorTabTitle)
                        )
                        adapter.addView(
                            getMiBiDataView(miSnapWorkflowStepResult.errorResult.mibiData),
                            getString(R.string.misnapSampleAppResultsMibiTabTitle)
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

        binding.viewPager.post {
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
                                view.pause()
                            }
                        }
                        is MiSnapAudioView -> {
                            if (view.isPlaying) {
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
                                                it.pause()
                                            } else if (it is MiSnapAudioView) {
                                                if (it.isPlaying) {
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
                        view.showMediaControls()
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

        nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).apply {
            setImageBitmap(BitmapFactory.decodeByteArray(nfcData.photo, 0, nfcData.photo.size))
        }

        //Set the nfc data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcChipDataContainer).apply {
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcFirstNameLabel),
                nfcData.firstName
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcLastNameLabel),
                nfcData.lastName
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDocumentNumberLabel),
                nfcData.documentNumber
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDocumentCodeLabel),
                nfcData.documentCode
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcMrzLabel),
                nfcData.mrz
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcIssuingCountryLabel),
                nfcData.issuingCountry
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcNationalityLabel),
                nfcData.nationality
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcGenderLabel),
                nfcData.gender
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPlaceOfBirthLabel),
                nfcData.placeOfBirth
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDateOfBirthLabel),
                nfcData.dateOfBirth
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcExpirationDateLabel),
                nfcData.dateOfExpiry
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDateOfIssueLabel),
                nfcData.dateOfIssue
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcIssuingAuthorityLabel),
                nfcData.issuingAuthority
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcOtherNamesLabel),
                nfcData.otherNames?.joinToString()
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcAddressLabel),
                nfcData.address?.joinToString()
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcTelephoneLabel),
                nfcData.telephone
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPersonalNumberLabel),
                nfcData.personalNumber
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcProfessionLabel),
                nfcData.profession
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcTitleLabel),
                nfcData.title
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPersonalSummaryLabel),
                nfcData.personalSummary
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcOtherTravelDocNumbersLabel),
                nfcData.otherTravelDocumentNumbers?.joinToString()
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcCustodyInfoLabel),
                nfcData.custodyInfo
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDataGroupsReadLabel),
                nfcData.dataGroupsRead.joinToString()
            )
        }

        //Set the auth data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcAuthDataContainer).apply {
            val authData: AuthenticationData = nfcData.authenticationData
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcSODPresentLabel),
                (authData.sod.isNotEmpty()).toString()
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDataGroupsForPassiveAuthenticationLabel),
                authData.dataGroups.keys.joinToString(separator = "\n")
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcActiveAuthenticationSupportedLabel),
                (authData.activeAuthInfo != null).toString()
            )
        }
    }

    private fun setEuDlResults(nfcData: MiSnapNfcReader.ChipData.EuDl, nfcResultsView: View) {
        //Set the name and photo
        nfcResultsView.findViewById<TextView>(R.id.fullName).apply {
            text = "${nfcData.firstName} ${nfcData.lastName}"
        }

        nfcResultsView.findViewById<ImageView>(R.id.nfcBioPhoto).apply {
            setImageBitmap(BitmapFactory.decodeByteArray(nfcData.photo, 0, nfcData.photo.size))
        }

        //Set the nfc data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcChipDataContainer).apply {
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcFirstNameLabel),
                nfcData.firstName
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcLastNameLabel),
                nfcData.lastName
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDocumentNumberLabel),
                nfcData.documentNumber
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDocumentCodeLabel),
                nfcData.documentCode
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcMrzLabel),
                nfcData.mrz
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcIssuingCountryLabel),
                nfcData.issuingCountry
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcNationalityLabel),
                nfcData.nationality
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcGenderLabel),
                nfcData.gender
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPlaceOfBirthLabel),
                nfcData.placeOfBirth
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDateOfBirthLabel),
                nfcData.dateOfBirth
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcExpirationDateLabel),
                nfcData.dateOfExpiry
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDateOfIssueLabel),
                nfcData.dateOfIssue
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcIssuingAuthorityLabel),
                nfcData.issuingAuthority
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPermanentPlaceOfResidenceLabel),
                nfcData.permanentPlaceOfResidence
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcVehicleCategoriesLabel),
                nfcData.vehicleCategories?.joinToString(separator = "\n") { it.toString() })
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcPersonalNumberLabel),
                nfcData.personalNumber
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDataGroupsReadLabel),
                nfcData.dataGroupsRead.joinToString()
            )
        }

        //Set the auth data
        nfcResultsView.findViewById<LinearLayout>(R.id.nfcAuthDataContainer).apply {
            val authData: AuthenticationData = nfcData.authenticationData
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcSODPresentLabel),
                (authData.sod.isNotEmpty()).toString()
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcDataGroupsForPassiveAuthenticationLabel),
                authData.dataGroups.keys.joinToString(separator = "\n")
            )
            addNfcDataEntry(
                this,
                getString(R.string.misnapSampleAppResultsNfcActiveAuthenticationSupportedLabel),
                (authData.activeAuthInfo != null).toString()
            )
        }
    }

    private fun addNfcDataEntry(layout: LinearLayout, title: String, content: String?) {
        if (content == null || content.isEmpty())
            return

        val titleView = MaterialTextView(requireActivity()).apply {
            setTypeface(null, Typeface.BOLD)
            text = title
            setTextIsSelectable(true)
        }

        val contentView = MaterialTextView(requireActivity())
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            contentView, 12, 30, 1,
            TypedValue.COMPLEX_UNIT_DIP
        )
        contentView.text = content
        contentView.setPadding(
            0, 0, 0, TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8F, resources.displayMetrics
            ).toInt()
        )
        contentView.setTextIsSelectable(true)
        layout.addView(titleView)
        layout.addView(contentView)
    }

    private fun getTouchImageView(byteImage: ByteArray) =
        TouchImageView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = false
                inSampleSize = 1 // downscale by 2 as it's just a review
            }

            setMaxZoom(4f)
            setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size, options))
        }

    private fun getMiSnapVideoView(data: ByteArray) =
        MiSnapVideoView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            MediaController(requireContext()).also {
                it.setAnchorView(this)
                setVideoData(data)
                setMediaController(it)
            }
        }

    private fun getMiSnapAudioView(data: ByteArray) =
        MiSnapAudioView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            MediaController(requireContext(), false).also {
                setAudioData(data)
                setMediaController(it)
            }
        }

    private fun getMrzTextView(mrz: Mrz) =
        MaterialTextView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER
            textSize = 25f
            when (mrz) {
                is MrzData -> {
                    text = HtmlCompat.fromHtml(
                        String.format(
                            getString(R.string.misnapSampleAppResultsMrzData),
                            mrz.documentNumber,
                            mrz.documentCode,
                            mrz.country,
                            mrz.dateOfBirth,
                            mrz.dateOfExpiry,
                            mrz.optionalData1
                        ),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                }
                is Mrz1Line -> {
                    text = HtmlCompat.fromHtml(
                        String.format(
                            getString(R.string.misnapSampleAppResultsMrzOneLineData),
                            mrz.mrzString
                        ),
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                }
            }
            setTextIsSelectable(true)
        }

    private fun getBarcodeView(barcode: Barcode) =
        LinearLayout(requireActivity()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val activityMarginVertical =
                    resources.getDimension(R.dimen.misnapSampleAppLayoutVerticalMargin).toInt()
                val activityMarginHorizontal =
                    resources.getDimension(R.dimen.misnapSampleAppLayoutHorizontalMargin)
                        .toInt()
                setMargins(
                    activityMarginHorizontal,
                    activityMarginVertical,
                    activityMarginHorizontal,
                    activityMarginVertical
                )
            }
            orientation = LinearLayout.VERTICAL
            barcode.type?.let {
                addView(
                    MaterialTextView(requireActivity()).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 25f
                        text = getString(R.string.misnapSampleAppResultsBarcodeType, it.name)
                        setTextIsSelectable(true)
                    }
                )
            }
            barcode.encodedBarcode?.let {
                addView(
                    MaterialTextView(requireActivity()).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        gravity = Gravity.START
                        textSize = 25f
                        text = getString(R.string.misnapSampleAppResultsBarcodeData, it)
                        setTextIsSelectable(true)
                    }
                )
            }
        }

    private fun getWarningsView(warnings: List<UserAction>) =
        LinearLayout(requireActivity()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val activityMarginVertical =
                    resources.getDimension(R.dimen.misnapSampleAppLayoutVerticalMargin).toInt()
                val activityMarginHorizontal =
                    resources.getDimension(R.dimen.misnapSampleAppLayoutHorizontalMargin)
                        .toInt()
                setMargins(
                    activityMarginHorizontal,
                    activityMarginVertical,
                    activityMarginHorizontal,
                    activityMarginVertical
                )
            }
            orientation = LinearLayout.VERTICAL
            warnings.forEach { userAction ->
                addView(
                    MaterialTextView(requireActivity()).apply {
                        layoutParams = ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ).apply {
                            val defaultMargin =
                                resources.getDimension(R.dimen.misnapSampleAppSpaceDefault)
                                    .toInt()
                            setMargins(0, 0, 0, defaultMargin)
                        }

                        val defaultMarginDouble =
                            resources.getDimension(R.dimen.misnapSampleAppSpaceLarge).toInt()
                        setPadding(defaultMarginDouble, 0, 0, 0)
                        background = AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.misnap_list_background
                        )
                        gravity = Gravity.START
                        textSize = 16f
                        text = userAction.toString()
                        setTextIsSelectable(true)
                    }
                )
            }
        }

    private fun getGenericTextView(content: String, layoutGravity: Int = Gravity.START) =
        MaterialTextView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = layoutGravity
            textSize = 16f
            text = content
            setTextIsSelectable(true)
        }

    private fun getMiBiDataView(mibiData: String) =
        LinearLayout(requireActivity()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            addView(MaterialButton(requireActivity()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = getString(R.string.misnapSampleAppResultsMibiCopyButtonLabel)
                setOnClickListener {
                    try {
                        (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).apply {
                            setPrimaryClip(
                                ClipData.newPlainText(
                                    getString(R.string.misnapSampleAppName),
                                    mibiData
                                )
                            )
                        }
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.misnapSampleAppResultsMibiCopySuccessMessage),
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.misnapSampleAppResultsMibiCopyFailureMessage),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })

            addView(JsonView(context).apply {
                isVerticalScrollBarEnabled = true
                this.setJson(mibiData)
            })
        }

    override fun onStop() {
        super.onStop()
        cleanup()
    }

    private fun cleanup() {
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
