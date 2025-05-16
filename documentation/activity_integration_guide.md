# MiSnap SDK v5.8.0 Activity-based Integration Guide

This **recommended** guide is targeted towards developers who want the **easiest integration** of the MiSnap SDK and is best suited for applications with a **multi-activity architecture**.

Please see the [customization guide](./customization_guide.md) for information on how to customize the SDK.

- - - -

# Table of Contents

[Dependencies](#dependencies)

[Starting a Session](#starting-a-session)

[Retrieving Results](#retrieving-results)

- - - -

# Dependencies

The easiest way to integrate the MiSnap SDK is to add the following to the integrating module's `build.gradle`:

```groovy
dependencies {
    // Use this for check + id sessions
    implementation "com.miteksystems.misnap:document:5.8.0"

    // Use this for document classification
    implementation "com.miteksystems.misnap:classifier:5.8.0"

    // Use this for barcode sessions
    implementation "com.miteksystems.misnap:barcode:5.8.0"

    // Use this for selfie + voice sessions
    implementation "com.miteksystems.misnap:biometric:5.8.0"

    // Use this for selfie sessions
    implementation "com.miteksystems.misnap:face:5.8.0"

    // Use this for voice sessions
    implementation "com.miteksystems.misnap:voice:5.8.0"

    // Use this for automatically getting the nfc credentials and then reading the chip
    implementation "com.miteksystems.misnap:combined-nfc:5.8.0"

    // Use this for only reading the nfc chip
    implementation "com.miteksystems.misnap:nfc:5.8.0"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

- - - -

# Starting a Session

1. Instantiate a `MiSnapWorkflowStep` with `MiSnapSettings` containing a valid MiSnap license.

2. Create an `Activity`-launch `Intent` by calling `MiSnapWorkflowActivity.buildIntent()` and passing the created `MiSnapWorkflowStep` from the previous step.  
Developers can pass multiple `MiSnapWorkflowStep`s to start multiple sessions.

3. Launch the `MiSnapWorkflowActivity` with the created `Intent` from the previous step.

Please see `examples/activity/IntegrationActivity.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -

# Retrieving Results

`MiSnapWorkflowActivity` stores the results in the `MiSnapWorkflowActivity.Result` singleton object. 
The results are published to this object at the end (successful or otherwise) of all of the sessions or up to the last executed session if the activity is finished abruptly.

The results are returned in the order in which the `MiSnapWorkflowStep`s were passed when creating the `Activity`-launch `Intent`.

Please see `examples/activity/IntegrationActivity.kt` for the full code example.

- - - -
