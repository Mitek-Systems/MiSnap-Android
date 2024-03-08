package com.miteksystems.misnap.sampleapp

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.miteksystems.misnap.R
import com.miteksystems.misnap.camera.requireProfile
import com.miteksystems.misnap.camera.util.CameraUtil
import com.miteksystems.misnap.camera.util.CameraUtil.CameraSupportResult
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.databinding.FragmentDemoBinding
import com.miteksystems.misnap.face.default
import com.miteksystems.misnap.sampleapp.results.SampleAppViewModel
import com.miteksystems.misnap.voice.MicrophoneUtil
import com.miteksystems.misnap.workflow.MiSnapWorkflowActivity
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.workflow.util.PermissionsUtil
import com.miteksystems.misnap.workflow.util.ViewBindingUtil

class DemoFragment : Fragment(R.layout.fragment_demo) {
    private val binding by ViewBindingUtil.viewBinding(
        this,
        FragmentDemoBinding::bind
    )
    private var frontCameraSupportResult: CameraSupportResult.Success? = null
    private var backCameraSupportResult: CameraSupportResult.Success? = null
    private val registerForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val sampleAppViewModel =
                ViewModelProvider(requireActivity())[SampleAppViewModel::class.java]
            sampleAppViewModel.results = MiSnapWorkflowActivity.Result.results
            MiSnapWorkflowActivity.Result.clearResults()

            findNavController().navigate(R.id.documentResultsFragment)
        }

    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayVersionCode(view)

        checkPermissions()

        val license = getString(R.string.misnapSampleAppLicense)

        binding.checkFrontSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.CHECK_FRONT,
                        license
                    )
                )
            )
        }

        binding.checkBackSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.CHECK_BACK,
                        license
                    )
                )
            )
        }

        binding.idFrontSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.ID_FRONT,
                        license
                    )
                )
            )
        }

        binding.idBackSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.ID_BACK,
                        license
                    )
                )
            )
        }

        binding.passportSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.PASSPORT,
                        license
                    )
                )
            )
        }

        binding.barcodeSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.BARCODE,
                        license
                    )
                )
            )
        }

        binding.faceSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.FACE,
                        license
                    )
                )
            )
        }

        binding.nfcSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(
                    MiSnapSettings(
                        MiSnapSettings.UseCase.NFC,
                        license
                    )
                )
            )
        }

        binding.voiceSessionEnrollmentButton.setOnClickListener {
            val settings = MiSnapSettings(MiSnapSettings.UseCase.VOICE, license).apply {
                voice.flow = MiSnapSettings.Voice.Flow.ENROLLMENT
            }

            startMiSnapSession(MiSnapWorkflowStep(settings))
        }

        binding.voiceSessionVerificationButton.setOnClickListener {
            val settings = MiSnapSettings(MiSnapSettings.UseCase.VOICE, license).apply {
                voice.flow = MiSnapSettings.Voice.Flow.VERIFICATION
                voice.phrase =
                    resources.getStringArray(R.array.misnapWorkflowVoicePhraseSelectionFragmentPhrases)
                        .firstOrNull()
            }

            startMiSnapSession(MiSnapWorkflowStep(settings))
        }

        binding.advancedSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.navigateSettings)
        }

        binding.nfcCombinedIdSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
                    analysis.document.documentExtractionRequirement =
                        MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
                }),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.ID_BACK, license).apply {
                    analysis.document.documentExtractionRequirement =
                        MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
                }),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license))
            )
        }

        binding.nfcCombinedPassportSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.PASSPORT, license).apply {
                    analysis.document.documentExtractionRequirement =
                        MiSnapSettings.Analysis.Document.ExtractionRequirement.REQUIRED
                }),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.BARCODE, license).apply {
                    analysis.barcode.type = MiSnapSettings.Analysis.Barcode.Type.QR_CODE
                }, behavior = MiSnapWorkflowStep.Behavior.OnMissingNldBSN),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license))
            )
        }

        binding.combinedCheckSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.CHECK_FRONT, license)),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.CHECK_BACK, license))
            )
        }

        binding.nfcCombinedIdFaceSessionButton.setOnClickListener {
            startMiSnapSession(
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT, license).apply {
                    analysis.document.documentExtractionRequirement =
                        MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
                }),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.ID_BACK, license).apply {
                    analysis.document.documentExtractionRequirement =
                        MiSnapSettings.Analysis.Document.ExtractionRequirement.OPTIONAL
                }),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.NFC, license)),
                MiSnapWorkflowStep(MiSnapSettings(MiSnapSettings.UseCase.FACE, license))
            )
        }
    }

    private fun checkPermissions() {
        val missingPermissions =
            permissions.filter { !PermissionsUtil.hasPermission(requireContext(), it) }

        if (!missingPermissions.contains(Manifest.permission.CAMERA)) {
            queryCameraSupport()
        }

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
            requestPermissions(nonRationalePermissions.toTypedArray(), PERMISSION_REQUEST_ALL)
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
            } else {
                queryCameraSupport()
            }
        }

        if (requestCode and PERMISSION_REQUEST_RECORD_AUDIO == PERMISSION_REQUEST_RECORD_AUDIO) {
            if (grantResults.isEmpty() || PackageManager.PERMISSION_GRANTED != grantResults.first()) {
                // Permission denied, MiSnap SDK will not be usable for usecases that depend on this permission
            } else {
                queryMicrophoneSupport()
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

    /**
     * Pre queries the camera's capabilities before starting a session in case the camera is insufficient
     * This is optional, as the SDK can detect the camera support at runtime and will either fallback
     * to manual or post an error if the camera is completely unusable.  If the camera is insufficient
     * for your purposes, consider skipping the MiSnap flow.
     */
    private fun queryCameraSupport() {
        val backCameraSettings = MiSnapSettings.Camera().apply {
            profile = MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA
        }

        val frontCameraSettings = MiSnapSettings.Camera().apply {
            profile = MiSnapSettings.Camera.Profile.FACE_FRONT_CAMERA
        }

        CameraUtil.findSupportedCamera(
            requireContext(),
            viewLifecycleOwner,
            backCameraSettings
        ) { backSupportResult ->
            when (backSupportResult) {
                is CameraSupportResult.Success -> {
                    backCameraSupportResult = backSupportResult
                }

                is CameraSupportResult.Error -> {
                    displayError(R.string.misnapAppUtilBackCameraErrorMessage)
                }
            }

            CameraUtil.findSupportedCamera(
                requireContext(),
                viewLifecycleOwner,
                frontCameraSettings
            ) { frontSupportResult ->
                when (frontSupportResult) {
                    is CameraSupportResult.Success -> {
                        frontCameraSupportResult = frontSupportResult
                    }

                    is CameraSupportResult.Error -> {
                        displayError(R.string.misnapAppUtilFrontCameraErrorMessage)
                    }
                }
            }
        }
    }

    /**
     * Starts a session with the given steps, the steps are mutated to add the camera
     * settings if not yet specified.
     */
    private fun startMiSnapSession(
        misnapWorkflowStep: MiSnapWorkflowStep,
        vararg misnapWorkflowSteps: MiSnapWorkflowStep
    ) {
        mutableListOf(misnapWorkflowStep).apply {
            addAll(misnapWorkflowSteps)
        }.forEach {
            if (!hasCameraSettings(it.settings)) {
                when (it.settings.camera.requireProfile()) {
                    MiSnapSettings.Camera.Profile.DOCUMENT_BACK_CAMERA -> {
                        backCameraSupportResult?.let { cameraSupportResult ->
                            applyCameraSettings(cameraSupportResult, it.settings)
                        }
                    }
                    else -> {
                        frontCameraSupportResult?.let { cameraSupportResult ->
                            applyCameraSettings(cameraSupportResult, it.settings)
                        }
                    }
                }
            }
        }

        registerForActivityResult.launch(
            MiSnapWorkflowActivity.buildIntent(
                requireContext(),
                misnapWorkflowStep,
                *misnapWorkflowSteps,
                disableScreenshots = false // allow screenshots only because this is used for demos
            )
        )
    }

    private fun hasCameraSettings(settings: MiSnapSettings) =
        when (settings.useCase) {
            MiSnapSettings.UseCase.FACE -> {
                settings.analysis.face.trigger != null
            }
            MiSnapSettings.UseCase.BARCODE -> {
                settings.analysis.barcode.trigger != null
            }
            else -> {
                settings.analysis.document.trigger != null
            }
        }

    /**
     * Mutates the [settings] by applying the corresponding camera parameters.
     */
    private fun applyCameraSettings(
        cameraSupportResult: CameraSupportResult.Success,
        settings: MiSnapSettings
    ) {
        if (cameraSupportResult.cameraInfo.supportsAutoAnalysis) {
            // This camera supports auto, set the trigger to auto
            when (settings.useCase) {
                MiSnapSettings.UseCase.FACE -> {
                    if (settings.analysis.face.trigger == null) {
                        settings.analysis.face.trigger =
                            MiSnapSettings.Analysis.Face.Trigger.default()
                    }
                }
                MiSnapSettings.UseCase.BARCODE -> {
                    settings.analysis.barcode.trigger = MiSnapSettings.Analysis.Barcode.Trigger.AUTO
                }
                else -> {
                    settings.analysis.document.trigger =
                        MiSnapSettings.Analysis.Document.Trigger.AUTO
                }
            }
        } else {
            // This camera does not support auto, set the trigger to manual
            when (settings.useCase) {
                MiSnapSettings.UseCase.FACE -> {
                    settings.analysis.face.trigger =
                        MiSnapSettings.Analysis.Face.Trigger.MANUAL
                }
                MiSnapSettings.UseCase.BARCODE -> {
                    settings.analysis.barcode.trigger =
                        MiSnapSettings.Analysis.Barcode.Trigger.MANUAL
                }
                else -> {
                    settings.analysis.document.trigger =
                        MiSnapSettings.Analysis.Document.Trigger.MANUAL
                }
            }
        }
    }

    private fun displayError(stringId: Int) {
        Toast.makeText(
            requireContext(),
            getString(stringId),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun displayVersionCode(view: View) {
        val versionString = """
            MiSnap SDK
            VersionName: ${getString(R.string.misnapVersion)}
            """.trimIndent()
        view.findViewById<TextView>(R.id.versionInfo).text = versionString
    }

    companion object {
        //Bit flags
        private const val PERMISSION_REQUEST_CAMERA = 1
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 2
        private const val PERMISSION_REQUEST_ALL = 15
    }
}
