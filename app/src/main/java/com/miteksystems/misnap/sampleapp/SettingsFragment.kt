package com.miteksystems.misnap.sampleapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.barcode.default
import com.miteksystems.misnap.barcode.getScanSpeed
import com.miteksystems.misnap.camera.util.CameraUtil
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.BarcodeSettingsBinding
import com.miteksystems.misnap.databinding.BarcodeWorkflowSettingsBinding
import com.miteksystems.misnap.databinding.CameraSettingsBinding
import com.miteksystems.misnap.databinding.DocumentSettingsBinding
import com.miteksystems.misnap.databinding.DocumentWorkflowSettingsBinding
import com.miteksystems.misnap.databinding.FaceSettingsBinding
import com.miteksystems.misnap.databinding.FaceWorkflowSettingsBinding
import com.miteksystems.misnap.databinding.FragmentSettingsRootBinding
import com.miteksystems.misnap.databinding.GeneralSettingsBinding
import com.miteksystems.misnap.databinding.NfcSettingsBinding
import com.miteksystems.misnap.databinding.NfcWorkflowSettingsBinding
import com.miteksystems.misnap.databinding.VoiceSettingsBinding
import com.miteksystems.misnap.databinding.VoiceWorkflowSettingsBinding
import com.miteksystems.misnap.document.*
import com.miteksystems.misnap.face.default
import com.miteksystems.misnap.face.getMaxAngle
import com.miteksystems.misnap.face.getMinEyesOpenConfidence
import com.miteksystems.misnap.face.getMinHorizontalFill
import com.miteksystems.misnap.face.getMinPadding
import com.miteksystems.misnap.face.getMinSmileConfidence
import com.miteksystems.misnap.face.getTriggerDelay
import com.miteksystems.misnap.face.requireTrigger
import com.miteksystems.misnap.nfc.default
import com.miteksystems.misnap.nfc.shouldRedactOptionalData
import com.miteksystems.misnap.sampleapp.results.SampleAppViewModel
import com.miteksystems.misnap.apputil.ViewPageAdapter
import com.miteksystems.misnap.camera.*
import com.miteksystems.misnap.controller.getImageQuality
import com.miteksystems.misnap.controller.getMotionDetectorSensitivity
import com.miteksystems.misnap.voice.*
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.fragment.*
import com.miteksystems.misnap.workflow.getForcedOrientation
import com.miteksystems.misnap.workflow.util.PermissionsUtil
import com.miteksystems.misnap.workflow.util.SharedPrefsUtil
import com.miteksystems.misnap.workflow.util.ViewBindingUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 *  NOTE : Assuming you've already have a shared resources module and a new app module depending on the former,
 *      these are the steps to split this fragment into individual SDKs, Face example:
 *      1. Copy the relevant resources, strings.xml and the settings layout face_settings.xml
 *      2. Adjust the lifecycle, state and helper common methods to exclude non face related code.
 *          - Depending on your case you might be able to ditch use case handling and tab management.
 *      3. Copy the related functions for face SDK
 *          - addFaceSettingsTab/addFaceWorkflowSettingsTab
 *          - applyFaceTabUiInputToSettings/applyFaceWorkflowTabUiInputToSettings
 *          - applySettingsToFaceTabUi/applySettingsToFaceWorkflowTabUi
 *          - Grab the helper functions for the face related tabs
 *      4. Streamline the methods as you see fit depending on what you were able to ditch, e.g. if you don't
 *          need tabs management, then do all the setup in onViewCreated
 */
class SettingsFragment : Fragment(R.layout.fragment_settings_root) {
    private val binding by ViewBindingUtil.viewBinding(
        this,
        FragmentSettingsRootBinding::bind
    )
    private val adapter = ViewPageAdapter()
    private var useCaseIndex = 0
    private var voiceFlowIndex = 0
    private var barcodeExtractionRequirementIndex = 0
    private val license by lazy { requireContext().getString(R.string.misnapSampleAppLicense) }
    private val sharedPrefs by lazy {
        requireContext().getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val sampleAppViewModel =
                ViewModelProvider(requireActivity())[SampleAppViewModel::class.java]
            sampleAppViewModel.results = MiSnapWorkflowActivity.Result.results
            MiSnapWorkflowActivity.Result.clearResults()

            findNavController().navigate(R.id.documentResultsFragment)
        }

    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    //Tabs config content layout bindings
    private var generalSettingsBinding: GeneralSettingsBinding? = null
    private var documentSettingsBinding: DocumentSettingsBinding? = null
    private var documentWorkflowSettingsBinding: DocumentWorkflowSettingsBinding? = null
    private var barcodeSettingsBinding: BarcodeSettingsBinding? = null
    private var barcodeWorkflowSettingsBinding: BarcodeWorkflowSettingsBinding? = null
    private var cameraSettingsBinding: CameraSettingsBinding? = null
    private var faceSettingsBinding: FaceSettingsBinding? = null
    private var faceWorkflowSettingsBinding: FaceWorkflowSettingsBinding? = null
    private var nfcSettingsBinding: NfcSettingsBinding? = null
    private var nfcWorkflowSettingsBinding: NfcWorkflowSettingsBinding? = null
    private var voiceSettingsBinding: VoiceSettingsBinding? = null
    private var voiceWorkflowSettingsBinding: VoiceWorkflowSettingsBinding? = null

    private val voicePhrases by lazy {
        listOf(
            *resources.getStringArray(R.array.misnapWorkflowVoicePhraseSelectionFragmentPhrases),
            "Custom"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPermissions()

        //Setup the viewpager + tabs
        binding.settingsViewPager.adapter = adapter
        binding.settingsTabLayout.setupWithViewPager(binding.settingsViewPager)

        //Clear preferences and reload the default settings for a given use case
        binding.settingsClearButton.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            SharedPrefsUtil.clearSharedPreferences(requireContext())

            applySettingsToUi(MiSnapSettings(getUseCaseAt(useCaseIndex), license))
        }

        //Apply the user's selections to a settings object and save them. Also start the workflow.
        binding.settingsSessionButton.setOnClickListener {
            val selectedUseCase = getUseCaseAt(useCaseIndex)
            val settings = MiSnapSettings(selectedUseCase, license).also {
                //General Settings Tab
                applyGeneralTabUiInputToSettings(it)
                //Video Settings Input
                applyCameraTabUiInputToSettings(it)

                when (selectedUseCase) {
                    MiSnapSettings.UseCase.BARCODE -> {
                        //BarcodeSettingsInput
                        applyBarcodeTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.FACE -> {
                        //FaceSettingsInput
                        applyFaceTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.NFC -> {
                        //NFCSettingsInput
                        applyNfcTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.VOICE -> {
                        //VoiceSettingsInput
                        applyVoiceTabUiInputToSettings(it)
                    }
                    else -> {
                        //Document Settings Tab
                        applyDocumentTabUiInputToSettings(it)
                        //BarcodeSettingsInput
                        applyBarcodeTabUiInputToSettings(it)
                    }
                }

                //Workflow Settings
                when (it.useCase) {
                    MiSnapSettings.UseCase.BARCODE -> {
                        applyBarcodeWorkflowTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.FACE -> {
                        applyFaceWorkflowTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.NFC -> {
                        applyNfcWorkflowTabUiInputToSettings(it)
                    }
                    MiSnapSettings.UseCase.VOICE -> {
                        applyVoiceWorkflowTabUiInputToSettings(it)
                    }
                    else -> {
                        applyDocumentWorkflowTabUiInputToSettings(it)
                    }
                }
            }

            if (settings.useCase == MiSnapSettings.UseCase.VOICE && getVoiceFlowAt(voiceFlowIndex) == MiSnapSettings.Voice.Flow.VERIFICATION &&
                (settings.voice.phrase.isNullOrEmpty() || settings.voice.phrase!!.isBlank())
            ) {
                voiceSettingsBinding?.let { showErrorForInvalidCustomPhrase(it) }

            } else {
                sharedPrefs.edit().apply {
                    if (settings.useCase == MiSnapSettings.UseCase.VOICE) {
                        putString(
                            getUseCaseSettingsPrefsKey(settings.useCase, settings.voice.flow),
                            Json.encodeToString(settings)
                        )
                        putInt(SHARED_PREFS_LAST_SELECTED_VOICE_FLOW_INDEX, voiceFlowIndex)
                    } else {
                        putString(
                            getUseCaseSettingsPrefsKey(settings.useCase),
                            Json.encodeToString(settings)
                        )
                    }

                    putInt(SHARED_PREFS_LAST_SELECTED_USECASE_INDEX, useCaseIndex)
                    apply()
                }

                startMiSnapSession(MiSnapWorkflowStep(settings))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //Clean any leftover and add the General Settings Tab
        adapter.clearViews()
        addGeneralSettingsTab()
    }

    private fun checkPermissions() {
        val missingPermissions =
            permissions.filter { !PermissionsUtil.hasPermission(requireContext(), it) }

        if (!missingPermissions.contains(Manifest.permission.RECORD_AUDIO)) {
            queryMicrophoneSupport()
        }

        requestPermissions(missingPermissions.toTypedArray())
    }

    /**
     * The SDK will automatically request the permissions it needs at runtime if needed,
     * but if you want to pre-query the camera or microphone capabilities you will need to
     * request the permissions first.
     */
    private fun requestPermissions(permissions: Array<String>) {
        val nonRationalePermissions = mutableListOf<String>()

        permissions.forEach { permission ->
            if (shouldShowRequestPermissionRationale(permission)) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(getPermissionRationaleTitle(permission))
                    setMessage(getPermissionRationaleMessage(permission))
                    setOnDismissListener {
                        requestPermissions(
                            arrayOf(permission),
                            getPermissionRequestCode(permission)
                        )
                    }
                    setPositiveButton(android.R.string.ok, null)
                    show()
                }
            } else {
                nonRationalePermissions.add(permission)
            }
        }

        if (nonRationalePermissions.isNotEmpty()) {
            requestPermissions(
                nonRationalePermissions.toTypedArray(),
                PERMISSION_REQUEST_ALL
            )
        }
    }

    private fun getPermissionRationaleTitle(permission: String) =
        when (permission) {
            Manifest.permission.CAMERA -> R.string.misnapAppUtilCameraPermissionRationaleTitle
            Manifest.permission.RECORD_AUDIO -> R.string.misnapAppUtilAudioRecordPermissionRationaleTitle
            else -> 0
        }

    private fun getPermissionRationaleMessage(permission: String) =
        when (permission) {
            Manifest.permission.CAMERA -> R.string.misnapAppUtilCameraPermissionRationaleMessage
            Manifest.permission.RECORD_AUDIO -> R.string.misnapAppUtilAudioRecordPermissionRationaleMessage
            else -> 0
        }

    private fun getPermissionRequestCode(permission: String) =
        when (permission) {
            Manifest.permission.CAMERA -> PERMISSION_REQUEST_CAMERA
            Manifest.permission.RECORD_AUDIO -> PERMISSION_REQUEST_RECORD_AUDIO
            else -> PERMISSION_REQUEST_ALL
        }

    /**
     * Callback for handling permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode and PERMISSION_REQUEST_CAMERA == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isEmpty() || PackageManager.PERMISSION_GRANTED != grantResults.first()) {
                // Permission denied, MiSnap SDK will not be usable for usecases that depend on this permission
            }
        }

        if (requestCode and PERMISSION_REQUEST_RECORD_AUDIO == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.isEmpty() || PackageManager.PERMISSION_GRANTED != grantResults.first()) {
                // Permission denied, MiSnap SDK will not be usable for usecases that depend on this permission
            }
        }
    }

    // region UI > MiSnapSettings helpers

    private fun applyGeneralTabUiInputToSettings(settings: MiSnapSettings) {
        settings.workflow.apply {
            generalSettingsBinding?.let {
                forceOrientation =
                    getDeviceOrientationAt(it.generalSettingsDeviceOrientationSpinner.selectedItemPosition)
            }
        }
    }

    private fun applyCameraTabUiInputToSettings(settings: MiSnapSettings) {
        settings.camera.apply {
            cameraSettingsBinding?.let {
                profile =
                    getCameraDirectionAt(it.cameraSettingsFacingDirectionSpinner.selectedItemPosition)
                torchMode = getTorchModeAt(it.cameraSettingsTorchModeSpinner.selectedItemPosition)

                videoRecord.recordSession = it.cameraSettingsRecordSessionCheckbox.isChecked

                videoRecord.videoResolution = Size(
                    kotlin.runCatching {
                        it.cameraSettingsVideoResolutionWidth.text.toString().toInt()
                    }.getOrDefault(videoRecord.getVideoResolution().width),
                    kotlin.runCatching {
                        it.cameraSettingsVideoResolutionHeight.text.toString().toInt()
                    }.getOrDefault(videoRecord.getVideoResolution().height)
                )

                videoRecord.videoBitrate =
                    kotlin.runCatching { it.cameraSettingsVideoBitrate.text.toString().toInt() }
                        .getOrDefault(videoRecord.getVideoBitrate())

                enableHighResolutionFrames =
                    it.cameraSettingsRequestHighResolutionCheckbox.isChecked
            }
        }
    }

    private fun applyDocumentTabUiInputToSettings(settings: MiSnapSettings) {
        settings.analysis.apply {
            documentSettingsBinding?.let {
                motionDetectorSensitivity =
                    getDeviceMotionSensitivityAt(it.analysisSettings.findViewById<Spinner>(R.id.analysisSettingsDeviceMotionSensitivitySpinner).selectedItemPosition)
                document.trigger =
                    getTriggerAt(it.documentSettingsTriggerSpinner.selectedItemPosition)
                document.enableEnhancedManual = it.documentSettingsEnableEnhancedManual.isChecked
                document.orientation =
                    getSessionOrientationAt(it.documentSettingsSessionOrientationSpinner.selectedItemPosition)
                document.documentExtractionRequirement =
                    getDocumentExtractionRequirementAt(it.documentSettingsDocumentRequestOcrSpinner.selectedItemPosition)
                document.barcodeExtractionRequirement =
                    getDocumentBarcodeExtractionRequirementAt(it.documentSettingsBarcodeRequestExtractionSpinner.selectedItemPosition)
                document.check.geo =
                    getGeoAt(it.documentSettingsGeoSpinner.selectedItemPosition)

                document.redactOptionalData = it.documentSettingsRedactOptionalData.isChecked

                document.enableDocumentClassification =
                    it.documentSettingsEnableClassification.isChecked

                document.advanced.cornerConfidence = kotlin.runCatching {
                    it.documentSettingsCornerConfidence.text.toString().toInt()
                }.getOrDefault(document.advanced.getCornerConfidence())

                document.advanced.minPadding = kotlin.runCatching {
                    it.documentSettingsMinPadding.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinPadding())

                document.advanced.minHorizontalFillUnaligned = kotlin.runCatching {
                    it.documentSettingsMinHorizontalFillUnAligned.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinHorizontalFillUnaligned())

                document.advanced.minBrightness = kotlin.runCatching {
                    it.documentSettingsMinBrightness.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinBrightness())

                document.advanced.maxBrightness = kotlin.runCatching {
                    it.documentSettingsMaxBrightness.text.toString().toInt()
                }.getOrDefault(document.advanced.getMaxBrightness())

                document.advanced.minContrast = kotlin.runCatching {
                    it.documentSettingsMinContrast.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinContrast())

                document.advanced.minHorizontalFillAligned = kotlin.runCatching {
                    it.documentSettingsMinHorizontalFillAligned.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinHorizontalFillAligned())

                document.advanced.minBusyBackground = kotlin.runCatching {
                    it.documentSettingsMinBusyBackground.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinBusyBackground())

                document.advanced.maxAngle = kotlin.runCatching {
                    it.documentSettingsMaxAngle.text.toString().toInt()
                }.getOrDefault(document.advanced.getMaxAngle())

                document.advanced.minSharpness = kotlin.runCatching {
                    it.documentSettingsMinSharpness.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinSharpness())

                document.advanced.minNoGlare = kotlin.runCatching {
                    it.documentSettingsMinNoGlare.text.toString().toInt()
                }.getOrDefault(document.advanced.getMinNoGlare())

                document.advanced.mrzConfidence = kotlin.runCatching {
                    it.documentSettingsMrzConfidence.text.toString().toInt()
                }.getOrDefault(document.advanced.getMrzConfidence())

                jpgQuality = kotlin.runCatching {
                    it.analysisSettings.findViewById<EditText>(R.id.analysisSettingsJpegQuality).text.toString().toInt()
                }.getOrDefault(getImageQuality(settings.useCase))
            }
        }
    }

    private fun applyDocumentWorkflowTabUiInputToSettings(settings: MiSnapSettings) {
        documentWorkflowSettingsBinding?.let {
            val defaultWorkflowSettings =
                DocumentAnalysisFragment.getDefaultWorkflowSettings(settings)
            val workflowSettings = DocumentAnalysisFragment.buildWorkflowSettings(
                reviewCondition = getDocumentReviewConditionAt(it.documentWorkflowSettingsReviewConditionSpinner.selectedItemPosition),
                misnapViewShouldShowBoundingBox = it.documentWorkflowSettingsShowBoundingBox.isChecked,
                misnapViewShouldShowGlareBox = it.documentWorkflowSettingsShowGlareBox.isChecked,
                timeoutDuration = kotlin.runCatching {
                    it.documentWorkflowSettingsTimeoutDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.timeoutDuration),
                hintDuration = kotlin.runCatching {
                    it.documentWorkflowSettingsHintDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.hintDuration),
                guideViewAlignedScalePercentage = kotlin.runCatching {
                    it.documentWorkflowSettingsGuideViewScalePercentageAligned.text.toString()
                        .toFloat()
                }.getOrDefault(defaultWorkflowSettings.guideViewAlignedScalePercentage),
                guideViewUnalignedScalePercentage = kotlin.runCatching {
                    it.documentWorkflowSettingsGuideViewScalePercentageUnaligned.text.toString()
                        .toFloat()
                }.getOrDefault(defaultWorkflowSettings.guideViewUnalignedScalePercentage),
                guideViewShowVignette = it.documentWorkflowSettingsShowVignetteBox.isChecked,
                hintViewShouldShowBackground = it.documentWorkflowSettingsHintViewShowBackground.isChecked,
                successViewShouldVibrate = it.documentWorkflowSettingsSuccessViewShouldVibrateBox.isChecked,
                manualButtonVisible = it.documentWorkflowSettingsShowManualButtonBox.isChecked,
                shouldShowDocumentLabel = it.documentWorkflowSettingsShowDocumentLabelBox.isChecked
            )

            settings.workflow.add(
                getString(DOCUMENT_ANALYSIS_FRAGMENT_LABEL_RES),
                workflowSettings
            )
        }
    }

    private fun applyBarcodeTabUiInputToSettings(settings: MiSnapSettings) {
        settings.analysis.barcode.apply {
            barcodeSettingsBinding?.let {
                trigger =
                    getBarcodeTriggerAt(it.barcodeSettingsTriggerSpinner.selectedItemPosition)
                type = getBarcodeTypeAt(it.barcodeSettingsTypeSpinner.selectedItemPosition)
                scanSpeed =
                    getBarcodeScanSpeedAt(it.barcodeSettingsScanSpeedSpinner.selectedItemPosition)
                orientation =
                    getBarcodeOrientationAt(it.barcodeSettingsOrientationSpinner.selectedItemPosition)
            }
        }
    }

    private fun applyBarcodeWorkflowTabUiInputToSettings(settings: MiSnapSettings) {
        barcodeWorkflowSettingsBinding?.let {
            val defaultWorkflowSettings =
                BarcodeAnalysisFragment.getDefaultWorkflowSettings(settings)
            val workflowSettings = BarcodeAnalysisFragment.buildWorkflowSettings(
                reviewCondition = getBarcodeReviewConditionAt(it.barcodeWorkflowSettingsReviewConditionSpinner.selectedItemPosition),
                guideViewAlignedScalePercentage = kotlin.runCatching {
                    it.barcodeWorkflowSettingsGuideViewAlignedScalePercentage.text.toString()
                        .toFloat()
                }.getOrDefault(defaultWorkflowSettings.guideViewAlignedScalePercentage),
                guideViewUnalignedScalePercentage = kotlin.runCatching {
                    it.barcodeWorkflowSettingsGuideViewUnalignedScalePercentage.text.toString()
                        .toFloat()
                }.getOrDefault(defaultWorkflowSettings.guideViewUnalignedScalePercentage),
                guideViewShowVignette = it.barcodeWorkflowSettingsShowVignetteBox.isChecked,
                successViewShouldVibrate = it.barcodeWorkflowSettingsSuccessViewShouldVibrateBox.isChecked
            )

            settings.workflow.add(
                getString(BARCODE_ANALYSIS_FRAGMENT_LABEL_RES),
                workflowSettings
            )
        }
    }

    private fun applyFaceTabUiInputToSettings(settings: MiSnapSettings) {
        settings.analysis.apply {
            faceSettingsBinding?.let {
                motionDetectorSensitivity =
                    getDeviceMotionSensitivityAt(it.analysisSettings.findViewById<Spinner>(R.id.analysisSettingsDeviceMotionSensitivitySpinner).selectedItemPosition)

                face.trigger =
                    getFaceTriggerAt(it.faceSettingsTriggerSpinner.selectedItemPosition)

                face.advanced.triggerDelay = kotlin.runCatching {
                    it.faceSettingsTriggerDelay.text.toString().toInt()
                }.getOrDefault(face.advanced.getTriggerDelay(face.requireTrigger()))

                face.advanced.minEyesOpenConfidence = kotlin.runCatching {
                    it.faceSettingsEyesOpenConfidenceThreshold.text.toString().toInt()
                }.getOrDefault(face.advanced.getMinEyesOpenConfidence())

                face.advanced.minSmileConfidence = kotlin.runCatching {
                    it.faceSettingsSmileConfidenceThreshold.text.toString().toInt()
                }.getOrDefault(face.advanced.getMinSmileConfidence())

                face.advanced.maxAngle = kotlin.runCatching {
                    it.faceSettingsMaxAngleThreshold.text.toString().toInt()
                }.getOrDefault(face.advanced.getMaxAngle())

                face.advanced.minHorizontalFill = kotlin.runCatching {
                    it.faceSettingsMinFillThreshold.text.toString().toInt()
                }.getOrDefault(face.advanced.getMinHorizontalFill())

                face.advanced.minPadding = kotlin.runCatching {
                    it.faceSettingsMinPaddingThreshold.text.toString().toInt()
                }.getOrDefault(face.advanced.getMinPadding())

                jpgQuality = kotlin.runCatching {
                    it.analysisSettings.findViewById<EditText>(R.id.analysisSettingsJpegQuality).text.toString().toInt()
                }.getOrDefault(getImageQuality(settings.useCase))
            }
        }
    }

    private fun applyFaceWorkflowTabUiInputToSettings(settings: MiSnapSettings) {
        faceWorkflowSettingsBinding?.let {
            val defaultWorkflowSettings = FaceAnalysisFragment.getDefaultWorkflowSettings(settings)
            val workflowSettings = FaceAnalysisFragment.buildWorkflowSettings(
                reviewCondition = getFaceReviewConditionAt(it.faceWorkflowSettingsReviewConditionSpinner.selectedItemPosition),
                misnapViewShouldShowBoundingBox = it.faceWorkflowSettingsShowBoundingBox.isChecked,
                showCountdownTimer = it.faceWorkflowSettingsShowCountdownTimer.isChecked,
                countdownTimerDuration = kotlin.runCatching {
                    it.faceWorkflowSettingsCountdownTimerDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.countdownTimerDuration),
                timeoutDuration = kotlin.runCatching {
                    it.faceWorkflowSettingsTimeoutDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.timeoutDuration),
                hintDuration = kotlin.runCatching {
                    it.faceWorkflowSettingsHintDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.hintDuration),
                guideViewScalePercentage = kotlin.runCatching {
                    it.faceWorkflowSettingsGuideViewScalePercentage.text.toString().toFloat()
                }.getOrDefault(defaultWorkflowSettings.guideViewScalePercentage),
                guideViewShowVignette = it.faceWorkflowSettingsShowVignetteBox.isChecked,
                hintViewShouldShowBackground = it.faceWorkflowSettingsHintViewShowBackground.isChecked,
                successViewShouldVibrate = it.faceWorkflowSettingsSuccessViewShouldVibrateBox.isChecked,
                changeGuideViewStateOnFeedback = it.faceWorkflowSettingsChangeGuideViewStateOnFeedbackBox.isChecked,
                lowLightSensitivity = getLowLightSensitivityAt(it.faceWorkflowSettingsLowLightSensitivitySpinner.selectedItemPosition),
            )

            settings.workflow.add(
                getString(FACE_ANALYSIS_FRAGMENT_LABEL_RES),
                workflowSettings
            )
        }
    }

    private fun applyNfcTabUiInputToSettings(settings: MiSnapSettings) {
        settings.nfc.apply {
            nfcSettingsBinding?.let {
                soundAndVibration =
                    getNfcSoundAndVibrationAt(it.nfcSettingsSoundAndVibrationSpinner.selectedItemPosition)
                advanced.docType =
                    getNfcDoctypeAt(it.nfcSettingsDoctypeSpinner.selectedItemPosition)
                redactOptionalData = it.nfcSettingsRedactOptionalData.isChecked
            }
        }
    }

    private fun applyNfcWorkflowTabUiInputToSettings(settings: MiSnapSettings) {
        nfcWorkflowSettingsBinding?.let {
            val defaultWorkflowSettings =
                NfcReaderFragment.getDefaultWorkflowSettings(settings, requireContext())
            val workflowSettings = NfcReaderFragment.buildWorkflowSettings(
                shouldShowFailoverPopup = it.nfcWorkflowSettingsShowFailoverPopUp.isChecked,
                skipVisibilityTimeout = kotlin.runCatching {
                    it.nfcWorkflowSettingsSkipVisibilityTimeoutDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.skipVisibilityTimeout)
            )

            settings.workflow.add(
                getString(NFC_READER_FRAGMENT_LABEL_RES),
                workflowSettings
            )
        }
    }

    private fun applyVoiceTabUiInputToSettings(settings: MiSnapSettings) {
        settings.voice.apply {
            voiceSettingsBinding?.let {
                flow = getVoiceFlowAt(it.voiceSettingsFlowSpinner.selectedItemPosition)
                phrase = getVoicePhraseAt(it, it.voiceSettingsPhraseSpinner.selectedItemPosition)
                advanced.minSpeechLength = it.voiceSettingsMinSpeechLength.text.toString().toInt()
                advanced.minSNR = kotlin.runCatching {
                    it.voiceSettingsMinSNR.text.toString().toFloat()
                }
                    .getOrDefault(advanced.getMinSNR(flow!!))

                advanced.maxSilenceLength = it.voiceSettingsMaxSilenceLength.text.toString().toInt()
            }
        }
    }

    private fun applyVoiceWorkflowTabUiInputToSettings(settings: MiSnapSettings) {
        voiceWorkflowSettingsBinding?.let {
            val defaultWorkflowSettings = VoiceProcessorFragment.getDefaultWorkflowSettings()
            val workflowSettings = VoiceProcessorFragment.buildWorkflowSettings(
                timeoutDuration = kotlin.runCatching {
                    it.voiceWorkflowSettingsSessionTimeoutDuration.text.toString().toInt()
                }.getOrDefault(defaultWorkflowSettings.timeoutDuration)
            )

            settings.workflow.add(
                getString(VOICE_ANALYSIS_FRAGMENT_LABEL_RES),
                workflowSettings
            )
        }
    }

    // endregion

    // region MiSnapSettings > UI helpers

    private fun applySettingsToUi(settings: MiSnapSettings) {
        when (settings.useCase) {
            MiSnapSettings.UseCase.BARCODE -> {
                //Remove document settings
                findTabIndexByTabTitle(getString(DOCUMENT_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove face settings
                findTabIndexByTabTitle(getString(FACE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove NFC settings
                findTabIndexByTabTitle(getString(NFC_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Voice settings
                findTabIndexByTabTitle(getString(VOICE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the camera settings tab if not already there
                if (!isTabWithTitlePresent(getString(CAMERA_TAB_TITLE_RES))) {
                    addCameraSettingsTab()
                }

                //Add the barcode settings tab if not already there
                if (!isTabWithTitlePresent(getString(BARCODE_TAB_TITLE_RES))) {
                    addBarcodeSettingsTab()
                }

                //Add the barcode workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addBarcodeWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToCameraTabUi(settings)
                applySettingsToBarcodeTabUi(settings)
                applySettingsToBarcodeWorkflowTabUi(settings)
            }
            MiSnapSettings.UseCase.FACE -> {
                //Remove document settings
                findTabIndexByTabTitle(getString(DOCUMENT_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove barcode settings
                findTabIndexByTabTitle(getString(BARCODE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove NFC settings
                findTabIndexByTabTitle(getString(NFC_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Voice settings
                findTabIndexByTabTitle(getString(VOICE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the camera settings tab if not already there
                if (!isTabWithTitlePresent(getString(CAMERA_TAB_TITLE_RES))) {
                    addCameraSettingsTab()
                }

                //Add the face settings if not already there
                if (!isTabWithTitlePresent(getString(FACE_TAB_TITLE_RES))) {
                    addFaceSettingsTab()
                }

                //Add the face workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addFaceWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToCameraTabUi(settings)
                applySettingsToFaceTabUi(settings)
                applySettingsToFaceWorkflowTabUi(settings)
            }
            MiSnapSettings.UseCase.NFC -> {
                //Remove camera settings
                findTabIndexByTabTitle(getString(CAMERA_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove document settings
                findTabIndexByTabTitle(getString(DOCUMENT_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove barcode settings
                findTabIndexByTabTitle(getString(BARCODE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove face settings
                findTabIndexByTabTitle(getString(FACE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Voice settings
                findTabIndexByTabTitle(getString(VOICE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the nfc settings if not already there
                if (!isTabWithTitlePresent(getString(NFC_TAB_TITLE_RES))) {
                    addNfcSettingsTab()
                }

                //Add the nfc workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addNfcWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToNfcTabUi(settings)
                applySettingsToNfcWorkflowTabUi(settings)
            }

            MiSnapSettings.UseCase.VOICE -> {
                //Remove camera settings
                findTabIndexByTabTitle(getString(CAMERA_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove document settings
                findTabIndexByTabTitle(getString(DOCUMENT_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove barcode settings
                findTabIndexByTabTitle(getString(BARCODE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove face settings
                findTabIndexByTabTitle(getString(FACE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove NFC settings
                findTabIndexByTabTitle(getString(NFC_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the voice settings if not already there
                if (!isTabWithTitlePresent(getString(VOICE_TAB_TITLE_RES))) {
                    addVoiceSettingsTab()
                }

                //Add the voice workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addVoiceWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToVoiceTabUi(settings)
                applySettingsToVoiceWorkflowTabUi(settings)
            }

            MiSnapSettings.UseCase.PASSPORT,
            MiSnapSettings.UseCase.CHECK_FRONT,
            MiSnapSettings.UseCase.CHECK_BACK,
            MiSnapSettings.UseCase.GENERIC_DOCUMENT
            -> {
                //Remove barcode settings
                findTabIndexByTabTitle(getString(BARCODE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove face settings
                findTabIndexByTabTitle(getString(FACE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove NFC settings
                findTabIndexByTabTitle(getString(NFC_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Voice settings
                findTabIndexByTabTitle(getString(VOICE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the camera settings tab if not already there
                if (!isTabWithTitlePresent(getString(CAMERA_TAB_TITLE_RES))) {
                    addCameraSettingsTab()
                }

                //Add the document settings tab if not already there
                if (!isTabWithTitlePresent(getString(DOCUMENT_TAB_TITLE_RES))) {
                    addDocumentSettingsTab()
                }

                //Add the document workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addDocumentWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToCameraTabUi(settings)
                applySettingsToDocumentTabUi(settings)
                applySettingsToDocumentWorkflowTabUi(settings)
            }

            else -> {
                //Remove Face Settings
                findTabIndexByTabTitle(getString(FACE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove NFC settings
                findTabIndexByTabTitle(getString(NFC_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Workflow settings
                findTabIndexByTabTitle(getString(WORKFLOW_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Remove Voice settings
                findTabIndexByTabTitle(getString(VOICE_TAB_TITLE_RES))?.let {
                    adapter.removeViewAt(it)
                }

                //Add the camera settings tab if not already there
                if (!isTabWithTitlePresent(getString(CAMERA_TAB_TITLE_RES))) {
                    addCameraSettingsTab()
                }

                //Add the document settings tab if not already there
                if (!isTabWithTitlePresent(getString(DOCUMENT_TAB_TITLE_RES))) {
                    addDocumentSettingsTab()
                }

                //Add the barcode settings tab if not already there
                if (!isTabWithTitlePresent(getString(BARCODE_TAB_TITLE_RES))) {
                    addBarcodeSettingsTab()
                }

                //Add the document workflow settings tab if not already there
                if (!isTabWithTitlePresent(getString(WORKFLOW_TAB_TITLE_RES))) {
                    addDocumentWorkflowSettingsTab()
                }

                applySettingsToGeneralTabUi(settings)
                applySettingsToCameraTabUi(settings)
                applySettingsToDocumentTabUi(settings)
                applySettingsToBarcodeTabUi(settings)
                applySettingsToDocumentWorkflowTabUi(settings)
            }
        }
    }

    private fun applySettingsToGeneralTabUi(settings: MiSnapSettings) {
        generalSettingsBinding?.let {
            settings.workflow.forceOrientation?.let { index ->
                it.generalSettingsDeviceOrientationSpinner.setSelection(
                    getDeviceOrientationSpinnerIndex(index)
                )
            }
                ?: it.generalSettingsDeviceOrientationSpinner.setSelection(
                    getDeviceOrientationSpinnerIndex(
                        settings.workflow.getForcedOrientation(settings.useCase)
                            ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    )
                )
        }
    }

    private fun applySettingsToCameraTabUi(settings: MiSnapSettings) {
        cameraSettingsBinding?.let {
            it.cameraSettingsTorchModeSpinner.setSelection(settings.camera.getTorchMode().ordinal)
            it.cameraSettingsFacingDirectionSpinner.setSelection(settings.camera.requireProfile().ordinal)

            val shouldRecordSession = settings.camera.videoRecord.shouldRecordSession()
            it.cameraSettingsRecordSessionCheckbox.isChecked = shouldRecordSession

            it.cameraSettingsVideoResolutionWidth.isEnabled = shouldRecordSession
            it.cameraSettingsVideoResolutionHeight.isEnabled = shouldRecordSession
            it.cameraSettingsVideoBitrate.isEnabled = shouldRecordSession


            it.cameraSettingsVideoResolutionWidth.setText(settings.camera.videoRecord.getVideoResolution().width.toString())
            it.cameraSettingsVideoResolutionHeight.setText(settings.camera.videoRecord.getVideoResolution().height.toString())
            it.cameraSettingsVideoBitrate.setText(
                settings.camera.videoRecord.getVideoBitrate().toString()
            )

            it.cameraSettingsRequestHighResolutionCheckbox.isChecked =
                settings.camera.shouldEnableHighResolutionFrames()
        }
    }

    private fun applySettingsToDocumentTabUi(settings: MiSnapSettings) {
        val isGeneric = settings.useCase == MiSnapSettings.UseCase.GENERIC_DOCUMENT
        documentSettingsBinding?.let {
            it.analysisSettings.findViewById<Spinner>(R.id.analysisSettingsDeviceMotionSensitivitySpinner).setSelection(
                settings.analysis.getMotionDetectorSensitivity(settings.useCase).ordinal
            )

            if (settings.analysis.document.trigger == MiSnapSettings.Analysis.Document.Trigger.MANUAL) {
                it.documentSettingsTriggerSpinner.setSelection(MiSnapSettings.Analysis.Document.Trigger.MANUAL.ordinal)
            } else {
                it.documentSettingsTriggerSpinner.setSelection(MiSnapSettings.Analysis.Document.Trigger.AUTO.ordinal)
            }

            it.documentSettingsEnableEnhancedManual.isChecked =
                settings.analysis.document.shouldEnableEnhancedManual()

            settings.analysis.document.orientation?.let { orientation ->
                it.documentSettingsSessionOrientationSpinner.setSelection(orientation.ordinal)
            }
                ?: it.documentSettingsSessionOrientationSpinner.setSelection(
                    MiSnapSettings.Analysis.Document.Orientation.default().ordinal
                )

            it.documentSettingsDocumentRequestOcrSpinner.apply {
                isEnabled =
                    !settings.analysis.document.advanced.requireDocType().isCheck() && !isGeneric
                setSelection(settings.analysis.document.getDocumentExtractionRequirement().ordinal)
            }
            it.documentSettingsBarcodeRequestExtractionSpinner.apply {
                isEnabled = !settings.analysis.document.advanced.requireDocType().isMrzDocument() &&
                    !settings.analysis.document.advanced.requireDocType()
                        .isCheck() && !isGeneric
                setSelection(settings.analysis.document.getBarcodeExtractionRequirement().ordinal)
            }

            it.documentSettingsGeoSpinner.isEnabled =
                settings.analysis.document.advanced.requireDocType().isCheck()
            it.documentSettingsGeoSpinner.setSelection(settings.analysis.document.check.getGeo().ordinal)

            it.documentSettingsRedactOptionalData.isChecked =
                settings.analysis.document.shouldRedactOptionalData()

            it.documentSettingsEnableClassification.isChecked =
                settings.analysis.document.shouldEnableDocumentClassification()

            it.documentSettingsCornerConfidence.setText(
                settings.analysis.document.advanced.getCornerConfidence()
                    .toString()
            )
            it.documentSettingsMinPadding.setText(
                settings.analysis.document.advanced.getMinPadding()
                    .toString()
            )
            it.documentSettingsMinHorizontalFillAligned.setText(
                settings.analysis.document.advanced.getMinHorizontalFillAligned()
                    .toString()
            )
            it.documentSettingsMinHorizontalFillUnAligned.setText(
                settings.analysis.document.advanced.getMinHorizontalFillUnaligned()
                    .toString()
            )
            it.documentSettingsMinBrightness.setText(
                settings.analysis.document.advanced.getMinBrightness()
                    .toString()
            )
            it.documentSettingsMaxBrightness.setText(
                settings.analysis.document.advanced.getMaxBrightness()
                    .toString()
            )
            it.documentSettingsMinContrast.setText(
                settings.analysis.document.advanced.getMinContrast()
                    .toString()
            )
            it.documentSettingsMinBusyBackground.setText(
                settings.analysis.document.advanced.getMinBusyBackground()
                    .toString()
            )
            it.documentSettingsMaxAngle.setText(
                settings.analysis.document.advanced.getMaxAngle()
                    .toString()
            )
            it.documentSettingsMinSharpness.setText(
                settings.analysis.document.advanced.getMinSharpness()
                    .toString()
            )
            it.documentSettingsMinNoGlare.setText(
                settings.analysis.document.advanced.getMinNoGlare()
                    .toString()
            )
            it.documentSettingsMrzConfidence.setText(
                settings.analysis.document.advanced.getMrzConfidence()
                    .toString()
            )

            it.analysisSettings.findViewById<EditText>(R.id.analysisSettingsJpegQuality).setText(
                settings.analysis.getImageQuality(settings.useCase)
                    .toString()
            )
        }
    }

    private fun applySettingsToDocumentWorkflowTabUi(settings: MiSnapSettings) {
        documentWorkflowSettingsBinding?.let { binding ->
            val workflowSettings =
                settings.workflow.get(getString(DOCUMENT_ANALYSIS_FRAGMENT_LABEL_RES))
                    ?.let { workflowSettingsString ->
                        Json.decodeFromString<DocumentAnalysisFragment.WorkflowSettings>(
                            workflowSettingsString
                        )
                    }

            //Trigger is needed to determine the correct manual button visibility
            documentSettingsBinding?.let { documentSettingsBinding ->
                settings.apply {
                    analysis.document.trigger = getTriggerAt(
                        documentSettingsBinding.documentSettingsTriggerSpinner.selectedItemPosition)
                }
            }

            val defaultWorkflowSettings =
                DocumentAnalysisFragment.getDefaultWorkflowSettings(settings)


            (workflowSettings?.reviewCondition
                ?: defaultWorkflowSettings.reviewCondition)?.let {
                binding.documentWorkflowSettingsReviewConditionSpinner.setSelection(
                    getDocumentReviewConditionIndex(it)
                )
            }

            (workflowSettings?.misnapViewShouldShowBoundingBox
                ?: defaultWorkflowSettings.misnapViewShouldShowBoundingBox)?.let {
                binding.documentWorkflowSettingsShowBoundingBox.isChecked = it
            }

            (workflowSettings?.misnapViewShouldShowGlareBox
                ?: defaultWorkflowSettings.misnapViewShouldShowGlareBox)?.let {
                binding.documentWorkflowSettingsShowGlareBox.isChecked = it
            }

            (workflowSettings?.timeoutDuration
                ?: defaultWorkflowSettings.timeoutDuration)?.let {
                binding.documentWorkflowSettingsTimeoutDuration.setText(it.toString())
            }

            (workflowSettings?.hintDuration ?: defaultWorkflowSettings.hintDuration)?.let {
                binding.documentWorkflowSettingsHintDuration.setText(it.toString())
            }

            (workflowSettings?.guideViewAlignedScalePercentage
                ?: defaultWorkflowSettings.guideViewAlignedScalePercentage)?.let {
                binding.documentWorkflowSettingsGuideViewScalePercentageAligned.setText(
                    it.toString()
                )
            }

            (workflowSettings?.guideViewUnalignedScalePercentage
                ?: defaultWorkflowSettings.guideViewUnalignedScalePercentage)?.let {
                binding.documentWorkflowSettingsGuideViewScalePercentageUnaligned.setText(
                    it.toString()
                )
            }

            (workflowSettings?.guideViewShowVignette
                ?: defaultWorkflowSettings.guideViewShowVignette)?.let {
                binding.documentWorkflowSettingsShowVignetteBox.isChecked = it
            }

            (workflowSettings?.hintViewShouldShowBackground
                ?: defaultWorkflowSettings.hintViewShouldShowBackground)?.let {
                binding.documentWorkflowSettingsHintViewShowBackground.isChecked = it
            }

            (workflowSettings?.successViewShouldVibrate
                ?: defaultWorkflowSettings.successViewShouldVibrate)?.let {
                binding.documentWorkflowSettingsSuccessViewShouldVibrateBox.isChecked = it
            }

            (workflowSettings?.manualButtonVisible
                ?: defaultWorkflowSettings.manualButtonVisible)?.let {
                binding.documentWorkflowSettingsShowManualButtonBox.isChecked = it
            }

            (workflowSettings?.shouldShowDocumentLabel
                ?: defaultWorkflowSettings.shouldShowDocumentLabel)?.let {
                binding.documentWorkflowSettingsShowDocumentLabelBox.isChecked = it
            }
        }
    }

    private fun applySettingsToBarcodeTabUi(settings: MiSnapSettings) {
        barcodeSettingsBinding?.let {
            val isBarcodeUseCase = getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.BARCODE
            it.barcodeSettingsTriggerSpinner.isEnabled = isBarcodeUseCase
            it.barcodeSettingsTypeSpinner.isEnabled = isBarcodeUseCase
            it.barcodeSettingsScanSpeedSpinner.isEnabled = isBarcodeUseCase
            it.barcodeSettingsOrientationSpinner.isEnabled = isBarcodeUseCase

            if (settings.analysis.barcode.trigger == MiSnapSettings.Analysis.Barcode.Trigger.MANUAL) {
                it.barcodeSettingsTriggerSpinner.setSelection(MiSnapSettings.Analysis.Barcode.Trigger.MANUAL.ordinal)
            } else {
                it.barcodeSettingsTriggerSpinner.setSelection(MiSnapSettings.Analysis.Barcode.Trigger.AUTO.ordinal)
            }

            settings.analysis.barcode.type?.let { barcodeType ->
                it.barcodeSettingsTypeSpinner.setSelection(getBarcodeSpinnerIndex(barcodeType))
            } ?: it.barcodeSettingsTypeSpinner.setSelection(
                getBarcodeSpinnerIndex(MiSnapSettings.Analysis.Barcode.Type.default())
            )

            it.barcodeSettingsScanSpeedSpinner.setSelection(settings.analysis.barcode.getScanSpeed().ordinal)

            settings.analysis.barcode.orientation?.let { barcodeOrientation ->
                it.barcodeSettingsOrientationSpinner.setSelection(barcodeOrientation.ordinal)
            }
                ?: it.barcodeSettingsOrientationSpinner.setSelection(MiSnapSettings.Analysis.Barcode.Orientation.default().ordinal)
        }
    }

    private fun applySettingsToBarcodeWorkflowTabUi(settings: MiSnapSettings) {
        barcodeWorkflowSettingsBinding?.let { binding ->
            val workflowSettings =
                settings.workflow.get(getString(BARCODE_ANALYSIS_FRAGMENT_LABEL_RES))
                    ?.let { workflowSettingsString ->
                        Json.decodeFromString<BarcodeAnalysisFragment.WorkflowSettings>(
                            workflowSettingsString
                        )
                    }
            val defaultWorkflowSettings =
                BarcodeAnalysisFragment.getDefaultWorkflowSettings(settings)

            (workflowSettings?.reviewCondition
                ?: defaultWorkflowSettings.reviewCondition)?.let {
                binding.barcodeWorkflowSettingsReviewConditionSpinner.setSelection(
                    getBarcodeReviewConditionIndex(it)
                )
            }

            (workflowSettings?.guideViewAlignedScalePercentage
                ?: defaultWorkflowSettings.guideViewAlignedScalePercentage)?.let {
                binding.barcodeWorkflowSettingsGuideViewAlignedScalePercentage.setText(it.toString())
            }

            (workflowSettings?.guideViewUnalignedScalePercentage
                ?: defaultWorkflowSettings.guideViewUnalignedScalePercentage)?.let {
                binding.barcodeWorkflowSettingsGuideViewUnalignedScalePercentage.setText(it.toString())
            }

            (workflowSettings?.guideViewShowVignette
                ?: defaultWorkflowSettings.guideViewShowVignette)?.let {
                binding.barcodeWorkflowSettingsShowVignetteBox.isChecked = it
            }

            (workflowSettings?.successViewShouldVibrate
                ?: defaultWorkflowSettings.successViewShouldVibrate)?.let {
                binding.barcodeWorkflowSettingsSuccessViewShouldVibrateBox.isChecked = it
            }
        }
    }

    private fun applySettingsToFaceTabUi(settings: MiSnapSettings) {
        faceSettingsBinding?.let {
            it.analysisSettings.findViewById<Spinner>(R.id.analysisSettingsDeviceMotionSensitivitySpinner).setSelection(
                settings.analysis.getMotionDetectorSensitivity(settings.useCase).ordinal
            )
            //Set the ttigger from the settings, or the default value, the "require" method is not a good fit here.
            settings.analysis.face.trigger?.let { faceTrigger ->
                it.faceSettingsTriggerSpinner.setSelection(
                    getFaceTriggerSpinnerIndex(
                        faceTrigger
                    )
                )
            } ?: it.faceSettingsTriggerSpinner.setSelection(
                MiSnapSettings.Analysis.Face.Trigger.default().ordinal
            )

            it.faceSettingsTriggerDelay.setText(
                settings.analysis.face.advanced.getTriggerDelay(
                    settings.analysis.face.trigger ?: MiSnapSettings.Analysis.Face.Trigger.default()
                ).toString()
            )

            it.faceSettingsEyesOpenConfidenceThreshold.setText(
                settings.analysis.face.advanced.getMinEyesOpenConfidence().toString()
            )

            it.faceSettingsMaxAngleThreshold.setText(
                settings.analysis.face.advanced.getMaxAngle().toString()
            )

            it.faceSettingsMinFillThreshold.setText(
                settings.analysis.face.advanced.getMinHorizontalFill().toString()
            )

            it.faceSettingsMinPaddingThreshold.setText(
                settings.analysis.face.advanced.getMinPadding().toString()
            )

            it.faceSettingsSmileConfidenceThreshold.setText(
                settings.analysis.face.advanced.getMinSmileConfidence().toString()
            )

            it.analysisSettings.findViewById<EditText>(R.id.analysisSettingsJpegQuality).setText(
                settings.analysis.getImageQuality(settings.useCase)
                    .toString()
            )
        }
    }

    private fun applySettingsToFaceWorkflowTabUi(settings: MiSnapSettings) {
        faceWorkflowSettingsBinding?.let { binding ->
            val workflowSettings =
                settings.workflow.get(getString(FACE_ANALYSIS_FRAGMENT_LABEL_RES))
                    ?.let { workflowSettingsString ->
                        Json.decodeFromString<FaceAnalysisFragment.WorkflowSettings>(
                            workflowSettingsString
                        )
                    }
            if (settings.analysis.face.trigger == null) {
                settings.analysis.face.trigger = MiSnapSettings.Analysis.Face.Trigger.default()
            }
            val defaultWorkflowSettings =
                FaceAnalysisFragment.getDefaultWorkflowSettings(settings)

            (workflowSettings?.reviewCondition
                ?: defaultWorkflowSettings.reviewCondition)?.let {
                binding.faceWorkflowSettingsReviewConditionSpinner.setSelection(
                    getFaceReviewConditionIndex(it)
                )
            }

            (workflowSettings?.misnapViewShouldShowBoundingBox
                ?: defaultWorkflowSettings.misnapViewShouldShowBoundingBox)?.let {
                binding.faceWorkflowSettingsShowBoundingBox.isChecked = it
            }

            (workflowSettings?.showCountdownTimer
                ?: defaultWorkflowSettings.showCountdownTimer)?.let {
                binding.faceWorkflowSettingsShowCountdownTimer.isChecked = it
            }

            (workflowSettings?.countdownTimerDuration
                ?: defaultWorkflowSettings.countdownTimerDuration)?.let {
                binding.faceWorkflowSettingsCountdownTimerDuration.setText(it.toString())
            }

            (workflowSettings?.timeoutDuration
                ?: defaultWorkflowSettings.timeoutDuration)?.let {
                binding.faceWorkflowSettingsTimeoutDuration.setText(it.toString())
            }

            (workflowSettings?.hintDuration
                ?: defaultWorkflowSettings.hintDuration)?.let {
                binding.faceWorkflowSettingsHintDuration.setText(it.toString())
            }

            (workflowSettings?.guideViewScalePercentage
                ?: defaultWorkflowSettings.guideViewScalePercentage)?.let {
                binding.faceWorkflowSettingsGuideViewScalePercentage.setText(it.toString())
            }

            (workflowSettings?.guideViewShowVignette
                ?: defaultWorkflowSettings.guideViewShowVignette)?.let {
                binding.faceWorkflowSettingsShowVignetteBox.isChecked = it
            }

            (workflowSettings?.hintViewShouldShowBackground
                ?: defaultWorkflowSettings.hintViewShouldShowBackground)?.let {
                binding.faceWorkflowSettingsHintViewShowBackground.isChecked = it
            }

            (workflowSettings?.successViewShouldVibrate
                ?: defaultWorkflowSettings.successViewShouldVibrate)?.let {
                binding.faceWorkflowSettingsSuccessViewShouldVibrateBox.isChecked = it
            }

            (workflowSettings?.lowLightSensitivity
                ?: defaultWorkflowSettings.lowLightSensitivity)?.let {
                binding.faceWorkflowSettingsLowLightSensitivitySpinner.setSelection(
                    getLowLightSensitivityIndex(it)
                )
            }

            (workflowSettings?.changeGuideViewStateOnFeedback
                ?: defaultWorkflowSettings.changeGuideViewStateOnFeedback)?.let {
                binding.faceWorkflowSettingsChangeGuideViewStateOnFeedbackBox.isChecked = it
            }
        }
    }

    private fun applySettingsToNfcTabUi(settings: MiSnapSettings) {
        nfcSettingsBinding?.let {
            settings.nfc.advanced.docType?.let { doctype ->
                it.nfcSettingsDoctypeSpinner.setSelection(
                    getNfcDoctypeSpinnerIndex(doctype)
                )
            }
                ?: it.nfcSettingsDoctypeSpinner.setSelection(MiSnapSettings.Nfc.Advanced.DocType.PASSPORT.ordinal)

            settings.nfc.soundAndVibration?.let { soundAndVibration ->
                it.nfcSettingsSoundAndVibrationSpinner.setSelection(
                    getNfcSoundAndVibrationSpinnerIndex(soundAndVibration)
                )
            }
                ?: it.nfcSettingsSoundAndVibrationSpinner.setSelection(MiSnapSettings.Nfc.SoundAndVibration.default().ordinal)

            it.nfcSettingsRedactOptionalData.isChecked = settings.nfc.shouldRedactOptionalData()
        }
    }

    private fun applySettingsToNfcWorkflowTabUi(settings: MiSnapSettings) {
        nfcWorkflowSettingsBinding?.let { binding ->
            val workflowSettings = settings.workflow.get(getString(NFC_READER_FRAGMENT_LABEL_RES))
                ?.let { workflowSettingsString ->
                    Json.decodeFromString<NfcReaderFragment.WorkflowSettings>(workflowSettingsString)
                }
            val defaultWorkflowSettings =
                NfcReaderFragment.getDefaultWorkflowSettings(settings, requireContext())

            (workflowSettings?.shouldShowFailoverPopup
                ?: defaultWorkflowSettings.shouldShowFailoverPopup)?.let {
                binding.nfcWorkflowSettingsShowFailoverPopUp.isChecked = it
            }

            (workflowSettings?.skipVisibilityTimeout
                ?: defaultWorkflowSettings.skipVisibilityTimeout)?.let {
                binding.nfcWorkflowSettingsSkipVisibilityTimeoutDuration.setText(it.toString())
            }
        }
    }

    private fun applySettingsToVoiceTabUi(settings: MiSnapSettings) {
        voiceSettingsBinding?.let { binding ->
            settings.voice.flow?.let { flow ->
                binding.voiceSettingsFlowSpinner.setSelection(
                    getVoiceFlowSpinnerIndex(flow)
                )
            }

            val phrase = settings.voice.phrase

            if (phrase != null) {
                val phraseIndex = getVoicePhraseSpinnerIndex(phrase)

                val selectedIndex =
                    if (phraseIndex == -1) {
                        binding.voiceSettingsCustomPhrase.setText(phrase)
                        voicePhrases.lastIndex
                    } else {
                        phraseIndex
                    }
                binding.voiceSettingsPhraseSpinner.setSelection(selectedIndex)
                if (selectedIndex != voicePhrases.lastIndex) {
                    binding.voiceSettingsCustomPhrase.text = null
                }
            } else {
                binding.voiceSettingsPhraseSpinner.setSelection(0)
                binding.voiceSettingsCustomPhrase.text = null
            }


            binding.voiceSettingsMinSpeechLength.setText(
                settings.voice.advanced.getMinSpeechLength().toString()
            )

            binding.voiceSettingsMinSNR.setText(
                settings.voice.advanced.getMinSNR(getVoiceFlowAt(binding.voiceSettingsFlowSpinner.selectedItemPosition)!!)
                    .toString()
            )

            binding.voiceSettingsMaxSilenceLength.setText(
                settings.voice.advanced.getMaxSilenceLength().toString()
            )
        }
    }

    private fun applySettingsToVoiceWorkflowTabUi(settings: MiSnapSettings) {
        voiceWorkflowSettingsBinding?.let { binding ->
            val workflowSettings =
                settings.workflow.get(getString(VOICE_ANALYSIS_FRAGMENT_LABEL_RES))
                    ?.let { workflowSettingsString ->
                        Json.decodeFromString<VoiceProcessorFragment.WorkflowSettings>(
                            workflowSettingsString
                        )
                    }

            val defaultWorkflowSettings = VoiceProcessorFragment.getDefaultWorkflowSettings()

            (workflowSettings?.timeoutDuration
                ?: defaultWorkflowSettings.timeoutDuration)?.let {
                binding.voiceWorkflowSettingsSessionTimeoutDuration.setText(it.toString())
            }
        }
    }

    // endregion

    // region Spinner selection helpers

    // region general settings tab

    private fun getUseCaseAt(index: Int) =
        when (index) {
            9 -> MiSnapSettings.UseCase.VOICE
            8 -> MiSnapSettings.UseCase.NFC
            7 -> MiSnapSettings.UseCase.FACE
            6 -> MiSnapSettings.UseCase.BARCODE
            5 -> MiSnapSettings.UseCase.GENERIC_DOCUMENT
            4 -> MiSnapSettings.UseCase.CHECK_BACK
            3 -> MiSnapSettings.UseCase.CHECK_FRONT
            2 -> MiSnapSettings.UseCase.ID_BACK
            1 -> MiSnapSettings.UseCase.ID_FRONT
            0 -> MiSnapSettings.UseCase.PASSPORT
            else -> MiSnapSettings.UseCase.ID_FRONT
        }

    private fun getTriggerAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Analysis.Document.Trigger.MANUAL
            0 -> MiSnapSettings.Analysis.Document.Trigger.AUTO
            else -> null
        }

    private fun getCameraDirectionAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Camera.Profile.FACE_FRONT_CAMERA
            0 -> MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
            else -> null
        }

    private fun getTorchModeAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Camera.TorchMode.AUTO
            1 -> MiSnapSettings.Camera.TorchMode.ON
            0 -> MiSnapSettings.Camera.TorchMode.OFF
            else -> null
        }

    private fun getDeviceOrientationAt(index: Int) =
        when (index) {
            0 -> ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
            1 -> ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

    private fun getDeviceOrientationSpinnerIndex(deviceOrientation: Int) =
        when (deviceOrientation) {
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> 0
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT -> 1
            else -> 2
        }

    // endregion

    // region document settings tab

    private fun getDocumentAnalysisTriggerAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Analysis.Document.Trigger.MANUAL
            0 -> MiSnapSettings.Analysis.Document.Trigger.AUTO
            else -> null
        }

    private fun getSessionOrientationAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.Document.Orientation.DEVICE
            1 -> MiSnapSettings.Analysis.Document.Orientation.PORTRAIT
            0 -> MiSnapSettings.Analysis.Document.Orientation.LANDSCAPE
            else -> null
        }

    private fun getDocumentExtractionRequirementAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
            1 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
            0 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE
            else -> null
        }

    private fun getDocumentExtractionRequirementIndex(requirement: MiSnapSettings.Analysis.Document.ExtractionRequirement) =
        when (requirement) {
            MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE -> 0
            MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL -> 1
            MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED -> 2
        }

    private fun getGeoAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Analysis.Document.Check.Geo.GLOBAL
            0 -> MiSnapSettings.Analysis.Document.Check.Geo.US
            else -> null
        }

    // endregion

    // region document workflow settings tab

    private fun getDocumentReviewConditionAt(index: Int) =
        when (index) {
            3 -> DocumentAnalysisFragment.ReviewCondition.NEVER
            2 -> DocumentAnalysisFragment.ReviewCondition.WARNINGS
            1 -> DocumentAnalysisFragment.ReviewCondition.MANUAL
            0 -> DocumentAnalysisFragment.ReviewCondition.ALWAYS
            else -> null
        }

    private fun getDocumentReviewConditionIndex(reviewCondition: DocumentAnalysisFragment.ReviewCondition) =
        when (reviewCondition) {
            DocumentAnalysisFragment.ReviewCondition.ALWAYS -> 0
            DocumentAnalysisFragment.ReviewCondition.MANUAL -> 1
            DocumentAnalysisFragment.ReviewCondition.WARNINGS -> 2
            DocumentAnalysisFragment.ReviewCondition.NEVER -> 3
        }

    // endregion

    // region barcode settings tab

    private fun getBarcodeTriggerAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Analysis.Barcode.Trigger.MANUAL
            0 -> MiSnapSettings.Analysis.Barcode.Trigger.AUTO
            else -> null
        }

    private fun getDocumentBarcodeExtractionRequirementAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
            1 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
            0 -> MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE
            else -> null
        }

    private fun getDocumentBarcodeExtractionRequirementIndex(requirement: MiSnapSettings.Analysis.Document.ExtractionRequirement) =
        when (requirement) {
            MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE -> 0
            MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL -> 1
            MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED -> 2
        }

    private fun getDeviceMotionSensitivityAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.MotionDetectorSensitivity.HIGH
            1 -> MiSnapSettings.Analysis.MotionDetectorSensitivity.LOW
            0 -> MiSnapSettings.Analysis.MotionDetectorSensitivity.NONE
            else -> null
        }

    private fun getBarcodeSpinnerIndex(barcodeType: Int): Int {
        return when (barcodeType) {
            MiSnapSettings.Analysis.Barcode.Type.PDF417 -> 0
            else -> 1
        }
    }

    private fun getBarcodeTypeAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Analysis.Barcode.Type.ALL - MiSnapSettings.Analysis.Barcode.Type.CODE_128
            0 -> MiSnapSettings.Analysis.Barcode.Type.PDF417
            else -> null
        }

    private fun getBarcodeScanSpeedAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.Barcode.ScanSpeed.SLOW
            1 -> MiSnapSettings.Analysis.Barcode.ScanSpeed.MEDIUM
            0 -> MiSnapSettings.Analysis.Barcode.ScanSpeed.FAST
            else -> null
        }

    private fun getBarcodeOrientationAt(index: Int) =
        when (index) {
            0 -> MiSnapSettings.Analysis.Barcode.Orientation.LANDSCAPE
            1 -> MiSnapSettings.Analysis.Barcode.Orientation.PORTRAIT
            2 -> MiSnapSettings.Analysis.Barcode.Orientation.DEVICE
            else -> null
        }

    // endregion

    // region barcode workflow settings tab
    private fun getBarcodeReviewConditionAt(index: Int) =
        when (index) {
            3 -> BarcodeAnalysisFragment.ReviewCondition.NEVER
            2 -> BarcodeAnalysisFragment.ReviewCondition.WARNINGS
            1 -> BarcodeAnalysisFragment.ReviewCondition.MANUAL
            0 -> BarcodeAnalysisFragment.ReviewCondition.ALWAYS
            else -> null
        }

    private fun getBarcodeReviewConditionIndex(reviewCondition: BarcodeAnalysisFragment.ReviewCondition) =
        when (reviewCondition) {
            BarcodeAnalysisFragment.ReviewCondition.ALWAYS -> 0
            BarcodeAnalysisFragment.ReviewCondition.MANUAL -> 1
            BarcodeAnalysisFragment.ReviewCondition.WARNINGS -> 2
            BarcodeAnalysisFragment.ReviewCondition.NEVER -> 3
        }
    // endregion

    // region face settings tab

    private fun getFaceTriggerAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Analysis.Face.Trigger.MANUAL
            1 -> MiSnapSettings.Analysis.Face.Trigger.AUTO_SMILE
            0 -> MiSnapSettings.Analysis.Face.Trigger.AUTO
            else -> null
        }

    private fun getFaceTriggerSpinnerIndex(trigger: MiSnapSettings.Analysis.Face.Trigger) =
        when (trigger) {
            MiSnapSettings.Analysis.Face.Trigger.AUTO -> 0
            MiSnapSettings.Analysis.Face.Trigger.AUTO_SMILE -> 1
            MiSnapSettings.Analysis.Face.Trigger.MANUAL -> 2
        }

    // endregion

    // region face workflow settings tab

    private fun getFaceReviewConditionAt(index: Int) =
        when (index) {
            3 -> FaceAnalysisFragment.ReviewCondition.NEVER
            2 -> FaceAnalysisFragment.ReviewCondition.WARNINGS
            1 -> FaceAnalysisFragment.ReviewCondition.MANUAL
            0 -> FaceAnalysisFragment.ReviewCondition.ALWAYS
            else -> null
        }

    private fun getFaceReviewConditionIndex(reviewCondition: FaceAnalysisFragment.ReviewCondition) =
        when (reviewCondition) {
            FaceAnalysisFragment.ReviewCondition.ALWAYS -> 0
            FaceAnalysisFragment.ReviewCondition.MANUAL -> 1
            FaceAnalysisFragment.ReviewCondition.WARNINGS -> 2
            FaceAnalysisFragment.ReviewCondition.NEVER -> 3
        }

    private fun getLowLightSensitivityAt(index: Int) =
        when (index) {
            3 -> FaceAnalysisFragment.LowLightSensitivity.HIGH
            2 -> FaceAnalysisFragment.LowLightSensitivity.MEDIUM
            1 -> FaceAnalysisFragment.LowLightSensitivity.LOW
            0 -> FaceAnalysisFragment.LowLightSensitivity.NONE
            else -> null
        }

    private fun getLowLightSensitivityIndex(sensitivity: FaceAnalysisFragment.LowLightSensitivity) =
        when (sensitivity) {
            FaceAnalysisFragment.LowLightSensitivity.NONE -> 0
            FaceAnalysisFragment.LowLightSensitivity.LOW -> 1
            FaceAnalysisFragment.LowLightSensitivity.MEDIUM -> 2
            FaceAnalysisFragment.LowLightSensitivity.HIGH -> 3
        }

    // endregion

    // region NFC settings tab

    private fun getNfcDoctypeSpinnerIndex(doctype: MiSnapSettings.Nfc.Advanced.DocType) =
        when (doctype) {
            MiSnapSettings.Nfc.Advanced.DocType.PASSPORT -> 0
            MiSnapSettings.Nfc.Advanced.DocType.ID -> 1
            MiSnapSettings.Nfc.Advanced.DocType.EU_DL -> 2
        }

    private fun getNfcDoctypeAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Nfc.Advanced.DocType.EU_DL
            1 -> MiSnapSettings.Nfc.Advanced.DocType.ID
            0 -> MiSnapSettings.Nfc.Advanced.DocType.PASSPORT
            else -> null
        }

    private fun getNfcSoundAndVibrationSpinnerIndex(soundAndVibration: MiSnapSettings.Nfc.SoundAndVibration) =
        when (soundAndVibration) {
            MiSnapSettings.Nfc.SoundAndVibration.OFF -> 0
            MiSnapSettings.Nfc.SoundAndVibration.FOLLOW_SYSTEM -> 1
            MiSnapSettings.Nfc.SoundAndVibration.VIBRATE_ONLY -> 2
        }

    private fun getNfcSoundAndVibrationAt(index: Int) =
        when (index) {
            2 -> MiSnapSettings.Nfc.SoundAndVibration.VIBRATE_ONLY
            1 -> MiSnapSettings.Nfc.SoundAndVibration.FOLLOW_SYSTEM
            0 -> MiSnapSettings.Nfc.SoundAndVibration.OFF
            else -> null
        }

    private fun getVoiceFlowSpinnerIndex(flow: MiSnapSettings.Voice.Flow) =
        when (flow) {
            MiSnapSettings.Voice.Flow.ENROLLMENT -> 0
            MiSnapSettings.Voice.Flow.VERIFICATION -> 1
        }

    private fun getVoiceFlowAt(index: Int) =
        when (index) {
            1 -> MiSnapSettings.Voice.Flow.VERIFICATION
            0 -> MiSnapSettings.Voice.Flow.ENROLLMENT
            else -> null
        }

    private fun getVoicePhraseSpinnerIndex(phrase: String) =
        voicePhrases.indexOf(phrase)

    private fun getVoicePhraseAt(binding: VoiceSettingsBinding, index: Int) =
        if (index == voicePhrases.lastIndex) binding.voiceSettingsCustomPhrase.text.toString()
        else voicePhrases[index]

    private fun showErrorForInvalidCustomPhrase(binding: VoiceSettingsBinding) {
        binding.voiceSettingsCustomPhraseLayout.error =
            getString(R.string.misnapSampleAppSettingsVoiceCustomPhraseErrorEmptyField)
    }

    // endregion

    // endregion

    // region Tab management helpers

    private fun addGeneralSettingsTab() {
        val generalSettingsView = layoutInflater.inflate(R.layout.general_settings, null)
        generalSettingsBinding = GeneralSettingsBinding.bind(generalSettingsView)

        val lastSelectedPosition = sharedPrefs.getInt(SHARED_PREFS_LAST_SELECTED_USECASE_INDEX, 0);

        generalSettingsBinding?.let {
            //General settings UseCase spinner

            it.generalSettingsUseCaseSpinner.setSelection(lastSelectedPosition)
            it.generalSettingsUseCaseSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        useCaseIndex = position
                        val useCase = getUseCaseAt(position)

                        val prefsSettings: String? = if (useCase == MiSnapSettings.UseCase.VOICE) {
                            val flow =
                                sharedPrefs.getInt(SHARED_PREFS_LAST_SELECTED_VOICE_FLOW_INDEX, 0)
                            sharedPrefs.getString(
                                getUseCaseSettingsPrefsKey(
                                    useCase,
                                    getVoiceFlowAt(flow)
                                ), null
                            )
                        } else {
                            sharedPrefs.getString(getUseCaseSettingsPrefsKey(useCase), null)
                        }

                        val settings =
                            prefsSettings
                                ?.let { serializedSettings ->
                                    Json.decodeFromString(serializedSettings)
                                } ?: MiSnapSettings(useCase, license)

                        applySettingsToUi(settings)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        adapter.addView(
            generalSettingsView,
            getString(R.string.misnapSampleAppSettingsGeneralTabTitle)
        )
    }

    private fun addCameraSettingsTab() {
        val cameraSettingsView = layoutInflater.inflate(R.layout.camera_settings, null)
        cameraSettingsBinding = CameraSettingsBinding.bind(cameraSettingsView)

        cameraSettingsBinding?.let {
            it.cameraSettingsRecordSessionCheckbox.setOnCheckedChangeListener { _, isChecked ->
                it.cameraSettingsVideoResolutionWidth.isEnabled = isChecked
                it.cameraSettingsVideoResolutionHeight.isEnabled = isChecked
                it.cameraSettingsVideoBitrate.isEnabled = isChecked
            }
        }

        adapter.addView(
            cameraSettingsView,
            getString(R.string.misnapSampleAppSettingsCameraTabTitle)
        )
    }

    private fun addDocumentSettingsTab() {
        val documentSettingsView = layoutInflater.inflate(R.layout.document_settings, null)
        documentSettingsBinding = DocumentSettingsBinding.bind(documentSettingsView)

        documentSettingsBinding?.let {

            //Document settings RequestOCR spinner
            it.documentSettingsDocumentRequestOcrSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        val isDocumentOcrNone =
                            getDocumentExtractionRequirementAt(position) == MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE

                        val isBarcodeUseCase =
                            getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.BARCODE


                        if (isTabWithTitlePresent(getString(BARCODE_TAB_TITLE_RES)) && !isBarcodeUseCase) {
                            it.documentSettingsBarcodeRequestExtractionSpinner.isEnabled =
                                isDocumentOcrNone
                            barcodeSettingsBinding?.let { barcodeBinding ->
                                barcodeBinding.barcodeSettingsTriggerSpinner.isEnabled =
                                    isDocumentOcrNone
                                barcodeBinding.barcodeSettingsTypeSpinner.isEnabled =
                                    isDocumentOcrNone
                                barcodeBinding.barcodeSettingsScanSpeedSpinner.isEnabled =
                                    isDocumentOcrNone
                                barcodeBinding.barcodeSettingsOrientationSpinner.isEnabled =
                                    isDocumentOcrNone
                            }
                        }

                        if (!isDocumentOcrNone && getDocumentBarcodeExtractionRequirementAt(it.documentSettingsBarcodeRequestExtractionSpinner.selectedItemPosition) != MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE) {
                            it.documentSettingsBarcodeRequestExtractionSpinner.setSelection(
                                getDocumentBarcodeExtractionRequirementIndex(MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE)
                            )
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

            it.documentSettingsBarcodeRequestExtractionSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        barcodeExtractionRequirementIndex = position

                        val isBarcodeOcrNone =
                            getDocumentBarcodeExtractionRequirementAt(position) == MiSnapSettings.Analysis.Document.ExtractionRequirement.NONE
                        val isBarcodeUseCase =
                            getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.BARCODE

                        val isUnSupportedUseCase =
                            getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.GENERIC_DOCUMENT ||
                                getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.CHECK_FRONT ||
                                getUseCaseAt(useCaseIndex) == MiSnapSettings.UseCase.CHECK_BACK


                        if (!isUnSupportedUseCase && isTabWithTitlePresent(
                                getString(
                                    DOCUMENT_TAB_TITLE_RES
                                )
                            ) && !isBarcodeUseCase
                        ) {
                            documentSettingsBinding?.let { documentBinding ->
                                documentBinding.documentSettingsDocumentRequestOcrSpinner.isEnabled =
                                    isBarcodeOcrNone
                            }

                            barcodeSettingsBinding?.let { barcodeBinding ->
                                barcodeBinding.barcodeSettingsTriggerSpinner.isEnabled =
                                    !isBarcodeOcrNone
                                barcodeBinding.barcodeSettingsTypeSpinner.isEnabled =
                                    !isBarcodeOcrNone
                                barcodeBinding.barcodeSettingsScanSpeedSpinner.isEnabled =
                                    !isBarcodeOcrNone
                                barcodeBinding.barcodeSettingsOrientationSpinner.isEnabled =
                                    !isBarcodeOcrNone
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

            it.documentSettingsTriggerSpinner.onItemSelectedListener =
                object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        documentWorkflowSettingsBinding?.documentWorkflowSettingsShowManualButtonBox?.isChecked =
                            getDocumentAnalysisTriggerAt(position) ==
                                MiSnapSettings.Analysis.Document.Trigger.MANUAL
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

            it.documentExtractionTooltip.setOnClickListener { tooltip ->
                showTooltipInfo(
                    tooltip,
                    getString(R.string.misnapSampleAppSettingsDocumentExtractionTooltip)
                )
            }

            it.barcodeExtractionTooltip.setOnClickListener { tooltip ->
                showTooltipInfo(
                    tooltip,
                    getString(R.string.misnapSampleAppSettingsDocumentBarcodeExtractionTooltip)
                )
            }

            it.geoTooltip.setOnClickListener { tooltip ->
                showTooltipInfo(
                    tooltip,
                    getString(R.string.misnapSampleAppSettingsDocumentGeoTooltip)
                )
            }

            it.optionalDataRedactionTooltip.setOnClickListener { tooltip ->
                showTooltipInfo(
                    tooltip,
                    getString(R.string.misnapSampleAppSettingsDocumentRedactOptionalDataTooltip)
                )
            }

            it.documentClassificationTooltip.setOnClickListener { tooltip ->
                showTooltipInfo(
                    tooltip,
                    getString(R.string.misnapSampleAppSettingsDocumentClassificationTooltip)
                )
            }

            it.documentSettingsRedactOptionalData.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    it.documentSettingsDocumentRequestOcrSpinner.setSelection(
                        getDocumentExtractionRequirementIndex(MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED)
                    )
                }
            }

            it.documentSettingsEnableClassification.setOnCheckedChangeListener { _, checked ->
                val supportedClassificationUseCases = listOf(
                    MiSnapSettings.UseCase.ID_FRONT,
                    MiSnapSettings.UseCase.ID_BACK,
                    MiSnapSettings.UseCase.PASSPORT
                )

                it.documentSettingsEnableClassification.isEnabled =
                    supportedClassificationUseCases.contains(getUseCaseAt(useCaseIndex))
            }
        }
        adapter.addView(
            documentSettingsView,
            getString(DOCUMENT_TAB_TITLE_RES)
        )
    }

    private fun addDocumentWorkflowSettingsTab() {
        val documentWorkflowSettingsView =
            layoutInflater.inflate(R.layout.document_workflow_settings, null)
        documentWorkflowSettingsBinding =
            DocumentWorkflowSettingsBinding.bind(documentWorkflowSettingsView)

        adapter.addView(documentWorkflowSettingsView, getString(WORKFLOW_TAB_TITLE_RES))
    }

    private fun addBarcodeSettingsTab() {
        val barcodeSettingsView = layoutInflater.inflate(R.layout.barcode_settings, null)
        barcodeSettingsBinding = BarcodeSettingsBinding.bind(barcodeSettingsView)

        adapter.addView(
            barcodeSettingsView,
            getString(BARCODE_TAB_TITLE_RES)
        )
    }

    private fun addBarcodeWorkflowSettingsTab() {
        val barcodeWorkflowSettingsView =
            layoutInflater.inflate(R.layout.barcode_workflow_settings, null)
        barcodeWorkflowSettingsBinding =
            BarcodeWorkflowSettingsBinding.bind(barcodeWorkflowSettingsView)

        adapter.addView(barcodeWorkflowSettingsView, getString(WORKFLOW_TAB_TITLE_RES))
    }

    private fun addFaceSettingsTab() {
        val faceSettingsView = layoutInflater.inflate(R.layout.face_settings, null)
        faceSettingsBinding = FaceSettingsBinding.bind(faceSettingsView)

        faceSettingsBinding?.let {
            //Face trigger spinner
            it.faceSettingsTriggerSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        it.faceSettingsTriggerDelay.isEnabled =
                            getFaceTriggerAt(position) != MiSnapSettings.Analysis.Face.Trigger.MANUAL
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            it.analysisSettings.findViewById<Spinner>(R.id.analysisSettingsDeviceMotionSensitivitySpinner).isEnabled = false
        }

        adapter.addView(faceSettingsView, getString(FACE_TAB_TITLE_RES))
    }

    private fun addFaceWorkflowSettingsTab() {
        val faceWorkflowSettingsView =
            layoutInflater.inflate(R.layout.face_workflow_settings, null)
        faceWorkflowSettingsBinding =
            FaceWorkflowSettingsBinding.bind(faceWorkflowSettingsView)

        faceWorkflowSettingsBinding?.let {
            it.faceWorkflowSettingsShowCountdownTimer.setOnCheckedChangeListener { _, isChecked ->
                it.faceWorkflowSettingsCountdownTimerDuration.isEnabled = isChecked
            }
        }

        adapter.addView(faceWorkflowSettingsView, getString(WORKFLOW_TAB_TITLE_RES))
    }

    private fun addNfcSettingsTab() {
        val nfcSettingsView = layoutInflater.inflate(R.layout.nfc_settings, null)
        nfcSettingsBinding = NfcSettingsBinding.bind(nfcSettingsView)

        adapter.addView(nfcSettingsView, getString(NFC_TAB_TITLE_RES))
    }

    private fun addNfcWorkflowSettingsTab() {
        val nfcWorkflowSettingsView =
            layoutInflater.inflate(R.layout.nfc_workflow_settings, null)
        nfcWorkflowSettingsBinding =
            NfcWorkflowSettingsBinding.bind(nfcWorkflowSettingsView)

        adapter.addView(nfcWorkflowSettingsView, getString(WORKFLOW_TAB_TITLE_RES))
    }

    private fun addVoiceSettingsTab() {
        val voiceSettingsView = layoutInflater.inflate(R.layout.voice_settings, null)
        voiceSettingsBinding = VoiceSettingsBinding.bind(voiceSettingsView).also {
            it.voiceSettingsPhraseSpinner.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                voicePhrases
            ).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }

            //Phrase trigger spinner
            it.voiceSettingsPhraseSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        it.voiceSettingsCustomPhraseLayout.isEnabled =
                            position == voicePhrases.lastIndex
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }

                }

            //Flow trigger spinner
            it.voiceSettingsFlowSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        voiceFlowIndex = position

                        val voiceSettings =
                            sharedPrefs.getString(
                                getUseCaseSettingsPrefsKey(
                                    getUseCaseAt(useCaseIndex),
                                    getVoiceFlowAt(position)
                                ), null
                            )
                                ?.let { serializedSettings ->
                                    Json.decodeFromString(serializedSettings)
                                } ?: MiSnapSettings(getUseCaseAt(useCaseIndex), license).apply {
                                this.voice.flow = getVoiceFlowAt(
                                    position
                                )
                            }

                        applySettingsToVoiceTabUi(voiceSettings)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }

        adapter.addView(voiceSettingsView, getString(VOICE_TAB_TITLE_RES))
    }

    private fun addVoiceWorkflowSettingsTab() {
        val voiceWorkflowSettingsView =
            layoutInflater.inflate(R.layout.voice_workflow_settings, null)
        voiceWorkflowSettingsBinding =
            VoiceWorkflowSettingsBinding.bind(voiceWorkflowSettingsView)

        adapter.addView(voiceWorkflowSettingsView, getString(WORKFLOW_TAB_TITLE_RES))
    }

    // Using the tab title as identifier because the use of Ids is unreliable when used with a viewpager
    //  and tab titles should be unique anyways.
    private fun findTabIndexByTabTitle(title: String): Int? {
        for (tabIndex in 0..binding.settingsTabLayout.tabCount) {
            if (binding.settingsTabLayout.getTabAt(tabIndex)?.text == title) {
                return tabIndex
            }
        }

        return null
    }

    private fun isTabWithTitlePresent(title: String): Boolean =
        findTabIndexByTabTitle(title) != null

    private fun showTooltipInfo(view: View, tooltipText: String) {
        TooltipCompat.setTooltipText(
            view,
            tooltipText
        )
        view.performLongClick()
    }

    // endregion

    /**
     * Pre queries the camera's capabilities before starting a session in case the camera is insufficient
     * This is optional, as the SDK can detect the camera support at runtime and will either fallback
     * to manual or post an error if the camera is completely unusable.  If the camera is insufficient
     * for your purposes, consider skipping the MiSnap flow.
     */
    private fun startMiSnapSession(misnapWorkflowStep: MiSnapWorkflowStep) {
        // Pre-query the device's camera support to see what sort of analysis is supported
        CameraUtil.findSupportedCamera(
            requireContext(),
            viewLifecycleOwner,
            misnapWorkflowStep.settings.camera
        ) { cameraSupportResult ->
            when (cameraSupportResult) {
                // MiSnap supports this camera
                is CameraUtil.CameraSupportResult.Success -> {
                    if (cameraSupportResult.cameraInfo.supportsAutoAnalysis) {
                        // This camera supports auto, set the trigger to auto
                        when (misnapWorkflowStep.settings.useCase) {
                            MiSnapSettings.UseCase.FACE -> {
                                if (misnapWorkflowStep.settings.analysis.face.trigger == null) {
                                    misnapWorkflowStep.settings.analysis.face.trigger =
                                        MiSnapSettings.Analysis.Face.Trigger.default()
                                }
                            }
                            MiSnapSettings.UseCase.BARCODE -> {
                                if (misnapWorkflowStep.settings.analysis.barcode.trigger == null) {
                                    misnapWorkflowStep.settings.analysis.barcode.trigger =
                                        MiSnapSettings.Analysis.Barcode.Trigger.AUTO
                                }
                            }
                            else -> {
                                if (misnapWorkflowStep.settings.analysis.document.trigger == null) {
                                    misnapWorkflowStep.settings.analysis.document.trigger =
                                        MiSnapSettings.Analysis.Document.Trigger.AUTO
                                }
                            }
                        }
                    } else {
                        // This camera does not support auto, set the trigger to manual
                        when (misnapWorkflowStep.settings.useCase) {
                            MiSnapSettings.UseCase.FACE -> {
                                misnapWorkflowStep.settings.analysis.face.trigger =
                                    MiSnapSettings.Analysis.Face.Trigger.MANUAL
                            }
                            MiSnapSettings.UseCase.BARCODE -> {
                                misnapWorkflowStep.settings.analysis.barcode.trigger =
                                    MiSnapSettings.Analysis.Barcode.Trigger.MANUAL
                            }
                            else -> {
                                misnapWorkflowStep.settings.analysis.document.trigger =
                                    MiSnapSettings.Analysis.Document.Trigger.MANUAL
                            }
                        }
                    }

                    registerForActivityResult.launch(
                        MiSnapWorkflowActivity.buildIntent(
                            requireContext(),
                            misnapWorkflowStep,
                            disableScreenshots = false // allow screenshots only because this is used for demos
                        )
                    )
                }
                is CameraUtil.CameraSupportResult.Error -> {
                    // This camera does not support auto or manual,
                    displayError(R.string.misnapAppUtilCameraErrorMessage)
                }
            }
        }
    }

    /**
     * Pre queries the microphones capabilities before starting a session.
     * This is optional, as the SDK can detect the microphone support at runtime and will post an
     * error if there's not a sufficient microphone or its unusable.
     * If the microphone is insufficient for your purposes, consider skipping the MiSnap flow.
     */
    private fun queryMicrophoneSupport() {
        val supportedMicrophoneSupportResult =
            MicrophoneUtil.findSupportedMicrophone(requireContext())

        if (supportedMicrophoneSupportResult is MicrophoneUtil.MicrophoneSupportResult.Error) {
            displayError(R.string.misnapAppUtilMicrophoneErrorMessage)
        }
    }

    private fun displayError(stringId: Int) {
        Toast.makeText(
            requireContext(),
            getString(stringId),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun getUseCaseSettingsPrefsKey(
        useCase: MiSnapSettings.UseCase,
        flow: MiSnapSettings.Voice.Flow? = null
    ) =
        if (useCase == MiSnapSettings.UseCase.VOICE) {
            "SAMPLE_APP_${useCase}_${flow}_SETTINGS"
        } else {
            "SAMPLE_APP_${useCase}_SETTINGS"
        }

    companion object {
        private const val SHARED_PREFS_FILE_NAME = "MiSnapSettingsSharedPreferences"
        private const val SHARED_PREFS_LAST_SELECTED_USECASE_INDEX =
            "UseCaseSpinnerLastSelectedIndex"
        private const val SHARED_PREFS_LAST_SELECTED_VOICE_FLOW_INDEX =
            "VoiceFlowSpinnerLastSelectedIndex"
        private const val CAMERA_TAB_TITLE_RES = R.string.misnapSampleAppSettingsCameraTabTitle
        private const val DOCUMENT_TAB_TITLE_RES =
            R.string.misnapSampleAppSettingsDocumentTabTitle
        private const val BARCODE_TAB_TITLE_RES =
            R.string.misnapSampleAppSettingsBarcodeTabTitle
        private const val FACE_TAB_TITLE_RES = R.string.misnapSampleAppSettingsFaceTabTitle
        private const val NFC_TAB_TITLE_RES = R.string.misnapSampleAppSettingsNfcTabTitle
        private const val WORKFLOW_TAB_TITLE_RES =
            R.string.misnapSampleAppSettingsWorkflowTabTitle
        private const val VOICE_TAB_TITLE_RES = R.string.misnapSampleAppSettingsVoiceTabTitle
        private const val DOCUMENT_ANALYSIS_FRAGMENT_LABEL_RES =
            R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel
        private const val BARCODE_ANALYSIS_FRAGMENT_LABEL_RES =
            R.string.misnapWorkflowBarcodeAnalysisFlowBarcodeAnalysisFragmentLabel
        private const val FACE_ANALYSIS_FRAGMENT_LABEL_RES =
            R.string.misnapWorkflowFaceAnalysisFlowFaceAnalysisFragmentLabel
        private const val NFC_READER_FRAGMENT_LABEL_RES =
            R.string.misnapWorkflowNfcReaderFlowNfcReaderFragmentLabel
        private const val VOICE_ANALYSIS_FRAGMENT_LABEL_RES =
            R.string.misnapWorkflowVoiceProcessorFlowVoiceProcessorFragmentLabel

        //Bit flags
        private const val PERMISSION_REQUEST_CAMERA = 1
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 2
        private const val PERMISSION_REQUEST_ALL = 15
    }
}
