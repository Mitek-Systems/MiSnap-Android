# MiSnap SDK v5.3.4 Customization Guide
While many of the customization options mentioned in this guide may be applicable to other MiSnap integration types, this guide focuses on customization options for the [activity-based integration](./activity_integration_guide.md).

# Table of Contents
[Customization Options](#customization-options)
* [Through MiSnapSettings](#through-misnapsettings)
* [Through Resource Overrides](#through-resource-overrides)
* [Through a Custom Implementation](#through-a-custom-implementation)

[Approaching Customization](#approaching-customization)

[General SDK Customization](#general-sdk-customization)
* [Drawables](#drawables)
* [Colors](#colors)
* [Strings](#strings)
* [Localization](#localization)
* [Themes and Styles](#themes-and-styles)
* [Other resources](#other-resources)

[Flow Screens](#flow-screens)
* [Tutorial/Help Screen](#tutorialhelp-screen)
    * [Displaying Custom Instructions](#displaying-custom-instructions)
    * [Skipping the Tutorial](#skipping-the-tutorial)
* [Analysis Screen](#analysis-screen)
    * [Customizing the GuideView](#customizing-the-guideview)
    * [Displaying the Manual Button in Auto Sessions](#displaying-the-manual-button-in-auto-sessions)
    * [Displaying a Document Label](#displaying-a-document-label)
    * [Adjusting the Session Timeout](#adjusting-the-session-timeout)
    * [Customizing the HintView](#customizing-the-hintview)
    * [Customizing the SuccessView](#customizing-the-successview)
    * [Controlling the Review Screen](#controlling-the-review-screen)
* [Failover Screen](#failover-screen)
    * [Customizing the Failover Reasons](#customizing-the-failover-reasons)
* [NFC Reader Screen](#nfc-reader-screen)
    * [Displaying Custom Scan Instructions](#displaying-custom-scan-instructions)
    * [Controlling the Skip and Failover](#controlling-the-skip-and-failover)
* [Voice Recording Screen](#voice-recording-screen)
    * [Customizing the ProgressTrackerView](#customizing-the-progresstrackerview)
    * [Adjusting the Session Timeout](#adjusting-the-session-timeout-1)
* [Custom Screens](#custom-screens)
    * [Replacing a Screen](#replacing-a-screen)
    * [Removing/Skipping a Screen](#removingskipping-a-screen)
    * [Adding a New Screen](#adding-a-new-screen)
* [Design Considerations](#design-considerations)

- - - - 

# Customization Options
## Through MiSnapSettings
The `MiSnapSettings` object is used to configure a session and allows to customize various aspects of the UI/UX for all the SDK capabilities. The `MiSnapSettings.Workflow` object allows to supply `per fragment` settings to easily customize supported elements and options, such as drawables, animations, behaviors, and more.

Please see the in-code documentation for more details on the `MiSnapSettings` object.

## Through Resource Overrides
The Android development framework allows to override defined resources, including the ones in the MiSnap SDK, which include drawables, colors, themes, strings, dimensions, etc.

## Through a Custom Implementation
While the MiSnap SDK provides an easy way to customize the most common elements and behaviors, there are situations where it is not enough. In those cases, the MiSnap SDK offers additional integration options which allow integrators to be more in control of the UI/UX.

Please see the [integration guides](../README.md#integration-guides) for more information on other integration options.

- - - -

# Approaching Customization
Identifying the best customization approach can be challenging. The following is a series of steps that can be used as reference to determine what is the best customization option when customizing the MiSnap SDK.

1. Identify the target element, resource, or behavior to customize.
2. Explore the `MiSnapSettings` in-code documentation and identify if there is an existing setting that satisfies the customization needs. 
The `MiSnapSettings.Workflow` object can be configured to use `sets of per fragment options` to customize several aspects of built-in `Fragments`.
Explore the `Workflow` module's built-in `Fragments` and look for the `WorkflowSettings` object inside each of them to learn about all the customization options applicable to that `Fragment`.
3. Identify the `Workflow` module's available resources that can be overridden.
Use the `Resource Manager` and filter the resources to include the `libraries` resources. The resources are also available for integrators of the release package under `/Workflow_Resources`.
4. When the customization needs are greater, it is possible to create and use custom `Fragments` and flows by using a `custom navgraph` resource that can be passed to the `MiSnapWorkflowStep` object to be used in place of the default.
5. Explore [other integration guides](../README.md#integration-guides), advanced integration guides provide more control and allow a wide range of customization options.

Please see the in-code documentation for more details on the available configurations of the `MiSnapSettings` object and the supported per `Fragment` settings.

- - - -

# General SDK Customization
## Drawables
Drawable assets are one of the most common types of customizations, the vast majority of the drawables included in the MiSnap SDK are `Vector Drawables`.

Some of the most common drawables in the MiSnap SDK can be customized through the `MiSnapSettings` object, for example:

```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(helpButtonDrawableId = R.drawable.my_help_drawable)
    )
}
```
Please review the in-code documentation for details on the available settings per fragment.

Please review the `examples/settings/WorkflowSettings.kt` example for a full demonstration on the use of the configurable settings.

Overriding an existing resource is another way to customize drawables. However, it is important to consider that some of the drawables included in the MiSnap SDK are used in different places and overriding one of them could have an undesired effect in other places.

To override the `help button drawable` first identify the corresponding drawable resource as explained [here](#approaching-customization), once the resource name is identified, in this case `misnap_button_help_icon`, create a `drawable` resource that matches the same name for the MiSnap SDK to use it.

## Colors
The customization of the colors used within the SDK can be achieved through resource overriding as explained [here](#approaching-customization), once the target color is identified, create a `resource entry` that matches the same name in the `values/colors.xml` and `values-night/colors.xml`, for example, to change the `GuideView` color, it is necessary to override the `colorGuideImage` resource:
```xml
<resources>
    <color name="colorGuideImage">#50C99C</color>
</resources>
```

Note that the MiSnap SDK uses some of the colors in multiple elements, and in some situations, integrators are required to customize other colors or to customize some colors through drawables instead.

## Strings
Customizing the text that is displayed within the MiSnap SDK can be achieved through resource overriding. To override a string, first identify the target resource as explained [here](#approaching-customization).

The MiSnap SDK defined string resources follow a pattern that can help to discover its usage within the SDK easier, for example, the resource `@string/misnapWorkflowDocumentAnalysisFragmentSuccessViewLabel` is used within the `Workflow` module and within the `DocumentAnalysisFragment` when the `SuccessView` is displayed.

Once the resource name is identified, create a matching string resource record in the `values/strings.xml` file, for example:

```xml
<string name="misnapWorkflowDocumentAnalysisFragmentSuccessViewLabel">Complete!</string>
```

It is important to create matching records on all the supported languages so that the custom string is displayed properly.

## Localization
The MiSnap SDK comes with support for the following languages:
* English
* Spanish
* French
* Dutch

To add support for other locales, it is necessary to identify all the MiSnap SDK used strings as explained [here](#approaching-customization) and create a new `strings.xml` file within the custom locale `values` folder, then in the newly created `strings.xml` create records for all the target strings so that the MiSnap SDK displays these properly.

## Themes and Styles
The `Workflow` module defines `MiSnapTheme` which extends the `Material` theme (required to properly function). The `MiSnapTheme` defines the common styles used across the SDK which can be extended, reused and overridden to achieve a consistent look and feel.

To create a custom theme based on the `MiSnapTheme`, define it in `styles.xml` and customize it, for example:

```xml
<style name="AppTheme" parent="@style/MiSnapTheme">
    <!-- Customize your theme here. -->
</style>
```
Once the custom theme is defined, it must be specified in the `AndroidManifest.xml`.

To override a style used in the MiSnap SDK, first identify the style resource as explained [here](#approaching-customization), then define the target style in `styles.xml` to customize it, for example:

```xml
<style name="MiSnapTheme.TextAppearance.HintView" parent="@style/TextAppearance.MaterialComponents.Headline3">
    <item name="android:fontFamily">monospace</item>
    <item name="android:textStyle">italic</item>
</style>
```

Please see `res/example_theme_customizations.xml` in the sample app for the full code sample.

Please see [Material Design Theming](https://github.com/material-components/material-components-android/tree/1.5.0/docs/theming) for more information on theme customizations.

## Other resources
Any other resource type defined by the MiSnap SDK can be customized through resource overriding. For example, to override a dimension, first identify the target resource as explained [here](#approaching-customization).

Once identified, create a resource of the same type and name in the corresponding `xml` resource, for example, to customize the dimension `misnapWorkflowLayoutHorizontalMargin` create a matching resource in `values/dimens.xml`:

```xml
<resources>
    <dimen name="misnapWorkflowLayoutHorizontalMargin">8dp</dimen>
</resources>
```

- - - -

# Flow Screens
The MiSnap SDK `Workflow` module defines `Fragments` or screens that can be used out of the box to invoke the various session flows, many visual aspects and behaviors can be customized in these screens through the `MiSnapSettings`.

## Tutorial/Help Screen
### Displaying Custom Instructions
The tutorial/help screen displays useful information to guide the end user to a successful session, the MiSnap SDK displays specific instructions and drawables `within a layout` depending on the characteristics of the session like the `trigger mode, use case and orientation`.

The displayed instructions can be customized through the `MiSnapSettings.Workflow` object by supplying a custom `layout` resource, for example, to display a custom content for a selfie session in manual mode:

```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.FACE,
    license
).apply { 
    workflow.add(
        getString(R.string.misnapWorkflowFaceAnalysisFlowHelpFragmentLabel),
        HelpFragment.buildWorkflowSettings(manualLayoutId = R.layout.my_custom_layout)
    )
}
```

### Skipping the Tutorial
The MiSnap SDK will always display a tutorial screen at the start of every session unless the end-user has opted out, to entirely skip this screen follow the guide explained [here](#removingskipping-a-screen). 

Please see the in-code documentation for more details and other supported customization options for the `HelpFragment`.

Please see `examples/advanced/screens/replacescreen/ReplaceScreenActivity.kt` for the full code sample.

## Analysis Screen
Most of these customizations are applicable to Document, Barcode and Face sessions. Please see the in-code documentation for more details and other supported customization options for the `DocumentAnalysisFragment`, `BarcodeAnalysisFragment` and `FaceAnalysisFragment`.

### Customizing the GuideView
The MiSnap SDK displays a `GuideView` on the analysis screen to assist the end user to correctly position the camera for the best image quality possible.

The `GuideView` can be customized through the `MiSnapSettings.Workflow` object to modify its appearance, for example, to customize the appearance of the `GuideView` in the `DocumentAnalysisFragment`:

```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            guideViewDrawableId = R.drawable.my_custom_guide,
            guideViewShowVignette = true,
            guideViewAlignedScalePercentage = 0.7f,
            guideViewUnalignedScalePercentage = 0.8f
        )
    )
}
```

### Displaying the Manual Button in Auto Sessions
By default, the MiSnap SDK does not display the manual button in sessions configured with an auto trigger. However, it is possible to still display the manual button in auto trigger sessions using the `MiSnapSettings.Workflow` object, for example:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            manualButtonVisible = true,
        )
    )
}
```

**Note: turning on this setting while requesting high resolution frames in an auto trigger session mode will prevent the camera from generating high resolution frames.**

### Displaying a Document Label
The MiSnap SDK can display a document label on the analysis screen to assist the end user to use the correct document for the session.

By default, the document label is not displayed on screen but it can be activated and customized through the `MiSnapSettings.Workflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            shouldShowDocumentLabel = true,
            documentLabelStringId = R.string.my_custom_document_label
        )
    )
}
```

### Adjusting the Session Timeout
The MiSnap SDK features a timeout during the session analysis screen to prevent extended sessions, reduce resource usage, and to provide help to the end user for a successful session the next time in the `FailoverFragment`.

The timeout can be adjusted through the `MiSnapSettings.Workflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            timeoutDuration = 10_000
        )
    )
}
```

### Customizing the HintView
The MiSnap SDK will display on screen live hints whenever there are improvements that can be made for a better image quality or there are actions that the end use must perform to finalize the session.

Several aspects of the HintView can be customized through the `MiSnapSettings.Workflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            hintDuration = 2_500,
            hintAnimationId = R.anim.my_custom_animation,
            hintViewShouldShowBackground = false
        )
    )
}
```

### Customizing the SuccessView
By default, the MiSnap SDK displays a fullscreen success message when an image has been correctly acquired in a session through the `SuccessView`. There are several aspects of the `SuccessView` that can be customized by using the `MiSnapSettings.Workflow` object, for example:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            successViewMessageDrawableId = R.drawable.my_custom_drawable,
            successViewMessageAnimationId = R.anim.my_custom_animation,
            successViewBackgroundDrawableId = R.drawable.my_custom_background_drawable,
            successViewShouldVibrate = true,
            successViewSoundUri = "my_custom_sound_uri",
        )
    )
}
```

### Controlling the Review Screen
The MiSnap SDK features a review screen that is suitable for situations where there might be a potential low quality on the acquired image.
This behavior can be customized using the `MiSnapSettings.Workflow` object, for example, to never show the review screen:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowDocumentAnalysisFragmentLabel),
        DocumentAnalysisFragment.buildWorkflowSettings(
            reviewCondition = DocumentAnalysisFragment.ReviewCondition.NEVER
        )
    )
}
```

## Failover Screen
### Customizing the Failover Reasons
The MiSnap SDK features a failover screen that displays useful information about the session and tips to successfully complete the session in the next attempt. The number of failover reasons and the evaluation threshold for these can be customized through the `MiSnapSettings.Workflow` object, for example:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.ID_FRONT,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowDocumentAnalysisFlowFailoverFragmentLabel),
        FailoverFragment.buildWorkflowSettings(
            maxReasons = 2,
            minReasonPercentage = 30
        )
    )
}
```

Please see the in-code documentation for more details and other supported customization options for the `FailoverFragment`.

## NFC Reader Screen
### Displaying Custom Scan Instructions
The MiSnap SDK, by default, selects the best set of instructions and graphics to display to the end user on the different stages of an NFC read session. However, there are situations in which it's better to tailor these instructions.
The customization of the instruction for an NFC session can be configured through the `MiSnapSettings.Workflow` object, for example:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.NFC,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowNfcReaderFlowNfcReaderFragmentLabel),
        NfcReaderFragment.buildWorkflowSettings(
            scanInstructionsSearchingDrawableId = R.drawable.my_custom_searching_drawable,
            scanInstructionsReadingDrawableId = R.drawable.my_custom_reading_drawable,
            scanInstructionsFailureDrawableId = R.drawable.my_custom_failure_drawable,
            scanInstructionsSuccessDrawableId = R.drawable.my_custom_success_drawable,
            scanInstructionsTextId = R.string.my_custom_instructions
        )
    )
}
```

### Controlling the Skip and Failover
During NFC sessions, the MiSnap SDK displays an option to skip the NFC read operation after a given period of time and also displays a failover dialog when there are errors during the NFC read session. These settings can be easily customized in the `MiSnapSettings.Workflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.NFC,
    license
).apply {
    workflow.add(
        getString(R.string.misnapWorkflowNfcReaderFlowNfcReaderFragmentLabel),
        NfcReaderFragment.buildWorkflowSettings(
            skipVisibilityTimeout = 20_000,
            shouldShowFailoverPopup = true
        )
    )
}
```

Please see the in-code documentation for more details and other supported customization options for the `NfcReaderFragment`.

## Voice Recording Screen
### Customizing the ProgressTrackerView
During a voice session, the MiSnap SDK displays a `ProgressTrackerView` to communicate the current progress of the voice samples collection, it is possible to customize the appearance of the `ProgressTrackerView` through the use of the `MiSnapSettings.Workflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.VOICE,
    license
).apply {
    voice.flow = MiSnapSettings.Voice.Flow.ENROLLMENT
    workflow.add(
        getString(R.string.misnapWorkflowVoiceProcessorFlowVoiceProcessorFragmentLabel),
        VoiceProcessorFragment.buildWorkflowSettings(
            progressTrackerViewFailureDrawableId = R.drawable.my_custom_failure_drawable,
            progressTrackerViewSuccessDrawableId = R.drawable.my_custom_success_drawable,
            progressTrackerViewUnprocessedDrawableId = R.drawable.my_custom_unprocessed_drawable
        )
    )
}
```

### Adjusting the Session Timeout
The MiSnap SDK features a timeout during the voice processing screen to prevent extended sessions, reduce resource usage, and to provide help to the end user in a timely manner.

The timeout can be adjusted through the `MiSnapSettings.Worrkflow` object as follows:
```kotlin
MiSnapSettings(
    MiSnapSettings.UseCase.VOICE,
    license
).apply {
voice.flow = MiSnapSettings.Voice.Flow.ENROLLMENT
workflow.add(
    getString(R.string.misnapWorkflowVoiceProcessorFlowVoiceProcessorFragmentLabel),
    VoiceProcessorFragment.buildWorkflowSettings(
        timeoutDuration = 20_000
    )
)
}
```

Please see the in-code documentation for more details and other supported customization options for the `VoiceProcessorFragment`.

- - - -

# Custom Screens
The MiSnap SDK includes a full featured workflow to achieve the best experience out of the box, however, in situations where the customization needs are greater than what the SDK offers, it is possible to create your own UI/UX by building a custom `Fragment` and defining a custom `navgraph` resource to use it within the MiSnap SDK.

Please see [Jetpack Navigation](https://developer.android.com/guide/navigation) for more information.

## Replacing a Screen
Replacing a screen in an existing workflow can be achieved with the following steps:
1. Identify the target workflow navigation graph resource as explained [here](#approaching-customization), then identify the `Fragment` to replace, including the defined `actions` that navigate to other fragments. For example, the `document_session_flow.xml` navgraph.
2. Build a custom `Fragment` to implement your own UI/UX, it is important to define logic that executes the `actions` defined in the target fragment if you want to still use the rest default fragments and resume the default flow. For example, replacing the `HelpFragment` with a custom version that is able to continue the flow to the built-in `DocumentAnalysisFragment` requires that the custom `Fragment` executes the `navigateContinue` action:
```kotlin
findNavController().navigate(R.id.navigateContinue)
```
3. Create a custom `navgraph` resource under `/navigation` and replace the target fragment definition with the custom implementation, for example, replacing the `HelpFagment` with a custom `MyHelpFragment` in the `document_session_flow.xml` to a custom navgraph can look like this:
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/customSessionFlow"
    app:startDestination="@id/helpFragment">
    <fragment
        android:id="@+id/helpFragment"
        android:name="com.example.MyHelpFragment"
        android:label="@string/myCustomFragmentLabel">
        <action
            android:id="@+id/navigateContinue"
            app:destination="@id/documentAnalysisFragment"
            app:popUpTo="@id/helpFragment"
            app:popUpToInclusive="true"/>
    </fragment>
    <!-- Other fragments definition -->
</navigation>
```
4. Finally, use the custom navgraph when invoking the MiSnap SDK:
```kotlin
MiSnapWorkflowStep(
    MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT,license),
    R.navigation.my_custom_flow
)
```

Please see `examples/advanced/screens/replacescreen/ReplaceScreenActivity.kt` for the full code sample.

## Removing/Skipping a Screen
Removing or skipping a screen in an existing workflow can be achieved with the following steps:
1. Identify the target workflow navigation graph resource as explained [here](#approaching-customization), then identify the fragment to skip. For example, the `document_session_flow.xml` navgraph and the `HelpFragment` to skip the first time tutorial screen.
2. If the target `Fragment` to skip is the `startDestination` of the `navgraph`, replacing the `startDestination` with another `Fragment` is enough to skip it. If it is a screen deeper into the flow, identify the `actions` that lead to the target `Fragment` and remove the `Fragment` reference, then modify the `actions` to reconnect them to the appropriate `Fragments`. 
For example, skipping the `HelpFragment` first tutorial screen to start in the `DocumentAnalysisFragment` instead can be achieved by replacing the `startDestination` in a custom navgraph as follows:
```xml
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/documentSessionFlow"
    app:startDestination="@id/documentAnalysisFragment"> 
    <!-- Fragments definition -->
</navigation>
```
3. Finally, use the custom navgraph when invoking the MiSnap SDK:
```kotlin
MiSnapWorkflowStep(
    MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT,license),
    R.navigation.my_custom_flow
)
```

Please see `examples/advanced/screens/SkipScreenActivity.kt` for the full code sample.

## Adding a New Screen
Adding a new screen to an existing flow can be achieved with the following steps:
1. Identify the target workflow navigation graph resource as explained [here](#approaching-customization), then identify the place where you want a new screen to be inserted. For example, in the `document_session_flow.xml` navgraph to include a new screen between the `HelpFragment` screen and the `DocumentAnalysisFragment`.
2. Identify the actions in the origin `Fragment` that drive the navigation to the destination `Fragment`, then modify the `destination` of the origin `Fragment` `action` to point to your custom `Fagment`. For example:
```xml
<fragment
    android:id="@+id/helpFragment"
    android:name="com.miteksystems.misnap.workflow.fragment.HelpFragment"
    android:label="@string/misnapWorkflowDocumentAnalysisFlowHelpFragmentLabel">
    <action
        android:id="@+id/navigateContinue"
        app:destination="@id/myCustomFragment"
        app:popUpTo="@id/helpFragment"
        app:popUpToInclusive="true"/>
        .
        .
        <!-- Other actions and arguments definition -->
</fragment>
```
3. Build a custom `Fragment` and include logic to navigate from it to the destination `Fragment`, then define it in the custom navgraph. For example:
```xml
<fragment
    android:id="@+id/myCustomFragment"
    android:name="com.example.myCustomFragment"
    android:label="@string/myCustomFragmentLabel">
    <action
        android:id="@+id/navigateContinue"
        app:destination="@id/documentAnalysisFragment"
        app:popUpTo="@id/myCustomFragment"
        app:popUpToInclusive="true"/>
        <!-- Other actions and arguments definition -->
</fragment>
```
4. Finally, use the custom navgraph when invoking the MiSnap SDK:
```kotlin
MiSnapWorkflowStep(
    MiSnapSettings(MiSnapSettings.UseCase.ID_FRONT,license),
    R.navigation.my_custom_flow
)
```

Please see `examples/advanced/screens/addscreen/AddScreenActivity.kt` for the full code sample.

- - - -

# Design Considerations
The following list is a collection of things to look out for while customizing the MiSnap SDK:

* For an optimal document analysis session, it is recommended to align the long side of the document with the long side of the device.

* When using a `GuideView` in a document analysis session, ensure that the drawable is:

    * Of the same aspect ratio as the target document.
    * Large enough to fit the target document but not too large that it's closer to the edges of the device.

* When enabling the vignette effect in a `GuideView` whose drawable is not rectangle-shaped, ensure that the drawable design includes the vignette. Please see `drawable/example_guideview_oval.xml` for reference.

* Keeping hint messages concise will ensure that the messages are easier to understand.

* Keeping hint duration too short (might not allow users to read the hints and act on them fast enough) or too long (the hint shown may not be relevant anymore) can negatively impact the user's experience.

* Using a `CountdownTimerView`, especially for face sessions, can result in high quality, non-blurry images, as it provides visual feedback to users when the MiSnap SDK is ready to complete the session.

* The `MiSnapView` and `CameraView` don't request access for camera permission. Ensure that the application holds the permission to use the camera before using them.

* Making the session timeout duration too short might result in multiple fail-overs and users might opt to complete the session manually, thereby resulting in a non-optimal image being returned.

* Synchronizing the trigger delay for a face analysis session and the duration of the CountdownTimerView yields a better experience.

* All views in the `workflow` module extend from either material design components or `AppCompat` components. All customizations supported by material design and `AppCompat` are supported by the views.

- - - -
