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
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.miteksystems.misnap.R
import com.miteksystems.misnap.controller.MiSnapController.FeedbackResult.UserAction
import com.miteksystems.misnap.core.Barcode
import com.miteksystems.misnap.core.Mrz
import com.miteksystems.misnap.core.Mrz1Line
import com.miteksystems.misnap.core.MrzData
import com.miteksystems.misnap.databinding.FragmentResultContentBinding
import com.miteksystems.misnap.nfc.NfcReader
import com.miteksystems.misnap.nfc.NfcReader.ChipData.AuthenticationData
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
                                    getTouchImageView(misnapResult.frame),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.videoRecording?.let {
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
                                    getTouchImageView(misnapResult.frame),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.videoRecording?.let {
                                    adapter.addView(
                                        getMiSnapVideoView(it),
                                        getString(R.string.misnapSampleAppResultsVideoTabTitle)
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
                                    getTouchImageView(misnapResult.frame),
                                    getString(R.string.misnapSampleAppResultsFrameTabTitle)
                                )

                                misnapResult.videoRecording?.let {
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

        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.viewPager.getChildAt(position)
                    ?.findViewById<LinearLayout>(ViewPageAdapter.CONTENT_VIEW_ID)
                    ?.getChildAt(0)?.let {
                        if (it is MiSnapVideoView) {
                            it.start()
                        }
                    }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        binding.tabLayout.setupWithViewPager(binding.viewPager)

    }

    private fun getNfcResultsView(nfcResult: NfcReader.ChipData): View {
        val nfcResultsView = layoutInflater.inflate(R.layout.nfc_results_page, null)

        when (nfcResult) {
            is NfcReader.ChipData.Icao -> {
                setIcaoResults(nfcResult, nfcResultsView)
            }
            is NfcReader.ChipData.EuDl -> {
                setEuDlResults(nfcResult, nfcResultsView)
            }
        }

        return nfcResultsView
    }

    private fun setIcaoResults(nfcData: NfcReader.ChipData.Icao, nfcResultsView: View) {
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

    private fun setEuDlResults(nfcData: NfcReader.ChipData.EuDl, nfcResultsView: View) {
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
            val mediaController = MediaController(requireContext())
            mediaController.setAnchorView(this)
            setVideoData(data)
            setMediaController(mediaController)
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
                            mrz.dateOfExpiry
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
                    resources.getDimension(R.dimen.misnapSampleAppLayoutHorizontalMargin).toInt()
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
                    resources.getDimension(R.dimen.misnapSampleAppLayoutHorizontalMargin).toInt()
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
                                resources.getDimension(R.dimen.misnapSampleAppSpaceDefault).toInt()
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

    companion object {
        const val RESULTS_INDEX = "RESULTS_INDEX"
    }
}
