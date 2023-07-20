# MiSnap SDK v5.3.3 for Android

# Table of Contents
[Getting Started](#getting-started)
* [Release Notes](#release-notes)
* [SDK Size](#sdk-size)
* [System Requirements](#system-requirements) 
* [Devices Tested](#devices-tested)
* [Known Issues](#known-issues)

[Migration Guide](#migration-guide)

[Integration Guides](#integration-guides)

[Customization Guide](#customization-guide)

[Code Examples](#code-examples)

[Frequently Asked Questions (FAQs)](#frequently-asked-questions-faqs)

[Third-Party Licenses](#third-party-licenses)

[Terms and Conditions](#terms-and-conditions)

- - - -

# Getting Started

## Release Notes
#### **Added**
* [Document] The `DocumentAnalysisFragment.WorkflowSettings` now support the boolean option `shouldShowDocumentLabel` which displays on screen a `TextView` with the name of the expected document type. Additionally, the `documentLabelStringId` argument has been added to customize the document name for special use cases.

#### **Fixed**
* [NFC] Fixed an issue that prevented some SWE and ITA documents from scanning correctly.
* [Common] Fixed an issue where the `HintView` would be displayed even when there were no hints to display, making the use of padding in the view difficult.

### **Version 5.3.3**

Please see [this page](documentation/change_log.md) for release notes from older releases.

Please see [this page](documentation/nfc_regions_documents_supported.md) for the full list of regions and documents supported by the MiSnap SDK NFC Reader.

## SDK Size
All sizes are download sizes for the `arm64-v8a` architecture.
Please see [this page](documentation/download_sizes.md) for the in-depth size tables.
<!-- SIZE_TABLE_START -->
| Use Case                         | Download Size (MiB) | 
| :------------------------------- | ------------------: |
| Document                         | 5.65                | 
| Document and Barcode             | 6.91                | 
| Document and Biometric           | 13.38               | 
| Document, Barcode, and Biometric | 14.64               | 
| Document, Biometric, and NFC     | 16.93               | 
<!-- SIZE_TABLE_END -->

## System Requirements

| Technology               | Version |
| :--------------------    |:--------|
| Android Gradle Plugin    | 4.2.2   |
| Gradle                   | 6.7.1   |
| Kotlin                   | 1.5.32  |
| CameraX                  | 1.1.0   |
| JDK                      | 11      |
| Android min API level    | 23      |
| Android target API level | 31      |

Please see [versions.gradle](versions.gradle) for more details.

## Devices Tested

Please see the [devices tested](documentation/devices_tested.md) page for more details.

## Known Issues
* As the `face-analysis` module uses `Google’s MLKit` for face detection, please follow [this link](https://developers.google.com/ml-kit/known-issues) for known issues.
* The `minSdkVersion` used in the MiSnap SDK is not compatible with higher `jmrtd` versions. 

- - - -

# Migration Guide

Please follow [this migration guide](documentation/migration_guide.md) to upgrade to the latest SDK version if you have an existing MiSnap SDK integration.

- - - -

# Integration Guides

The MiSnap SDK provides several integration layer options. We ***highly recommend*** [this activity-based integration guide](documentation/activity_integration_guide.md). 

To integrate the MiSnap SDK in single-activity architecture applications, please follow [this fragment-based integration guide](documentation/fragment_integration_guide.md).  

The MiSnap SDK also provides some easy-to-use custom Android Views to recreate various screens without integrating the default `Fragment`s provided in the SDK. Please follow [this views integration guide](documentation/views_integration_guide.md) for more information.

To use MiSnap's base processing without any UI/UX provided in the MiSnap SDK, please follow [this science integration guide](documentation/science_integration_guide.md).

- - - -

# Customization Guide

Please follow [this customization guide](documentation/customization_guide.md) for information on how to customize the MiSnap SDK workflow, UI and behavior.

- - - -

# Code Examples

The MiSnap SDK provides a set of [code examples](documentation/code_examples.md) that can be used as reference along with the integration guides to assist you with the SDK integration.

We highly recommend to use them as they provide key best practices for an optimal MiSnap SDK integration.

- - - -

# Frequently Asked Questions (FAQs)

### How to integrate the MiSnap SDK using Maven?
Please follow these steps:

1. Create a Personal Access Token (PAT) on https://www.github.com.  
   Please follow [this guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#creating-a-token) to create a PAT and make sure to select the `read:packages` scope.
2. Add the following to the **project level** `build.gradle`:
    ```groovy
    allProjects {
      repositories {
        ...
        maven {
          url = uri('https://maven.pkg.github.com/mitek-systems/misnap-android')
          credentials {
            username = 'your-github-username'
            password = 'personal-access-token' // created in Step 1.
          }
        }
      }
    }
    ```
3. Add the required dependencies as per the [Integration Guides](#integration-guides).

### Does the MiSnap SDK require a license to function?
Yes, MiSnap now requires a valid license to be provided to function. Please follow the appropriate [Integration Guide](#integration-guides) and the code samples for information on how to pass a license to the SDK.

### Where can I get a license?
Please reach out to your Mitek support team to obtain a license.

### How should I provision the license to the MiSnap SDK?
Avoid hard-coding the license in your application. Instead, fetch it from your application server before invoking the MiSnap SDK. This will allow you to easily switch the license in the future without requiring you to roll out a new version of your app.

### I want to customize the MiSnap SDK UI/UX. Where do I find the resources?
The MiSnap SDK provides several options to customize the UI/UX, whether it is drawables, colors, styles, strings or behaviors. Please follow [this customization guide](documentation/customization_guide.md) for more information on how to locate the appropriate resource or option that fits your needs.

### Can I know in advance the device's camera capabilities before starting a session?
Yes, when invoking the MiSnap SDK, it is a best practice to first query the camera support and set the `MiSnapSettings` capture mode according to the device's camera capabilities. This helps the asset selection of the `HelpFragment` be faster providing a smoother user experience. Look into `examples/camera/CameraSupport.Kt` for a code example on how to query the camera support.

### Which regions and documents are supported by the MiSnap SDK for NFC?
Please see the [regions and documents page](documentation/nfc_regions_documents_supported.md) for the full list of supported regions and documents.

### How can I reduce the size of my application?
The MiSnap SDK uses both JVM and native components, and so requires dedicated library versions for 
different hardware architectures (ABIs). No single device requires multiple ABIs, and so bundling multiple
ABIs into a single app increases its size without providing any addition functionality. This can be avoided
by either using [App Bundles](https://developer.android.com/guide/app-bundle) 
or by using [Apk Splits](https://developer.android.com/studio/build/configure-apk-splits#configure-abi-split).

### How does the video recording feature work when optional data (BSN) redaction for NLD documents is enabled?
The `video recording` feature requires no frame processing therefore optional data will not be redacted in a recorded video. It is your responsibility to enable or disable `video recording` as per your needs when `optional data redaction` is enabled.

### What device info is collected when using `DeviceInfoUtil`?
It's publicly available non-PII device properties exposed by the Android APIs along with a unique Mitek-specific ID. Note, an ID is unique for every application that has this SDK integrated and its sole purpose is tying a device along with biometrics (face and/or voice) in Mitek server products, i.e. it's impossible to use it to track any user activity for purposes of creating a user profile for advertisement and/or malicious activities.

### How to integrate the MiSnap SDK without having access to a remote Maven repository?
**NOTE**: Local maven integration requires access to the offline MiSnap SDK zip package, and will not work with the version available on Github.  Please reach out to support for access.

The offline MiSnap SDK provides the `Library_Modules/maven-local-manager.sh` script for Linux/MacOS and the `Library_Modules/maven-local-manager.bat` script for Windows in the offline release package. Please follow the below steps:
1. Run the appropriate script to deploy all the MiSnap SDK aar files to maven local.<br>  
___For Linux/MacOS___:  
a. Open a terminal in the `Library_Modules` directory.  
b. Run the `sh maven-local-manager.sh -i` command.  <br><br>
___For Windows___:  
a. Open a command prompt in the `Library_Modules` directory.  
b. Run the `maven-local-manager.bat /i` command.

2. Add the following to the **project level** `build.gradle`:
    ```groovy
    allprojects {
      repositories {
        ...
        //Please make sure that mavenLocal() is the last entry in the repositories block
        mavenLocal()
      }
    }
    ```
3. Add the required dependencies as per the [Integration Guides](#integration-guides).

- - - -

# Third-Party Licenses

### [3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause)
* #### [OpenCV](https://github.com/opencv/opencv/blob/4.4.0/LICENSE)

### [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
* #### [Apache Commons Imaging](https://github.com/apache/commons-imaging/blob/master/LICENSE.txt)
* #### [Kotlin](https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt)
* #### [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization/blob/master/license/)
* #### [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines/tree/master/license/)
* #### [Material Components for Android](https://github.com/material-components/material-components-android/blob/master/LICENSE)

### [BSD 2-Clause "Simplified" License](https://opensource.org/license/bsd-license-php/)
* #### [JP2ForAndroid](https://github.com/ThalesGroup/JP2ForAndroid/blob/master/LICENSE)

### [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0.en.html)
* #### [jMRTD](https://jmrtd.org/license.shtml)
* #### [Smart Card Utilities for Better Access (SCUBA) for Android](https://www.gnu.org/licenses/lgpl-3.0.en.html)

- - - -

# Terms and Conditions

By you accessing this SDK and/or any related documentation, sample apps and libraries (collectively, the “SDK”) you acknowledge that either (i) you currently are a party to a  contract/license with Mitek Systems, Inc. (“Mitek”) which governs access to and use of the SDK (in which case, the terms and conditions of that contract govern your use of the SDK) or (ii) you agree to the following terms and conditions:

You will use the SDK solely to evaluate the SDK for the purpose of internal development of software and applications to the extent necessary for such applications to transfer data to Mitek.  You have no right to receive any underlying software or a copy of the object code or source code to the SDK.   You will not make copies of the SDK. You will not make the SDK available to any third party.   Mitek shall own all modifications, revisions, or derivative works of the code contained in its SDK.

Your right to use the SDK is conditional upon the following. You may not: (i) sell, rent, lease or otherwise distribute or share the SDK; (ii) disassemble, decompile, reverse engineer the SDK or output from the foregoing or otherwise attempt to derive the source code (or the underlying ideas, algorithms, structure or organization); (iii) provide access to the SDK to any third party; (iv) create any derivative works based upon the SDK;  (v) access the SDK in order to build a competitive solution or gather competitive intelligence or to assist someone else to build a competitive solution or gather competitive intelligence; (vi) use the SDK in a way that violates any applicable law; (vii) interfere with or disrupt the integrity or performance of the SDK, or (viii) attempt to gain unauthorized access to Mitek’s related systems or networks.

Mitek is giving permission to you to use the SDK solely in accordance with these terms and conditions.  The SDK is not being sold. Mitek and/or its licensors retain the exclusive ownership of all rights, title and interest, including all intellectual property rights, in and to the SDK(s) and associated documentation, including but not limited (i) any components, corrections, updates, upgrades, methodologies, derivative works and associated documentation thereof and (ii) all trademarks, trade names, trade secrets and other proprietary information of Mitek related thereto. Mitek also owns all suggestions, ideas, enhancement requests, or feedback related to the SDK.

All rights not expressly granted to you are reserved by Mitek and its licensors, and you shall have no rights which arise by implication or estoppel. 

- - - -