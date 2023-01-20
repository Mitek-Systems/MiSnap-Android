# MiSnap SDK v5.2 Change Log

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