# MiSnap SDK v5.0.0 Fragment Integration Guide

This guide is targeted towards developers who want to integrate MiSnap in **single-activity application architecture**. Please see in-code documentation for full list of fragments available in MiSnap SDK.

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

The easiest way to integrate MiSnap SDK is to add the following to integrating module's `build.gradle`:
```groovy
dependencies {
    // Use this for check + id sessions
    implementation "com.miteksystems.misnap:document:5.0.0-beta2"

    // Use this for barcode sessions
    implementation "com.miteksystems.misnap:barcode:5.0.0-beta2"

    // Use this for selfie sessions
    implementation "com.miteksystems.misnap:face:5.0.0-beta2"

    // Use this for automatically getting the nfc credentials and then reading the chip
    implementation "com.miteksystems.misnap:combined-nfc:5.0.0-beta2"

    // Use this for only reading the nfc chip
    implementation "com.miteksystems.misnap:nfc:5.0.0-beta2"
}
```

To integrate MiSnap without having access to external maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

- - - -

# Integration using Navigation Graphs

## Overview

MiSnap provides out-of-the-box support for `Jetpack Navigation`. `workflow`module comes with the following navigation graphs:

* **barcode_session_flow** - has tutorial and barcode analysis screens
* **document_session_flow** - has tutorial, document analysis, failover, and review screens
* **face_session_flow** - has tutorial, face analysis, failover, and review screens
* **nfc_reader_flow** - has NFC selection, manual entry/review, and NFC reader screens

## Integration:

1. Include the existing navigation graphs

2. Create `MiSnapSettings` and apply them to `MiSnapWorkflowViewModel`

3. Register to observe `results` and `errors` from `MiSnapWorkflowViewModel`

4. Call the start destination of the included navigation graph

Please see `examples/fragment/AnalysisFragmentNavigation.kt` for full code.

Please see in-code documentation for full API.

## Customizations

### Theme and Colors

`workflow` defines `MiSnapTheme` which extends the `Material` theme (required to properly function). Developers can either colors to match the app's branding (recommended) or override `MiSnapTheme`. 

Please see `res/example_theme_customizations.xml` for sample code.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

### Resources

Developers can change the built-in resources (drawables, animations, etc.) in the following ways by:
1. overriding the existing resources.
2. passing resource ids to `MiSnapSettings.workflow`.
3. passing resource ids as arguments to `NavigationAction`.

Please see in-code documentation of fragment's respective `WorkflowSettings` for full list of allowed UI and behavior changes.

### Other settings

`MiSnapSettings.workflow` provides other settings to override existing behaviors. Please see in-code documentation for full API.

Please see `examples/settings/WorkflowSettings.kt` for sample code.

### Navigation Graphs

Developers can create their own custom navigation graphs with the fragments listed above and include them in their navigation graphs.

- - - -

# Integration using Fragment Transactions

## Overview
Although it is recommended to integrate MiSnap with navigation graphs it can alternatively be integrated using a `FragmentManager` and creating `FragmentTransactions` with `workflow` standalone fragments.

Internally MiSnap uses navigation graphs to conduct the fragments navigation. When a navigation fails MiSnap produces a `NavigationError` which can be listened to drive the navigation manually.

## Integration
1. Create `MiSnapSettings` and apply them to `MiSnapWorkflowViewModel`

2. Register to observe the `navigationErrors`, `results` and `errors` from `MiSnapWorkflowViewModel`

3. Start the workflow by performing a `FragmentTransaction` with the initial fragment destination, e.g. `HelpFragment`

4. Handle the `NavigationError` events from `MiSnapWorkflowViewModel` to determine the next fragment destination and perform a new `FragmentTransaction` accordingly

Please see `examples/fragment/AnalysisFragmentTransaction.kt` for full code.

Please see in-code documentation for more details.

## Customizations

### Theme and Colors

`workflow` defines `MiSnapTheme` which extends the `Material` theme (required to properly function). Developers can either colors to match the app's branding (recommended) or override `MiSnapTheme`. 

Please see `res/example_theme_customizations.xml` for sample code.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

### Resources

Developers can change the built-in resources in the following ways by:
1. overriding the existing resources.
2. passing resource ids to fragment arguments.

Please see in-code documentation of fragment's respective `WorkflowSettings` for full list of allowed UI and behavior changes.

### Other settings

`MiSnapSettings.workflow` provides other settings to override existing behaviors. Please see in-code documentation for full API.

Please see `examples/settings/FragmentArguments.kt` for sample code.

- - - -
