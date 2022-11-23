# MiSnap SDK v5.1.1 Migration Guide

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

#### Workflow Customizations
The workflow and its behavior can be customized in several ways, depending on the integration type.

For simple customizations that make use of the `Fragment`s included in the MiSnap SDK, please follow the samples in the [integration guides](activity_integration_guide.md) and the in-code documentation for `MiSnapSettings`.

For advanced customizations, please follow the [views integration guide](views_integration_guide.md) to build your own `Fragment`s and the [fragment-based integration guide](fragment_integration_guide.md) to use them.

#### UI Customizations
The MiSnap SDK offers several ways of customizing the UI depending on your integration type. Please look into the `Customizations` section of your preferred [integration guide](activity_integration_guide.md) for more details.
