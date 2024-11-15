# MiSnap SDK v5.6.1 Science Integration Guide

This guide is targeted towards customers who want to integrate **MiSnap's base processing** without the default workflow shipped with the MiSnap SDK.

# Table of Contents
[Document, Barcode, and Face Analysis](#document-barcode-and-face-analysis)
* [Dependencies](#dependencies)
* [Starting Image Analysis](#starting-image-analysis)

[Voice Processing](#voice-processing)
* [Dependencies](#dependencies-1)
* [Starting Voice Processing](#starting-voice-processing)

[NFC Reading](#nfc-reading)
* [Dependencies](#dependencies-2)
* [Start Reading NFC](#start-reading-nfc)

[Getting Device Metadata](#getting-device-metadata)
* [Dependencies](#dependencies-3)
* [Retrieve the Device Metadata](#retrieve-the-device-metadata)
- - - - 

# Document, Barcode, and Face Analysis

Document, barcode, and face analysis use `MiSnapController` in the `controller` module to determine whether the provided frame is good enough or not. The `controller` module is built on top of the `document-analysis`, `barcode-analysis`, and `face-analysis` science modules and provides an easy opt-out option with a consistent API and additional business logic for the selection of a high quality frame.

Developers can, however, choose to integrate the science modules alone, but in doing so lose the high quality frame selection capabilities as the science modules only return the raw Image Quality Analysis (IQA) values. This guide provides the integration steps for an integration at the `controller` level.

_Warning: This integration method is not compatible with the `Real-Time Security` feature._

## Dependencies 

The easiest way to integrate document, face, or barcode analysis is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.6.1"

    // Optional barcode analysis dependency
    implementation "com.miteksystems.misnap:barcode-analysis:5.6.1"

    // Optional document analysis dependency
    implementation "com.miteksystems.misnap:document-analysis:5.6.1"

    // Optional face analysis dependency
    implementation "com.miteksystems.misnap:face-analysis:5.6.1"

    // Optional MRZ detector dependency
    implementation "com.miteksystems.misnap:feature-detector:5.6.1"

    // Oprtional document classifier dependency
    implementation "com.miteksystems.misnap:document-classifier:5.6.1"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

## Starting Image Analysis

1. Create a `MiSnapSettings` instance with the appropriate `MiSnapSettings.UseCase` and a valid MiSnap license; for example `MiSnapSettings.UseCase.CHECK_FRONT` to analyze the front side of a check or `MiSnapSettings.UseCase.FACE` to analyze a face.

2. Create a `MiSnapController` instance with the `MiSnapSettings` from the previous step.

3. Register to listen for `LiveData` updates for `MiSnapController.feedbackResult`, `MiSnapController.frameResult`, and `MiSnapController.errorResult`.

4. Create a `Frame` instance and call `MiSnapController.analyzeFrame` to start the analysis. The analysis will be performed asynchronously and the appropriate `LiveData` will start receiving updates.

Please see `examples/science/FrameFromNativeCamera.kt` for the full code sample on how to create a `Frame` instance.

Please see `examples/science/DocumentAnalysis.kt`, `examples/science/FaceAnalysis.kt`, and `examples/science/BarcodeAnalysis.kt` for the full code sample.

- - - -

# Voice Processing

Voice sessions don't go through the `MiSnapController` as they don't require a camera frame to work. Hence, the `voice-processor` module can directly be integrated into the customer's application without the `controller` module.

## Dependencies

The easiest way to integrate voice processing is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:voice-processor:5.6.1"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

## Starting Voice Processing

1. Create a `MiSnapSettings` instance with the use case `UseCase.VOICE` and a valid MiSnap license.  Additionally, specify the `MiSnapSettings.Voice.Flow` for this session.

2. Create a `MiSnapVoiceProcessor` instance with the `MiSnapSettings` from the previous step.

3. Register to listen for `LiveData` updates for `MiSnapVoiceProcessor.events` and `MiSnapVoiceProcessor.results`.

Please see `examples/science/VoiceRecord.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.
- - - -

# NFC Reading

NFC sessions don't go through the `MiSnapController` as they don't require a camera frame to work. Hence, the `nfc-reader` module can directly be integrated into the customer's application without the `controller` module.

## Dependencies

The easiest way to integrate NFC reading is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:nfc-reader:5.6.1"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

## Start Reading NFC

1. Create a `MiSnapSettings` instance with the use case `UseCase.NFC` and a valid MiSnap license, and configure it with the appropriate `Mrz`; use `MrzData` for Passports and ID cards (including Resident Permits), use `Mrz1Line` for European Union Driver's Licenses.

2. Create a `MiSnapNfcReader` instance with the `MiSnapSettings` from the previous step.

3. Register to listen for `LiveData` updates for `MiSnapNfcReader.events`, `MiSnapNfcReader.completedEvent`, and `MiSnapNfcReader.errorEvents`.

4. Call `MiSnapNfcReader.start()` by passing an `Activity` and the `MiSnapSettings` from step 1. The appropriate `LiveData`s will start receiving updates during the reading process. 


Please see `examples/science/NfcRead.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -

# Getting Device Metadata
The MiSnap SDK provides the `DeviceInfoUtil` class to retrieve device metadata that adds an additional layer of security without adding additional friction by enrolling a device to be bound to a biometric in Mitek server products.

## Dependencies
The `DeviceInfoUtil` class is part of the `core` module of the MiSnap SDK, the `core` module of MiSnap is fundamental for the MiSnap SDK to work and as such it is available on all integration types.

## Retrieve the Device Metadata

1. Call the `DeviceInfoUtil.getDeviceInfo()` function and provide a valid MiSnap license.
2. Determine if the result is `DeviceInfoResult.Success` in which case the `deviceInfo` property will contain the device metadata, otherwise, handle the `DeviceInfoResult.Failure` accordingly.

Please see `examples/science/DeviceInfo.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

Please see this [FAQ](../README.md#what-device-info-is-collected-when-using-deviceinfoutil) for more information on how the device metadata collection works.
- - - -