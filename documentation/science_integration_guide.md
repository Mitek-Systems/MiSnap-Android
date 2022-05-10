# MiSnap SDK v5.0.0 Core Science Integration Guide

This guide is targeted towards customers who want to integrate **MiSnap's core computational processing** without the default workflow shipped with the MiSnap SDK.

# Table of Contents
[Document, Barcode, and Face Analysis](#document-barcode-and-face-analysis)
* [Dependencies](#dependencies)
* [Start Analysis](#start-analysis)

[NFC Reading](#nfc-reading)
* [Dependencies](#dependencies-1)
* [Start Reading](#start-reading)

- - - - 

# Document, Barcode, and Face Analysis

Document, barcode, and face analysis uses `MiSnapController` in `controller` module to determine whether the provided frame is good enough or not. `controller` is built on top of core science modules `document-analysis`, `barcode-analysis`, and `face-analysis` and provides an easy opt-out option with a consistent API and additional Mitek's business logic for selection of a high quality frame.

Developers can, however, choose to integrate the science modules but will lose the high quality frame selection capabilities as science modules only return the raw Image Quality Analysis (IQA) values. This guide provides integration steps for `controller` only.

## Dependencies 

The easiest way to integrate to integrate document, face, or barcode analysis is to add the following to integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:controller:5.0.0-beta2"

    // Optional barcode analysis dependency
    implementation "com.miteksystems.misnap:barcode-analysis:5.0.0-beta2"

    // Optional document analysis dependency
    implementation "com.miteksystems.misnap:document-analysis:5.0.0-beta2"

    // Optional face analysis dependency
    implementation "com.miteksystems.misnap:face-analysis:5.0.0-beta2"

    // Optional MRZ detector dependency
    implementation "com.miteksystems.misnap:feature-detector:5.0.0-beta2"
}
```

To integrate MiSnap without having access to external maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

## Start Analysis

1. Initialize `MiSnapSettings` with appropriate `UseCase`; for example `MiSnapSettings.UseCase.CHECK_FRONT` to analyze the front side of a check or `MiSnapSettings.UseCase.FACE` to analyze a face

2. Initialize `MiSnapController` with `MiSnapSettings` initialized in Step 1

3. Register to listen for `LiveData` updates for `MiSnapSettings.feedbackResult`, `MiSnapSettings.frameResult`, and `MiSnapSettings.errorResult`. Please see in-code documentation for more details

4. Create `Frame` instance and call `MiSnapSettings.analyzeFrame` to start analysis. Analysis will be performed asynchronously and appropriate `LiveData` would get updated

Please see `examples/science/FrameFromCamera.kt` to create `Frame` instance.

Please see `examples/science/DocumentAnalysis.kt`, `examples/science/FaceAnalysis.kt`, and `examples/science/BarcodeAnalysis.kt` for full code.

- - - -

# NFC Reading

NFC sessions don't go through `MiSnapController` as it doesn't require a camera frame to work. Hence, `nfc-reader` module can directly be integrated into customer's application without `controller`

## Dependencies

The easiest way to integrate to integrate NFC reader is to integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:nfc-reader:5.0.0-beta2"
}
```

To integrate MiSnap without having access to external maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

## Start Reading

1. Initialize `MiSnapSettings` with `UseCase.NFC`

2. Initialize `NfcReader` with `MiSnapSettings` initialized in Step 1

3. Register to listen for `LiveData` updates for `NfcReader.events`, `NfcReader.completedEvent`, and `NfcReader.errorEvents`. Please see inline code documentation for more details

4. Call `NfcReader.start()` with appropriate `Mrz`; `MrzData` for Passports and ID cards (including Resident Permits), `Mrz1Line` for European Union Drivers Licenses

Please see `examples/science/NfcRead.kt` for full code.

- - - -
