# MiSnap SDK v5.11.1 Science Integration Guide

This guide is targeted towards customers who want to integrate **MiSnap's base processing** without the default workflow shipped with the MiSnap SDK.

# Table of Contents
[Introduction](#introduction)
[Checks Analysis](#checks-analysis)
* [Dependencies](#dependencies)
* [Start Image Analysis](#start-image-analysis)

[Identity Documents Analysis](#identity-documents-analysis)
* [Dependencies](#dependencies-1)
* [Start Image Analysis](#start-image-analysis-1)

[Barcode Analysis](#barcode-analysis)
* [Dependencies](#dependencies-2)
* [Start Image Analysis](#start-image-analysis-2)

[Face Analysis](#face-analysis)
* [Dependencies](#dependencies-3)
* [Start Image Analysis](#start-image-analysis-3)

[Voice Processing](#voice-processing)
* [Dependencies](#dependencies-4)
* [Starting Voice Processing](#starting-voice-processing)

[NFC Reading](#nfc-reading)
* [Dependencies](#dependencies-5)
* [Start Reading NFC](#start-reading-nfc)

[Getting Device Metadata](#getting-device-metadata)
* [Dependencies](#dependencies-6)
* [Retrieve the Device Metadata](#retrieve-the-device-metadata)
- - - -

# Introduction

Document, barcode, and face analysis use `MiSnapController` in the `controller` module to determine whether the provided frame is good enough or not. The `controller` module is built on top of the `document-analysis`, `barcode-analysis`, and `face-analysis` science modules and provides an easy opt-out option with a consistent API and additional business logic for the selection of a high quality frame.

Developers can, however, choose to integrate the science modules alone, but in doing so lose the high quality frame selection capabilities as the science modules only return the raw Image Quality Analysis (IQA) values. This guide provides the integration steps for an integration at the `controller` level.

_Warning: These integrations methods are not compatible with the `Real-Time Security` feature._

# Checks Analysis

Check analysis uses `MiSnapController` with the `document-analysis` science module.

## Dependencies

Add the following to your `build.gradle`:

```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.11.1"
    implementation "com.miteksystems.misnap:document-analysis:5.11.1"
}
```

## Start Image Analysis

1. Create a `MiSnapSettings` instance with the appropriate `MiSnapSettings.UseCase` (e.g., `MiSnapSettings.UseCase.CHECK_FRONT`) and a valid MiSnap license.
2. Create a `MiSnapController` instance with the `MiSnapSettings` from the previous step.
3. Register to listen for `LiveData` updates for `MiSnapController.feedbackResult`, `MiSnapController.frameResult`, and `MiSnapController.errorResult`.
4. Create a `Frame` instance and call `MiSnapController.analyzeFrame` to start the analysis. The analysis will be performed asynchronously and the appropriate `LiveData` will start receiving updates.

See `examples/science/CheckAnalysis.kt` for a full code sample.

# Identity Documents Analysis

Identity documents analysis uses `MiSnapController` with the `document-analysis` science module.
You may optionally add `barcode-analysis` for barcode scanning, `feature-detector` for MRZ detection and `document-classifier` for document type classification.

## Dependencies

```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.11.1"
    implementation "com.miteksystems.misnap:document-analysis:5.11.1"

    // Optional barcode scanner dependency
    implementation "com.miteksystems.misnap:barcode-analysis:5.11.1"
    
    // Optional MRZ detector dependency
    implementation "com.miteksystems.misnap:feature-detector:5.11.1"

    // Optional document classifier dependency
    implementation "com.miteksystems.misnap:document-classifier:5.11.1"
}
```

## Start Image Analysis

1. Create a `MiSnapSettings` instance with the appropriate `MiSnapSettings.UseCase` (e.g., `MiSnapSettings.UseCase.ID_FRONT`, `MiSnapSettings.UseCase.ID_BACK`, `MiSnapSettings.UseCase.PASSPORT`) and a valid MiSnap license.
2. Create a `MiSnapController` instance with the `MiSnapSettings` from the previous step.
3. Register to listen for `LiveData` updates for `MiSnapController.feedbackResult`, `MiSnapController.frameResult`, and `MiSnapController.errorResult`.
4. Create a `Frame` instance and call `MiSnapController.analyzeFrame` to start the analysis. The analysis will be performed asynchronously and the appropriate `LiveData` will start receiving updates.

See `examples/science/IdentityDocumentAnalysis.kt` for a full code sample.

# Barcode Analysis

Barcode analysis uses the `controller` module with the `barcode-analysis` science module.

## Dependencies

```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.11.1"
    implementation "com.miteksystems.misnap:barcode-analysis:5.11.1"
}
```

## Start Image Analysis

1. Create a `MiSnapSettings` instance with `MiSnapSettings.UseCase.BARCODE` and a valid MiSnap license.
2. Create a `MiSnapController` instance with the `MiSnapSettings` from the previous step.
3. Register to listen for `LiveData` updates for `MiSnapController.feedbackResult`, `MiSnapController.frameResult`, and `MiSnapController.errorResult`.
4. Create a `Frame` instance and call `MiSnapController.analyzeFrame` to start the analysis. The analysis will be performed asynchronously and the appropriate `LiveData` will start receiving updates.

See `examples/science/BarcodeAnalysis.kt` for a full code sample.

# Face Analysis

Face analysis uses the `controller` module with the `face-analysis` science module.

## Dependencies

```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.11.1"
    implementation "com.miteksystems.misnap:face-analysis:5.11.1"
}
```

## Start Image Analysis

1. Create a `MiSnapSettings` instance with `MiSnapSettings.UseCase.FACE` and a valid MiSnap license.
2. Create a `MiSnapController` instance with the `MiSnapSettings` from the previous step.
3. Register to listen for `LiveData` updates for `MiSnapController.feedbackResult`, `MiSnapController.frameResult`, and `MiSnapController.errorResult`.
4. Create a `Frame` instance and call `MiSnapController.analyzeFrame` to start the analysis. The analysis will be performed asynchronously and the appropriate `LiveData` will start receiving updates.

See `examples/science/FaceAnalysis.kt` for a full code sample.

# Voice Processing

Voice sessions don't go through the `MiSnapController` as they don't require a camera frame to work. Hence, the `voice-processor` module can directly be integrated into the customer's application without the `controller` module.

## Dependencies

The easiest way to integrate voice processing is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:voice-processor:5.11.1"
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
    implementation "com.miteksystems.misnap:nfc-reader:5.11.1"
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