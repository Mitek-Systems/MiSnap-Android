# MiSnap SDK v5.0.0 Views Integration Guide

This guide is targeted towards customers who want to **create their own UI/UX** while re-using some of the components shipped within MiSnap SDK. These components (views) can be used to create UI for fragments or activities without having to re-write a major chunk of `workflow` shipped out of the box with MiSnap SDK.

# Table of Contents
[MiSnapView](#misnapview)
* [Dependencies](#dependencies)
* [Start Session](#start-misnap-session)

[CameraView](#cameraview)
* [Dependencies](#dependencies-1)
* [Start Preview](#start-camera-preview)

[Other Views](#other-views)
* [Dependencies](#dependencies-2)

[Customizations](#customizations)
* [Design Considerations](#design-considerations)

- - - -

# MiSnapView

Part of `workflow` module, `MiSnapView` combines the functionality of `CameraView` and `MiSnapController` in a easy-to-use Android `View` that can be added to the view heirarchy of the application and eliminates the need for `MiSnapController` hook-up boiler-plate code.

## Dependencies

The easiest way to integrate `MiSnapView` is to add the following to integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:workflow:5.0.0-beta2"
    implementation "com.miteksystems.misnap:controller:5.0.0-beta2"
    implementation "com.miteksystems.misnap:camera:5.0.0-beta2"

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

## Start MiSnap Session

1. Initialize `MiSnapSettings`

2. Initialize (or find in current view hierarchy) the `MiSnapView` and register to necessary `LiveData` events like `feedbackResult`, `finalFrame`, `controllerErrors`

3. Start MiSnap session by calling `MiSnapView.startMiSnapSession(...)`

Please see `examples/misnapview/MiSnapViewXml.kt` for full code.

Please see in-code documentation for the full API.

- - - -

# CameraView

Part of `camera` module, `CameraView` allows developers to add camera preview directly into the view heirarchy of the application.

## Dependencies

The easiest way to integrate `CameraView` is to add the following to integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:camera:5.0.0-beta2"
}
```

To integrate MiSnap without having access to external maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

## Start Camera Preview

1. Initialize `MiSnapSettings` and set required camera settings or get camera settings from existing `MiSnapSettings` instance

2. Initialize (or find in current view heirarchy) the `CameraView` and register to necessary `LiveData` events like `previewFrames`, `pictureFrames`, `cameraEvents`

3. Start camera preview by calling `CameraView.startCamera(...)`

Please see `examples/camera/CameraViewXml.kt` and `examples/camera/CameraViewCode.kt` for full code.

Please see in-code documentation for the full API.

- - - -

# Other Views

MiSnap provides a few easy-to-use, custom Android Views that can help developers create their own UX by re-using the same components that we use in `workflow`. These Views help eliminate a lot of boilerplate code that would otherwise be needed for customers creating their own UX.

Please see in-code documentation for full list of custom Android views provided in MiSnap SDK and their respective APIs.

Please see `/examples/views/AnalysisFragment.kt` for full integration code. 

## Dependencies

The easiest way to integrate these views is to add the following to integrating module's `build.gradle`:
```groovy
dependencies {
    implementation "com.miteksystems.misnap:workflow:5.0.0-beta2"
}
```

To integrate MiSnap without having access to external maven server, please see [this FAQ](../README.md#how-to-integrate-misnap-sdk-without-having-access-to-maven).

- - - -

# Customizations

All views in `workflow` extend from either material design components or `AppCompat` components. All customizations supported by material design and `AppCompat` are supported by the views.

Please see in-code documentation for full API and behaviors that can be changed.

## Design Considerations

Following are some things to look out for:

* For an optimal document session, it is recommended to align the long side of the document with the long side of the device.

* For an optimal document session, when using `GuideView`, ensure that the drawable is:
  * the same aspect ratio as the document.
  * large enough to fit the document but not too large that it is closer to the edges of the device.

* When enabling vignette effect in `GuideView`, ensure the drawable has some vignette if the drawable is not rectangle. Please see `workflow/drawable/misnap_guide_face.xml`.

* Keeping hint messages concise will ensure the messages are easier to understand.

* Keeping hint duration too short (might not allow users to read the hints and act on them) or too long (the hint shown may not even be relevant) can negatively impact user's experience.

* Using `CountdownTimerView`, especially for face sessions, can result in high quality, non-blurry images as it provides visual feedback to users when SDK is ready to complete the session.

* `MiSnapView` and `CameraView` don't request for camera permission. Ensure the application holds permission to use camera before using these views

* Keeping session timeout duration too short might result in multiple fail-overs and users might opt to complete manually, thereby resulting in non-optimal image being returned.

* Synchronizing the trigger delay for face analysis and duration for `CountdownTimerView` yields a better experience.

- - - -
