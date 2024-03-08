package com.miteksystems.misnap.apputil.util

import android.content.Context
import com.miteksystems.misnap.apputil.R
import com.miteksystems.misnap.core.MiSnapSettings
import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import org.json.JSONException
import org.json.JSONObject

object ResultsUtil {

    fun getUseCaseName(mibiData: String, context: Context) =
        try {
            val useCase = MiSnapSettings.UseCase.valueOf(
                JSONObject(mibiData).getJSONObject("PlatformPrivate")
                    .getJSONObject("OriginalSettings")
                    .getString("useCase")
            )

            when (useCase) {
                MiSnapSettings.UseCase.PASSPORT -> {
                    context.getString(R.string.misnapAppUtilResultsUseCasePassportTabTitle)
                }

                MiSnapSettings.UseCase.ID_FRONT -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseIdFrontTabTitle)
                }

                MiSnapSettings.UseCase.ID_BACK -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseIdBackTabTitle)
                }

                MiSnapSettings.UseCase.CHECK_FRONT -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseCheckFrontTabTitle)
                }

                MiSnapSettings.UseCase.CHECK_BACK -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseCheckBackTabTitle)
                }

                MiSnapSettings.UseCase.GENERIC_DOCUMENT -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseGenericDocumentTabTitle)
                }

                MiSnapSettings.UseCase.BARCODE -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseBarcodeTabTitle)
                }

                MiSnapSettings.UseCase.FACE -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseFaceTabTitle)
                }

                MiSnapSettings.UseCase.NFC -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseNfcTabTitle)
                }

                MiSnapSettings.UseCase.VOICE -> {
                    context.getString(R.string.misnapAppUtilResultsUseCaseVoiceTabTitle)
                }
            }
        } catch (e: JSONException) {
            context.getString(R.string.misnapAppUtilResultsUseCaseUnknownTabTitle)
        }

    fun getErrorMessageId(stepResult: MiSnapWorkflowStep.Result.Error) =
        when (stepResult.errorResult.error) {
            is MiSnapWorkflowError.Camera -> {
                R.string.misnapAppUtilCameraErrorMessage
            }

            is MiSnapWorkflowError.Analysis -> {
                R.string.misnapAppUtilAnalysisErrorMessage
            }

            is MiSnapWorkflowError.Permission -> {
                R.string.misnapAppUtilCameraPermissionErrorMessage
            }

            is MiSnapWorkflowError.SettingState -> {
                R.string.misnapAppUtilSettingsErrorMessage
            }

            is MiSnapWorkflowError.Nfc.DeviceDoesNotSupportNfc -> {
                R.string.misnapAppUtilNfcDeviceErrorMessage
            }

            is MiSnapWorkflowError.Nfc.DocumentNotNfcEnabled -> {
                R.string.misnapAppUtilNfcDocumentErrorMessage
            }

            is MiSnapWorkflowError.Nfc.InvalidCredentials -> {
                R.string.misnapAppUtilNfcCredentialsErrorMessage
            }

            is MiSnapWorkflowError.Nfc.Skipped -> {
                R.string.misnapAppUtilNfcSkippedErrorMessage
            }

            is MiSnapWorkflowError.Voice.Skipped -> {
                R.string.misnapAppUtilVoiceSkippedErrorMessage
            }

            is MiSnapWorkflowError.Cancelled -> {
                R.string.misnapAppUtilCancelledErrorMessage
            }

            is MiSnapWorkflowError.CombinedWorkflow -> {
                R.string.misnapAppUtilCombinedWorkflowCancelledErrorMessage
            }

            is MiSnapWorkflowError.CombinedWorkflowSkippedStep -> {
                R.string.misnapAppUtilCombinedWorkflowSkippedStepErrorMessage
            }

            is MiSnapWorkflowError.License -> {
                R.string.misnapAppUtilCombinedWorkflowLicenseErrorMessage
            }

            is MiSnapWorkflowError.Voice.Execution -> R.string.misnapAppUtilVoiceExecutionErrorMessage
            is MiSnapWorkflowError.Voice.Initialization -> R.string.misnapAppUtilVoiceInitializationErrorMessage
            is MiSnapWorkflowError.Voice.InputFormat -> R.string.misnapAppUtilVoiceInputFormatErrorMessage
            is MiSnapWorkflowError.Voice.MicrophoneMuted -> R.string.misnapAppUtilVoiceMicrophoneMutedErrorMessage
            is MiSnapWorkflowError.Voice.MissingRequirement -> R.string.misnapAppUtilVoiceMissingRequirementErrorMessage
        }

    fun getImageTypeMessageId(imageType: String) =
        when (imageType.lowercase()) {
            "Unknown".lowercase() -> R.string.misnapAppUtilResultsServerResultImageTypeUnknown
            "IdDocument".lowercase() -> R.string.misnapAppUtilResultsServerResultImageTypeIdDocument
            "Biometric".lowercase() -> R.string.misnapAppUtilResultsServerResultImageTypeBiometric
            else -> R.string.misnapAppUtilResultsServerResultImageTypeUnknown
        }

    fun getImageClassificationTypeMessageId(imageType: String) =
        when (imageType.lowercase()) {
            "Unknown".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeUnknown
            "DriversLicenseFront".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeDriversLicenseFront
            "DriversLicenseBack".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeDriversLicenseBack
            "PassportSignature".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypePassportSignature
            "PassportPicturePage".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypePassportPicturePage
            "IdentificationCardFront".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeIdentificationCardFront
            "IdentificationCardBack".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeIdentificationCardBack
            "ResidencePermitFront".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeResidencePermitFront
            "ResidencePermitBack".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeResidencePermitBack
            "PassportCardFront".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypePassportCardFront
            "PassportCardBack".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypePassportCardBack
            "HealthServicesCardFront".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeHealthServicesCardFront
            "HealthServicesCardBack".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeHealthServicesCardBack
            "Visa".lowercase() -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeVisa
            else -> R.string.misnapAppUtilResultsServerResultImageClassificationTypeUnknown
        }
}
