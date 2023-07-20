# MiSnap SDK v5.3.3 Views Integration Guide

This guide is targeted towards customers who want to **create their own UI/UX** while re-using some of the components shipped within the MiSnap SDK. These components (Views) can be used to create the UI for fragments or activities without having to re-write a major portion of the `workflow` module shipped out-of-the-box with the MiSnap SDK.

Please see the [customization guide](./customization_guide.md) for information on how to customize the SDK.

# Table of Contents
[MiSnapView](#misnapview)
* [Dependencies](#dependencies)
* [Starting a Session](#starting-a-session)

[CameraView](#cameraview)
* [Dependencies](#dependencies-1)
* [Starting the Camera Preview](#starting-the-camera-preview)

[Other Views](#other-views)
* [Dependencies](#dependencies-2)

- - - -

# MiSnapView

Part of the `workflow` module, `MiSnapView` combines the functionality of the `CameraView` and the `MiSnapController` in a easy-to-use Android `View` that can be added to the view hierarchy of an application and eliminates the boiler-plate code needed to hook-up the `MiSnapController`.

## Dependencies

The easiest way to integrate the `MiSnapView` is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:workflow:5.3.3"
    implementation "com.miteksystems.misnap:controller:5.3.3"
    implementation "com.miteksystems.misnap:camera:5.3.3"

    // Optional barcode analysis dependency
    implementation "com.miteksystems.misnap:barcode-analysis:5.3.3"

    // Optional document analysis dependency
    implementation "com.miteksystems.misnap:document-analysis:5.3.3"

    // Optional face analysis dependency
    implementation "com.miteksystems.misnap:face-analysis:5.3.3"

    // Optional MRZ detector dependency
    implementation "com.miteksystems.misnap:feature-detector:5.3.3"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

## Starting a Session

1. Create a `MiSnapSettings` instance with the appropriate `MiSnapSettings.UseCase` and a valid MiSnap license.

2. Create (or find in the current view hierarchy) the `MiSnapView` and register for `LiveData` updates from events like `feedbackResult`, `finalFrame`, and `controllerErrors`. 

3. Call `MiSnapView.startMiSnapSession(...)` to start a session by passing the `MiSnapSettings` from step 1. Make sure to provide the `requireTakePictureCapability` option to ensure that the take picture capability is enabled, if not provided, the `MiSnapView` will infer if it's needed based on the settings provided.

Please see `examples/misnapview/MiSnapViewXml.kt` and `examples/misnapview/MiSnapViewCode.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -

# CameraView

Part of the `camera` module, the `CameraView` allows developers to add a camera preview directly into the view hierarchy of the application.

## Dependencies

The easiest way to integrate the `CameraView` is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:camera:5.3.3"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

## Starting the Camera Preview

1. Create a `MiSnapSettings` instance with the appropriate `MiSnapSettings.UseCase` and a valid MiSnap license, and set the required camera configurations in `MiSnapSettings.Camera`.

2. Create (or find in the current view hierarchy) the `CameraView` and register for `LiveData` updates from events like `previewFrames`, `pictureFrames`, and `cameraEvents`.

3. Call `CameraView.startCamera(...)` to start the camera preview by passing the `MiSnapSettings.Camera` from step 1.

Please see `examples/camera/CameraViewXml.kt` and `examples/camera/CameraViewCode.kt` for the full code sample.

Please see the in-code documentation for more details and the full API.

- - - -

# Other Views

The MiSnap SDK provides a few easy-to-use, custom Android `View`s that can help developers to create their own UI/UX by re-using the same components used in the `workflow` module. These Views help to eliminate a lot of the boiler-plate code that would otherwise be needed for customers creating their own UI/UX.

Please see the in-code documentation for the full list of custom Android `View`s provided in the MiSnap SDK and their respective APIs.

Please see `/examples/views/AnalysisFragment.kt` for the full code sample.

## Dependencies

The easiest way to integrate these views is to add the following to the integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:workflow:5.3.3"
}
```

To integrate the MiSnap SDK without having access to an external Maven server, please see [this FAQ](../README.md#how-to-integrate-the-misnap-sdk-without-having-access-to-a-remote-maven-repository).

- - - -
