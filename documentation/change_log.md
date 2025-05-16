# MiSnap SDK v5.8.0 Change Log

### **Version 5.6.1**
#### **Added**
* [NFC] Support for NLD 2024 documents.
* [Document] Support for extracting the MRZ of TD2 documents by setting the `analysis.document.advanced.docType` property to `MiSnapSettings.Analysis.Document.Advanced.DocType.TD2`.
* [Common] Support for 16KB memory page size devices.
  * _For more information please see the official announcement in the [Android Developers Portal](https://developer.android.com/guide/practices/page-sizes)._
* [Common] An option to configure the `delay of the initial hint message` to the `HintView` class.
* [Barcode] An option to display a `barcode label` to indicate the expected type of barcode to read.

#### **Modified**
* [Common] Upgraded CameraX to version 1.4.0.
* [Common] Upgraded the NDK to version r27c.
* [Common] Upgraded the Android Gradle Plugin to 8.0.2.
  * _NOTE: Please see [this question](../README.md#how-to-integrate-misnap-561-using-android-gradle-plugin-801-and-lower) for relevant information when working with `Android Gradle Plugin 8.0.1 and lower`._

#### **Fixed**
* [Workflow] An issue with Fragments that presented dialogs in which the dialog could outlive the Fragment's lifecycle and produce a crash with certain interactions.
* [Workflow] An issue with the `HintView` in which the view was initially displayed regardless not having contents.

### **Version 5.6.0**
#### **Added**
* [Common] Support for recording the session including audio, requires `RECORD_AUDIO` permission and to set the `MiSnapSettings.Camera.VideoRecord.recordAudio` property to true.
    * _Note: integrators should make sure that the `RECORD_AUDIO` permission is granted when using audio recording, audio won't be recorded if the permission is missing._
* [NFC] A configuration option to skip reading the profile picture from NFC-enabled documents. To enable this option, set the `MiSnapSettings.Nfc.skipPortraitImage` property to true.
    * _Note: when using this option the image returned in the results will be empty, it is advised to send the `NfcSession.misnapMibiData.mibiData` to the Mitek APIs for diagnosis._
* [Document] A configuration option to request a forced focus operation before acquiring the final frame, this increases the chances of acquiring a sharp and well detailed image that is suitable for human review. To enable this option, set the `MiSnapSettings.Analysis.Document.enableFocusOnFinalFrame` property to true.
    * _Note: this feature is not supported for Check sessions._
* [Document] A configuration option to prioritize the extraction of the MRZ for identity documents over the quality of the image. To enable this option, set the `MiSnapSettings.Analysis.Document.prioritizeDocumentExtractionOverImageQuality` property to true.
    * _Note: this reduces the session time bypassing image quality checks as long as the document's MRZ has been extracted and it is only supported when the `MiSnapSettings.Analysis.Document.documentExtractionRequirement` is set to `ExtractionRequirement.REQUIRED`, as such, this feature is not compatible with On-Device Classification_
* [Common] Support for displaying a cancel button in the `DocumentAnalysisFragment`, `FaceAnalysisFragment` and `BarcodeAnalysisFragment`, displaying the cancel button can be enabled by setting the `shouldShowCancelButton` property to true in the `WorkflowSettings` of the target fragment. The `cancelButtonDrawableId` property of the `WorkflowSettings` can be used to customize the cancel button drawable.
    * _Note: pressing the cancel button has the same effect as pressing the back button on the device._
* [Document] A configuration option to display an exit confirmation dialog when the user navigates back. To enable displaying the dialog, set the `MiSnapSettings.Workflow.showExitConfirmationDialog` property to true.
    * _Note: the contents of the dialog can be customized through localization for string keys: `misnapDocumentAnalysisSessionExitConfirmationDialog*`, the display of the dialog is only supported in document sessions._
* [Common] Incremental updates to Real-Time Security.
* [NFC] Support for reading the holder optional info data group of ICAO documents.

#### **Modified**
* [Common] Deprecated the `MiSnapSettings.Camera.VideoRecord.videoResolution` settings in favor of the new API `com.miteksystems.misnap.core.MiSnapSettings.Camera.VideoRecord.videoQuality`, the equivalent XML attributes of `CameraView` have been deprecated too, the new API defines `FHD(1080p)` and `HD(720p)` video quality constants rather than open sizes.
* [Common] The `SurfaceRecorder` and `VideoWriter` classes have been deprecated in favor of using the `MiSnapView/CameraView` recording capabilities.
* [Common] MiSnap now links the `OpenMP library dynamically` and the `libomp.so` library is included as part of the `core-module` to be used across the SDK.
    * _Note: see the system requirements for information on the NDK version used in MiSnap for compiling the shipped native libraries, the NDK version indicates the libomp.so version used._
* [Document] The `UserAction.MRZ_NOT_FOUND` now has a default message that can be customized through localization key `misnapWorkflowDocumentAnalysisFragmentHintViewMrzNotFoundMessage`.

#### **Fixed**
* [NFC] Removed the `com.gemalto.jp2:jp2-android` dependency and the jcenter repository from the `nfc-reader` module, this fixes a build error while satisfying the dependency in some build environments due to the jcenter repository being sunset.
* [Common] An issue with Android 15 in which the camera initialization could take longer if the camera resource was being retained by a different process at the time of going into a MiSnap session.
* [Common] A concurrency modification issue that would sometimes result in a crash during the session when the device motion detector was enabled.
* [ODC] An issue where MRZ was not enforced despite setting `MiSnapSettings.Analysis.Document.documentExtractionRequirement` to `ExtractionRequirement.REQUIRED` when document classification was enabled.


### **Version 5.5.0**
#### **Added**
* [Face] For selfie images, the device analyzes the ambient light available to the device camera and adjusts the screen if the environment is too dark.  Two levels of adjustments are automatically made: one, turning up the brightness of the device; and two, adding a white vignette to the screen to provide lighting for the face.
  * See the in code documentation for more information about this feature and the [design considerations](./customization_guide.md#design-considerations) for more information on how to use the feature with a custom UI.
* [Document] For identity documents, the MiSnap SDK now makes use of a device motion detector to prevent the user from capturing a frame while in movement, reducing the likelihood of a blurry image due to motion.
  * See the in code documentation for `motionDetectorSensitivity` in `MiSnapSettings` for more information on this feature.
* [NFC] Fixed an issue that prevented some ROU documents from scanning correctly.

#### **Modified**
* [Common] The target API level has been updated to 32. MiSnap is now compiled against API level 34.
* [Common] The CameraX version has been updated to 1.3.0.
* [Common] The Kotlin version has been updated to 1.8.10 and the Gradle version has been upgraded to 6.8.3.

#### **Fixed**
* [Common] An issue with combined workflows that prevented non document sessions combinations from working correctly when not depending on the `document-analysis` package.
* [Common] An issue with the `SuccessView` not playing the default camera shutter sound on some devices.


### **Version 5.4.1**
#### **Fixed**
* [NFC] Fixed an issue that prevented the NFC flow from completing in some Android 14 devices.
* [Document] Fixed an issue that prevented some checks from completing a session in auto mode.

### **Version 5.4.0**
#### **Added**
* [Document] On Device Document Classification(ODC), this feature enables extra processing on identity documents to classify them into different document types, which makes it possible to provide special hints during the session to place the correct document if a wrong document is detected.
  To enable this feature, set the `getEnableDocumentClassification` from `MiSnapSettings.Analysis.Document` to true and retrieve the classification results from the `classification` property of the session results.
  Please see [this](../README.md#what-is-the-advantage-of-using-the-on-device-classification-feature-over-regular-document-sessions) FAQ for more information on the advantages of using this feature and [this](../README.md#what-are-the-considerations-of-using-the-on-device-classification-feature) FAQ to learn more about the considerations of using the ODC feature.
  * _The use of the On Device Classification(ODC) feature requires a valid MiSnap license with the ODC feature enabled to work. This feature is currently in beta._
* [Document] On Device Extraction(ODE), this feature allows MiSnap to return additional extracted data from supported MRZ enabled identity documents. If the feature is licensed and `the MRZ extraction is requested` the extraction results can be recovered from the `extraction` property of the session results.
  * _The use of the On Device Extraction(ODE) feature requires a valid MiSnap license with the ODE feature enabled to work._
* [Common] An encrypted payload for sending to Mitek APIs for increased security.
* [Document] Multiple optimizations to improve the frame processing speed for faster and more responsive sessions.
* [Common] Improved the frame handling process to reduce the chances of a session resulting in blurred frames due to ongoing camera autofocus routines.

Please see the in-code documentation for more details and the full API.

#### **Fixed**
* [Document] An issue where the first hint message for a document capture would often be irrelevant to the ongoing session.
* [Common] An issue where the final frame would sometimes be propagated more than one time.

#### **Modified**
* [Common] The `UserAction` class has been moved to `com.miteksystems.misnap.core` and is now part of the `core` module.
* [Document] The `mrz` property of a MiSnap document session result is now contained in the `extraction` property of type `DocumentExtraction` which includes other ODE related properties.
  * _The `mrz` property contents and the conditions in which is returned are not affected by this change nor require the ODE feature to be licensed._
* [Document] The `extractedDataCorners` property of the `MiSnapDocumentAnalyzerResult` class is now contained in the `extraction` property of type `DocumentExtraction` which includes other ODE related properties.

### **Version 5.3.4**
#### **Fixed**
* [Common] Fixed an issue where the EXIF data was sometimes not being written to the resulting image.
### **Version 5.3.3**
#### **Added**
* [Document] The `DocumentAnalysisFragment.WorkflowSettings` now support the boolean option `shouldShowDocumentLabel` which displays on screen a `TextView` with the name of the expected document type. Additionally, the `documentLabelStringId` argument has been added to customize the document name for special use cases.

#### **Fixed**
* [NFC] Fixed an issue that prevented some SWE and ITA documents from scanning correctly.
* [Common] Fixed an issue where the `HintView` would be displayed even when there were no hints to display, making the use of padding in the view difficult.

### **Version 5.3.2**
#### **Added**
* [Document & NFC] Support for optional data redaction in the document image, extracted data and NFC data read from the chip. Only supported for NLD IDs(BSN redaction). Please refer to [this FAQ](../README.md#how-does-the-video-recording-feature-work-when-optional-data-bsn-redaction-for-nld-documents-is-enabled) for information on how this feature works when video recording is enabled.
  * _The on-device redaction feature of the MiSnap SDK has been added in this version to aid in compliance with Dutch data protection legislation regarding the Citizen Service Number (BSN) - see section 46 of the Dutch Implementation Act of the GDPR (“UAVG”). Mitek is making its best effort to ensure the BSN is adequately redacted so it is unreadable by human or machine.  Mitek is only redacting the BSN from still images, so the customer or integrator must ensure they are not using the video component of MiSnap if redaction is to take place._

### **Version 5.3.1**
#### **Added**
* [Common] New metadata added to the EXIF tags `0x9003` and `0x9011` to store the `UTC datetime` to represent the time when the image was acquired by the MiSnap SDK.
* [Common] Improvements to orientation changes handling.
* [NFC] Improvements to the tutorial guides covering more devices with specialized instructions on reading NFC chips.

### **Version 5.3.0**
#### **Added**
* [Common] The new `MiSnapMibiData` object has been added to describe analytics data from a MiSnap SDK session and it's included in the session results.
* [Document] Improvements to the camera focus performance.
* [Document & Analysis] Support for high resolution frames in supported devices. High resolution frames can be enabled using the `enableHighResolutionFrames` setting in the `MiSnapSettings.Camera` object.
  * _The use of high resolution frames for analysis in auto sessions is only available when manual picture capabilities are not requested and when the device supports higher resolutions. Enabling high resolution frames impacts the performance of the analysis and the disk size of the returned image._
* [Common] `DeviceInfoUtil` class has been added to provide device metadata that adds an additional layer of security without adding additional friction by enrolling a device to be bound to a biometric in Mitek server products.
  * _When this API is called a collection of device info used for enrollment and verification happens without a user action therefore it's the app developer’s responsibility to get user's consent and possibly allow to opt out beforehand._
* [Face] The `GuideView` in face sessions now changes colors depending on the IQA evaluations of a frame, signaling a change between "bad image" and "good image".
* [NFC] Chip authentication support for Italian documents.

#### **Modified**
* [Document] The default `jpegQuality` has been increased to 90 for identity documents session types.
* [Common] A minimum `jpegQuality` of 50 has been established for all session types.
* [Camera] Updated the `CameraX` dependency to version `1.1.0`.
* [Common] The `mibiData` property has been replaced with `misnapMibiData` in the result objects returned by the MiSnap SDK.
  * Please see the [migration guide](./migration_guide.md) for more information on the updated APIs.
  
### **Version 5.2.1**
#### **Fixed**
* [NFC] An issue that prevented some ITA documents from completing NFC scanning.

### **Version 5.2.0**
#### **Added**
* [Document & NFC] Support for optional data redaction in the document image, extracted data and NFC data read from the chip. Only supported for NLD Passports(BSN redaction). Please refer to [this FAQ](../README.md#how-does-the-video-recording-feature-work-when-optional-data-bsn-redaction-for-nld-documents-is-enabled) for information on how this feature works when video recording is enabled.
    * _The on-device redaction feature of the MiSnap SDK has been added in version 5.2 to aid in compliance with Dutch data protection legislation regarding the Citizen Service Number (BSN) - see section 46 of the Dutch Implementation Act of the GDPR (“UAVG”). Mitek is making its best effort to ensure the BSN is adequately redacted so it is unreadable by human or machine.  Mitek is only redacting the BSN from still images, so the customer or integrator must ensure they are not using the video component of MiSnap if redaction is to take place._
* [Document] Support for an "enhanced manual" session mode where hints are displayed on screen to assist on getting better results in manual mode sessions.
* [Common] A new configuration option to allow skipping the "manual review screen".

#### **Fixed**
* [NFC] An issue where the NFC sessions were not ending when some drawables were customized.

### **Version 5.1.1**
#### **Fixed**
* [Common] Images failing to process in manual mode sessions for some devices.
* [Document & NFC] Some ITA ID documents failing to process the MRZ and not triggering NFC reading in combined workflows.

### **Version 5.1.0**
#### **Added**
* [Voice] Voice biometrics have been introduced using the new `UseCase.VOICE`.  Integrators can now record a spoken phrase, perform pre-qualifying audio quality acceptance, and create a unique voice print to associate with a user. This voice print can be passed on to the `MiPass` product for multi-modality enrollment and verification.

#### **Fixed**
* [Common] SDK defined XML attributes conflicting with integrator's attributes.
* [Analysis] Sequential sessions with different camera configurations not updating properly.

#### **Modified**
* [NFC] Renamed `com.miteksystems.misnap.nfc.NfcReader` to `com.miteksystems.misnap.nfc.MiSnapNfcReader`.
* [Common] Instances of `com.miteksystems.misnap.controller.MiSnapController` must now be created with the `MiSnapController.create` factory method.
* [Common] Server transaction utilities at `com.miteksystems.misnap.core.serverconnection` were updated to work with concrete implementations of `com.miteksystems.misnap.core.serverconnection.MiSnapTransactionResult`.

### **Version 5.0.0**

This version is a **complete redesign** of the MiSnap SDK and is **NOT backwards compatible**.

This release focuses on providing a **uniform API** throughout the different parts of the SDK, **ease of integration**, and **true opt-in behavior** for optional components. 

Additionally, the MiSnap SDK for Android is now available as a **maven/gradle dependency** and requires a valid license to function. Please follow the [FAQs](../README.md#frequently-asked-questions-faqs) for more details and see the in-code documentation for the API changes.

Starting with v5.0.0, the MiSnap SDK does not provide any module as source.

#### **Added**
* [Common] Support to record a video of the session.
* [Common] Support to seamlessly start multiple sessions.
* [Common] Support to query the camera capabilities before starting a session.
* [Common] Support for 1080p selfie images.
* [Common]Support to show a manual review screen to users.
* [Common] Dutch (nl) string translations.
* [Common] Support for Jetpack Navigation to create custom flows.
* [Common] Android Views for creating custom screens.
* [Document] Support for generic documents analysis sessions.
* [Document] Support to extract BSN (burgerservicenummer) from newer Netherlands passport templates.
* [Barcode] x86_64 support for the barcode scanning use case.
* [Barcode] Support for mandatory barcode extraction in addition to a good quality image.

#### **Modified**
* [Common] Changed the minimum API level to 23 (Android Marshmallow).
* [Common] Dropped the use of deprecated camera APIs in favor of CameraX.
* [Common] Reduced the memory footprint of the SDK by roughly 15%.
* [Common] MiSnap SDK now runs in the same process as the host app.
* [Common] Improved the organization and discoverability of the SDK configuration options.
* [Common] Improved the document orientation handling.
* [Common] Improved the SDK theming (including support for light and dark themes).
* [Common] Improved messaging when accessibility services are enabled.
* [Document] Various non-ID documents were folded into the generic document use case.
* [Face & NFC] Reduced the SDK footprint for face and NFC use cases.

#### **Removed**
* [Common] MiSnapWorkflow UX1 and UX2 in favor of a new, unified style.
* [Document] Support for credit cards.