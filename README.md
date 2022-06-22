# MiSnap SDK v5.0.0 for Android

# Table of Contents
[Getting Started](#getting-started)
* [Release Notes](#release-notes)
* [SDK Size](#sdk-size)
* [System Requirements](#system-requirements) 
* [Devices Tested](#devices-tested)
* [Known Issues](#known-issues)

[Migration Guide](#migration-guide)

[Integration Guides](#integration-guides)

[Frequently Asked Questions (FAQs)](#frequently-asked-questions-faqs)

[Third-Party Licenses](#third-party-licenses)

- - - -

# Getting Started

## Release Notes

### **Version 5.0.0**

This version is a **complete redesign** of the MiSnap SDK and is **NOT backwards compatible**.

This release focuses on providing a **uniform API** throughout the different parts of the SDK, **ease of integration**, and **true opt-in behavior** for optional components. 

Additionally, the MiSnap SDK for Android is now available as a **maven/gradle dependency** and requires a valid license to function. Please follow the [FAQs](#frequently-asked-questions-faqs) for more details and see the in-code documentation for the API changes.

Starting with v5.0.0, the MiSnap SDK does not provide any module as source.

#### **Added**
* Support to extract BSN (burgerservicenummer) from newer Netherlands passport templates.
* x86_64 support for the barcode scanning use case.
* Support for mandatory barcode extraction in addition to a good quality image.
* Support to record a video of the session.
* Support to seamlessly start multiple sessions.
* Support to query the camera capabilities before starting a session.
* Support for 1080p selfie images.
* Support for generic documents analysis sessions.
* Support to show a manual review screen to users.
* Dutch (nl) string translations.
* Support for Jetpack Navigation to create custom flows.
* Android Views for creating custom screens.

#### **Modified**
* Changed the minimum API level to 23 (Android Marshmallow).
* Dropped the use of deprecated camera APIs in favor of CameraX.
* Reduced the SDK footprint for face and NFC use cases.
* Reduced the memory footprint of the SDK by roughly 15%.
* MiSnap SDK now runs in the same process as the host app.
* Improved the organization and discoverability of the SDK configuration options.
* Improved the document orientation handling.
* Improved the SDK theming (including support for light and dark themes).
* Improved messaging when accessibility services are enabled.
* Various non-ID documents were folded into the generic document use case.

#### **Removed**
* MiSnapWorkflow UX1 and UX2 in favor of a new, unified style.
* Support for credit cards.

Please see [this page](documentation/nfc_regions_documents_supported.md) for the full list of regions and documents supported by the MiSnap SDK NFC Reader.

## SDK Size
All sizes are download sizes for the arm64-v8a architecture.
Please see [this page](documentation/download_sizes.md) for the in-depth size tables.
<!-- SIZE_TABLE_START -->
| Use Case                    | Download Size (MiB) | 
| :-------------------------- | ------------------: |
| Document                    | 5.18                | 
| Document and Barcode        | 6.42                | 
| Document and Face           | 11.95               | 
| Document, Barcode, and Face | 13.18               | 
| Document, Face, and NFC     | 15.13               | 
<!-- SIZE_TABLE_END -->

## System Requirements

| Technology               | Version |
| :--------------------    | :------ |
| Android Gradle Plugin    | 4.2.2   |
| Gradle                   | 6.7.1   |
| Kotlin                   | 1.5.32  |
| JDK                      | 11      |
| Android min API level    | 23      |
| Android target API level | 31      |

Please see [versions.gradle](versions.gradle) for more details.

## Devices Tested

Please see the [devices tested](documentation/devices_tested.md) page for more details.

## Known Issues
* As the `face-analysis` module uses `Google’s MLKit` for face detection, please follow [this link](https://developers.google.com/ml-kit/known-issues) for known issues.

- - - -

# Migration Guide

Please follow [this migration guide](documentation/migration_guide.md) to upgrade to MiSnap SDK v5.0.0 if you have an existing MiSnap SDK integration.

- - - -

# Integration Guides

The MiSnap SDK provides several integration layer options. We ***highly recommend*** [this activity-based integration guide](documentation/activity_integration_guide.md). 

To integrate the MiSnap SDK in single-activity architecture applications, please follow [this fragment-based integration guide](documentation/fragment_integration_guide.md).  

The MiSnap SDK also provides some easy-to-use custom Android Views to recreate various screens without integrating the default `Fragment`s provided in the SDK. Please follow [this views integration guide](documentation/views_integration_guide.md) for more information.

To use MiSnap's base processing without any UI/UX provided in the MiSnap SDK, please follow [this science integration guide](documentation/science_integration_guide.md).

- - - -

# Frequently Asked Questions (FAQs)

### How to integrate the MiSnap SDK using Maven?
Please follow these steps:

1. Create a Personal Access Token (PAT) on https://www.github.com  
   Please follow [this guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token#creating-a-token) to create PAT.
2. Add the following to the **project level** `build.gradle`:
    ```groovy
    allProjects {
      repositories {
        ...
        maven {
          url 'https://maven.pkg.github.com/mitek-systems/misnap-android'
          credentials {
            username = 'your-github-username'
            password = 'personal-access-token' // created in Step 1.
          }
        }
      }
    }
    ```
3. Add the required dependencies as per the [Integration Guides](#integration-guides).

### How to integrate the MiSnap SDK without having access to a remote Maven repository?
The MiSnap SDK provides the `Library_Modules/maven-local-manager.sh` script for Linux/MacOS and the `Library_Modules/maven-local-manager.bat` script for Windows in the release package. Please follow the below steps:
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

### Does the MiSnap SDK require a license to function?
Yes, MiSnap now requires a valid license to be provided to function. Please follow the appropriate [Integration Guide](#integration-guides) and the code samples for information on how to pass a license to the SDK.

### Where can I get a license?
Please reach out to your Mitek support team to obtain a license.

### Which regions and documents are supported by the MiSnap SDK for NFC?
Please see the [regions and documents page](documentation/nfc_regions_documents_supported.md) for the full list of supported regions and documents.

### How can I reduce the size of my application?
The MiSnap SDK uses both JVM and native components, and so requires dedicated library versions for 
different hardware architectures (ABIs). No single device requires multiple ABIs, and so bundling multiple
ABIs into a single app increases its size without providing any addition functionality. This can be avoided
by either using [App Bundles](https://developer.android.com/guide/app-bundle) 
or by using [Apk Splits](https://developer.android.com/studio/build/configure-apk-splits#configure-abi-split).

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

### [BSD 2-Clause "Simplified" License](https://opensource.org/licenses/BSD-2-Clause)
* #### [JP2ForAndroid](https://github.com/ThalesGroup/JP2ForAndroid/blob/master/LICENSE)

### [GNU Lesser General Public License](https://www.gnu.org/licenses/lgpl-3.0.en.html)
* #### [jMRTD](https://jmrtd.org/license.shtml)
* #### [Smart Card Utilities for Better Access (SCUBA) for Android](https://www.gnu.org/licenses/lgpl-3.0.en.html)

- - - -
