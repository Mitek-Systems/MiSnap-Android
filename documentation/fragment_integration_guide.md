# MiSnap SDK v5.3.4 Fragment Integration Guide

This guide is targeted towards developers who want to integrate the MiSnap SDK in **single-activity architecture** applications. Please see the in-code documentation for the full list of `Fragment`s available in the MiSnap SDK.

Please see the [customization guide](./customization_guide.md) for information on how to customize the SDK.

# Table of Contents

[Dependencies](#dependencies)

[Integration using Navigation Graphs (recommended)](#integration-using-navigation-graphs) 
  * [Overview](#overview)
  * [Integration](#integration)

[Integration using Fragment Transaction](#integration-using-fragment-transactions)
  * [Overview](#overview-1)
  * [Integration](#integration-1)

- - - -

# Dependencies

The easiest way to integrate the MiSnap SDK is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    // Use this for check + id sessions
    implementation "com.miteksystems.misnap:document:5.3.4"

    // Use this for barcode sessions
    implementation "com.miteksystems.misnap:barcode:5.3.4"

    // Use this for selfie + voice sessions
    implementation "com.miteksystems.misnap:biometric:5.3.4"

    // Use this for selfie sessions
    implementation "com.miteksystems.misnap:face:5.3.4"

    // Use this for voice sessions
    implementation "com.miteksystems.misnap:voice:5.3.4"

    // Use this for automatically getting the nfc credentials and then reading the chip
    implementation "com.miteksystems.misnap:combined-nfc:5.3.4"

    // Use this for only reading the nfc chip
    implementation "com.miteksystems.misnap:nfc:5.3.4"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

- - - -

# Integration using Navigation Graphs

## Overview

The MiSnap SDK provides out-of-the-box support for `Jetpack Navigation`. The `workflow` module comes with the following navigation graphs:

* **barcode_session_flow** - includes the help and barcode analysis screens.
* **document_session_flow** - includes the help, document analysis, failover, and review screens.
* **face_session_flow** - includes the help, face analysis, failover, and review screens.
* **nfc_reader_flow** - includes the NFC selection, manual entry/review, and NFC reader screens.
* **voice_session_flow** - includes the help, password phrase selection and voice analysis screens.

## Integration

1. Include the existing navigation graphs.

2. Create a `MiSnapSettings` instance containing a valid MiSnap license and apply it to the `MiSnapWorkflowViewModel`.

3. Register to observe `LiveData` updates from `results` and `errors` from the `MiSnapWorkflowViewModel`.

4. Call the start destination of the included navigation graph.

Please see `examples/fragment/AnalysisFragmentNavigation.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -

# Integration using Fragment Transactions

## Overview
Although it is recommended to integrate the MiSnap SDK with `Jetpack Navigation`, it can alternatively be integrated using a `FragmentManager` and creating `FragmentTransactions` with `workflow` module's standalone fragments.

Internally, the MiSnap SDK uses navigation graphs to conduct the fragments navigation. When a navigation fails, the MiSnap SDK produces a `NavigationError` event which can be observed to drive the navigation manually.

## Integration
1. Create a `MiSnapSettings` instance containing a valid MiSnap license and apply it to the `MiSnapWorkflowViewModel`.

2. Register to observe `LiveData` updates from `navigationErrors`, `results`, and `errors` from the `MiSnapWorkflowViewModel`.

3. Start the workflow by performing a `FragmentTransaction` with the initial fragment destination, e.g. `HelpFragment`.

4. Handle the `NavigationError` events from the `MiSnapWorkflowViewModel` to determine the next fragment destination and perform a new `FragmentTransaction` accordingly.

Please see `examples/fragment/AnalysisFragmentTransaction.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -
