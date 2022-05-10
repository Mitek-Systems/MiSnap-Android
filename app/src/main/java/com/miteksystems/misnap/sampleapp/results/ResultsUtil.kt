package com.miteksystems.misnap.sampleapp.results

import com.miteksystems.misnap.workflow.MiSnapWorkflowError
import com.miteksystems.misnap.workflow.MiSnapWorkflowStep
import com.miteksystems.misnap.R

fun getErrorMessageId(stepResult: MiSnapWorkflowStep.Result.Error) =
    when (stepResult.errorResult.error) {
        is MiSnapWorkflowError.Camera -> {
            R.string.misnapSampleAppCameraErrorMessage
        }
        is MiSnapWorkflowError.Analysis -> {
            R.string.misnapSampleAppAnalysisErrorMessage
        }
        is MiSnapWorkflowError.Permission -> {
            R.string.misnapSampleAppCameraPermissionErrorMessage
        }
        is MiSnapWorkflowError.SettingState -> {
            R.string.misnapSampleAppSettingsErrorMessage
        }
        is MiSnapWorkflowError.Nfc.DeviceDoesNotSupportNfc -> {
            R.string.misnapSampleAppNfcDeviceErrorMessage
        }
        is MiSnapWorkflowError.Nfc.DocumentNotNfcEnabled -> {
            R.string.misnapSampleAppNfcDocumentErrorMessage
        }
        is MiSnapWorkflowError.Nfc.InvalidCredentials -> {
            R.string.misnapSampleAppNfcCredentialsErrorMessage
        }
        is MiSnapWorkflowError.Nfc.Skipped -> {
            R.string.misnapSampleAppNfcSkippedErrorMessage
        }
        is MiSnapWorkflowError.Cancelled -> {
            R.string.misnapSampleAppCancelledErrorMessage
        }
        is MiSnapWorkflowError.CombinedWorkflow -> {
            R.string.misnapSampleAppCombinedWorkflowCancelledErrorMessage
        }
        is MiSnapWorkflowError.CombinedWorkflowSkippedStep -> {
            R.string.misnapSampleAppCombinedWorkflowSkippedStepErrorMessage
        }
        is MiSnapWorkflowError.License -> {
            R.string.misnapSampleAppCombinedWorkflowLicenseErrorMessage
        }
    }
