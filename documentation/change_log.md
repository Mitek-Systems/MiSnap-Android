# MiSnap SDK v5.2.1 Change Log

### **Version 5.2.0**
#### **Added**
* [Document & NFC] Support for optional data redaction in the document image, extracted data and NFC data read from the chip. Only supported for NLD Passports(BSN redaction). Please refer to [this FAQ](../README.md#how-does-the-video-recording-feature-work-when-optional-data-bsn-redaction-for-nld-passports-is-enabled) for information on how this feature works when video recording is enabled.
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