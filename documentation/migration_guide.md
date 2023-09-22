# MiSnap SDK v5.3.4 Migration Guide

## Upgrading the MiSnap SDK from v5.2.1 to v5.3.0
### API Changes
* The `startMiSnapSession` method of the `MiSnapView` now optionally takes the boolean parameter `requireTakePictureCapability` which starts the camera with or without the capability to take manual pictures.
  * When the option is not specified, the `MiSnapView` will infer if the capability is needed according to the trigger mode specified in the `MiSnapSettings` object. If the `DocumentAnalysisFragment` is used, the `manualButtonVisible` option from the `WorkflowSettings` will be also considered to determine whether to request the capability or not.
  * When working directly with the `MiSnapView`, it is necessary to determine if the capability should be requested or not. E.g. if the session has been configured to start in auto mode but the custom UI includes a shutter button to call for a manual picture, it is necessary to specify that the manual picture capability should be enabled, otherwise the action to take a picture won't work.
* The `startCamera` method of the `CameraView` now optionally takes the boolean parameter `requireTakePictureCapability` which defaults to true. If set to false, the camera will start without the take picture capability.
* The `mibiData` property of the `com.miteksystems.misnap.controller.MiSnapController.FrameResult` class types has been changed to `misnapMibiData`.
* The `mibiData` property of the `com.miteksystems.misnap.nfc.MiSnapNfcReader.Result` class has been changed to `misnapMibiData`.
* The `mibiData` property of the `com.miteksystems.misnap.workflow.MiSnapErrorResult` class has been changed to `misnapMibiData`. 
* The `mibiData` property of the `com.miteksystems.misnap.workflow.MiSnapFinalResult` class types has been changed to `misnapMibiData`.  
* The `com.miteksystems.misnap.voice.AudioUtil` class functions that received a `mibiData` string now require a `misnapMibiData` object.

## Upgrading the MiSnap SDK from v5.0.0 to v5.1.0

### API Changes
* The `com.miteksystems.misnap.nfc.NfcReader` was renamed to `com.miteksystems.misnap.nfc.MiSnapNfcReader`. 
* The `com.miteksystems.misnap.controller.MiSnapController` constructor has been removed and now the instances of this class must be created through the factory method `MiSnapController.create`.
* The server transaction utilities `com.miteksystems.misnap.core.serverconnection.FaceComparisonV3Request`, `com.miteksystems.misnap.examples.serverconnection.MobileVerifyV2Request` and `com.miteksystems.misnap.examples.serverconnection.MobileVerifyV3Request` APIs were updated to work with concrete implementations of `com.miteksystems.misnap.core.serverconnection.MiSnapTransactionResult`.

## Upgrading the MiSnap SDK from v4.x to v5.0.0
This new version of the MiSnap SDK introduces a variety of improvements over past versions, and it comes with a long list of breaking changes. For this reason, this is not a step-by-step guide on how to migrate your existing integration, but a collection of important differences to consider while upgrading.

If you prefer to start fresh with a MiSnap SDK v5.0.0 integration instead, please follow the guide that best suits your needs from the [integration guides](activity_integration_guide.md) section.

### Project Configuration
The project configuration and requirements have changed. Please look into the [system requirements](../README.md#system-requirements) section for more details.

### Module Changes
Most of the APIs in the existing modules were refactored into improved APIs. Some of the existing modules were removed, shifted around or merged with others. The most relevant changes are:
* The `cardio` and `creditcardcontroller` modules were removed.
* The `api`, `imageutils`, and `mibidata` modules were restructured and unified into the `core` module.
* The `barcodecontroller`, `misnapcontroller`, `misnapextractioncontroller`, and `misnaphybridcontroller` modules were unified into the `controller` module.
* All the science modules are now optional and integrators can take only the ones that are appropriate for their needs.

Please look into the `Dependencies` section of your preferred [integration guide](activity_integration_guide.md) for more information on the available modules and the in-code documentation for details on the APIs.

### Sourceless Integration
The MiSnap SDK doesn't expose any module as source code for customizations and no longer supports source code integration. All modules must be integrated through Maven or as Android Archive(.aar) files.

Please look into the `Dependencies` section of your preferred [integration guide](activity_integration_guide.md) for more information on the available modules and how to integrate them into your project.

### I/O API Changes
This version of the MiSnap SDK introduces changes the way a session is started and how the results are retrieved. The most relevant changes are:
* Previous API classes such as `MiSnapApi` and `ScienceApi` and parameter manager classes such as `ScienceParamMgr` and `BarcodeParamMgr` were all replaced with `MiSnapSettings`, the latter being the single source of truth for specifying a session configuration.
* The `Activity` classes in `misnapworkflow` and `misnapworkflow_UX2` modules were replaced by the `MiSnapWorkflowActivity` in `workflow`'s module.
* The session results are no longer in the results intent, the session results can be retrieved from the `MiSnapWorkflowActivity.Result` singleton instead.

Please look into the [integration guides](activity_integration_guide.md) for more details and examples on the various ways of starting a session.

### MiSnap SDK Customizations
The MiSnap SDK introduces an improved API to enable integrators to customize the UI and the workflow to their needs. The customization through the modification of source files in the MiSnap SDK modules is deprecated.

There are several ways of customizing the SDK workflow and UI depending on the integration type.

Please see the [customization guide](./customization_guide.md) for information on how to customize the SDK.
