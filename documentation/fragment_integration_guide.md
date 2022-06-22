# MiSnap SDK v5.0.0 Fragment Integration Guide

This guide is targeted towards developers who want to integrate the MiSnap SDK in **single-activity architecture** applications. Please see the in-code documentation for the full list of `Fragment`s available in the MiSnap SDK.

# Table of Contents

[Dependencies](#dependencies)

[Integration using Navigation Graphs (recommended)](#integration-using-navigation-graphs) 
  * [Overview](#overview)
  * [Integration](#integration)
  * [Customizations](#customizations)

[Integration using Fragment Transaction](#integration-using-fragment-transactions)
  * [Overview](#overview-1)
  * [Integration](#integration-1)
  * [Customizations](#customizations-1)

- - - -

# Dependencies

The easiest way to integrate the MiSnap SDK is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    // Use this for check + id sessions
    implementation "com.miteksystems.misnap:document:5.0.0-beta3"

    // Use this for barcode sessions
    implementation "com.miteksystems.misnap:barcode:5.0.0-beta3"

    // Use this for selfie sessions
    implementation "com.miteksystems.misnap:face:5.0.0-beta3"

    // Use this for automatically getting the nfc credentials and then reading the chip
    implementation "com.miteksystems.misnap:combined-nfc:5.0.0-beta3"

    // Use this for only reading the nfc chip
    implementation "com.miteksystems.misnap:nfc:5.0.0-beta3"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

- - - -

# Integration using Navigation Graphs

## Overview

The MiSnap SDK provides out-of-the-box support for `Jetpack Navigation`. The `workflow` module comes with the following navigation graphs:

* **barcode_session_flow** - includes the tutorial and barcode analysis screens.
* **document_session_flow** - includes the tutorial, document analysis, failover, and review screens.
* **face_session_flow** - includes the tutorial, face analysis, failover, and review screens.
* **nfc_reader_flow** - includes the NFC selection, manual entry/review, and NFC reader screens.

## Integration

1. Include the existing navigation graphs.

2. Create a `MiSnapSettings` instance and apply it to the `MiSnapWorkflowViewModel`.

3. Register to observe `LiveData` updates from `results` and `errors` from the `MiSnapWorkflowViewModel`.

4. Call the start destination of the included navigation graph.

Please see `examples/fragment/AnalysisFragmentNavigation.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

## Customizations

### Theme and Colors

The `workflow` module defines `MiSnapTheme` which extends the `Material` theme (required to properly function). Developers can either define colors to match the app's branding (recommended) or override `MiSnapTheme`. 

Please see `res/example_theme_customizations.xml` for the full code sample.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

### Resources

Developers can change the built-in resources (drawables, animations, etc.) in the following ways by:
1. Overriding the existing resources.
2. Passing resource IDs to `MiSnapSettings.Workflow`.
3. Passing resource IDs as arguments to a `NavigationAction`.

Please refer to each `Fragment`'s `WorkflowSettings` in-code documentation for a full list of the allowed UI and behavior changes.

### Other settings

`MiSnapSettings.Workflow` provides other settings to override existing behaviors.

Please see `examples/settings/WorkflowSettings.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

### Navigation Graphs

Developers can create their own custom navigation graphs with the available `Fragment`s in the MiSnap SDK. Please see the in-code documentation for the full list of `Fragment`s available in the MiSnap SDK.

- - - -

# Integration using Fragment Transactions

## Overview
Although it is recommended to integrate the MiSnap SDK with `Jetpack Navigation`, it can alternatively be integrated using a `FragmentManager` and creating `FragmentTransactions` with `workflow` module's standalone fragments.

Internally, the MiSnap SDK uses navigation graphs to conduct the fragments navigation. When a navigation fails, the MiSnap SDK produces a `NavigationError` event which can be observed to drive the navigation manually.

## Integration
1. Create a `MiSnapSettings` instance and apply it to the `MiSnapWorkflowViewModel`.

2. Register to observe `LiveData` updates from `navigationErrors`, `results`, and `errors` from the `MiSnapWorkflowViewModel`.

3. Start the workflow by performing a `FragmentTransaction` with the initial fragment destination, e.g. `HelpFragment`.

4. Handle the `NavigationError` events from the `MiSnapWorkflowViewModel` to determine the next fragment destination and perform a new `FragmentTransaction` accordingly.

Please see `examples/fragment/AnalysisFragmentTransaction.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

## Customizations

### Theme and Colors

The `workflow` module defines `MiSnapTheme` which extends the `Material` theme (required to properly function). Developers can either define colors to match the app's branding (recommended) or override `MiSnapTheme`. 

Please see `res/example_theme_customizations.xml` for the full code sample.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

### Resources

Developers can change the built-in resources in the following ways by:
1. Overriding the existing resources.
2. Passing resource IDs to fragment arguments.

Please refer to each `Fragment`'s `WorkflowSettings` in-code documentation for a full list of the allowed UI and behavior changes.

### Other settings

`MiSnapSettings.Workflow` provides other settings to override existing behaviors.

Please see `examples/settings/FragmentArguments.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -
