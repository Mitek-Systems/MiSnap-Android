# MiSnap SDK v5.0.0 Activity-based Integration Guide

This **recommended** guide is targeted towards developers who want the **easiest integration** of MiSnap SDK and is best suited for applications with **multi-activity application architecture**.

- - - -

# Table of Contents

[Dependencies](#dependencies)

[Start Session](#start-session)

[Retrieving Results](#retrieve-results)

[Customizations](#customizations)
* [Theme and Colors](#theme-and-colors)
* [Drawables](#drawables)
* [Strings and Dimens](#strings-and-dimens)
* [Other Settings](#other-settings)
* [Custom Navigation Graphs](#custom-navigation-graphs)

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

# Start Session

1. Create `MiSnapWorkflowStep` by instantiating either `Analysis` or `Nfc`

2. Create `Activity`-launch `Intent` by calling `MiSnapWorkflowActivity.buildIntent()` by passing `MiSnapWorkflowStep`.
 Developers can pass multiple `MiSnapWorkflowStep`s to start multiple sessions

3. Launch `MiSnapWorkflowActivity`

Please see `examples/activity/IntegrationActivity.kt` for full code.

Please see in-code documentation for more details.

- - - -

# Retrieve Results

`MiSnapWorkflowActivity` returns the results in `MiSnapWorkflowActivityResult` singleton object. The results are published to this object at the end (successful or otherwise) of all of the sessions.

Results are returned in the order of `MiSnapWorkflowStep`s passed when starting `MiSnapWorkflowActivity`.

Please see `examples/activity/IntegrationActivity.kt` for full code.

- - - -

# Customizations

## Theme and Colors

`workflow` defines `MiSnapTheme` which extends the `Material` theme (required to properly function). Developers can either colors to match the app's branding (recommended) or override `MiSnapTheme`. 

Please see `res/example_theme_customizations.xml` for sample code.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

## Drawables

Developers can change the drawables in 2 ways by:
1. overriding the existing drawables.
2. passing drawable ids to `MiSnapSettings.workflow`.

## Strings and Dimens

Developers can change strings and dimen values by overriding the existing resources.

## Other Settings

`MiSnapSettings.workflow` provides other settings to override existing behaviors. Please see in-code documentation for full API.

Please see `examples/settings/WorkflowSettings.kt` for sample code.

## Custom Navigation Graphs

Developers can optionally choose to create and pass a custom navigation graph for a session's flow. This can be done by passing the optional argument to `MiSnapWorkflowStep`.

Please see [Jetpack Navigation](https://developer.android.com/guide/navigation) for more information.

- - - -
