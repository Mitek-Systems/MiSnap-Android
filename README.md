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

#### **Added**
* Feature

#### **Removed**
* Feature

#### **Modified**
* API

#### **Fixed**
* Defect

Please see [this page](documentation/nfc_regions_documents_supported.md) for full list of regions and documents supported by MiSnap NFC Reader.

## SDK Size
All sizes are download sizes for the arm64-v8a architecture.
Please see [this page](documentation/download_sizes.md) for in-depth size tables.
<!-- SIZE_TABLE_START -->
| Use Case                    | Download Size (MiB) | 
| :-------------------------- | ------------------: |
| Document                    | 5.14                | 
| Document and Barcode        | 6.38                | 
| Document and Face           | 11.9                | 
| Document, Barcode, and Face | 13.14               | 
| Document, Face, and NFC     | 15.09               | 
<!-- SIZE_TABLE_END -->

## System Requirements

| Technology               | Version |
| :--------------------    | :------ |
| Android Gradle Plugin    | 4.2.2   |
| Gradle                   | 6.7.1   |
| Kotlin                   | 1.5.32  |
| JDK                      | 11      |
| Android min API level    | 23+     |
| Android target API level | 31      |

Please see [versions.gradle](versions.gradle) for more details.

## Devices Tested

Please see [devices tested](documentation/devices_tested.md) page for more details.

## Known Issues
* As the `face-analysis` module uses `Google’s MLKit` for face detection, please follow [this link](https://developers.google.com/ml-kit/known-issues) for known issues.

- - - -

# Migration Guide

Please follow [this migration guide](documentation/migration_guide.md) to upgrade to MiSnap SDK 5.0.0 if you're an existing MiSnap user.

- - - -

# Integration Guides

MiSnap SDK provides several layers of integration options. We ***highly recommend*** [this activity-based integration guide](documentation/activity_integration_guide.md). 

To integrate MiSnap in single-activity architecture applications, please follow [this fragment-based integration guide](documentation/fragment_integration_guide.md).  

MiSnap SDK also provides some easy-to-use custom Android views to be able to recreate various screens without integrating default `Fragment`s provided in the SDK. Please follow [this views integration guide](documentation/views_integration_guide.md) for more information.

To use Mitek's core computational processing without any UI/UX provided in MiSnap SDK, please follow [this science integration guide](documentation/science_integration_guide.md).

- - - -

# Frequently Asked Questions (FAQs)

### How to integrate MiSnap SDK using maven?
Please follow the below steps:

1. Add the following to **project level** `build.gradle`:
    ```groovy
    allProjects {
      repositories {
        ...
        maven {
          url '' // coming soon
          username '' // coming soon
          password '' // coming soon
        }
      }
    }
    ```
2. Add required dependencies as per the [Integration Guides](#integration-guides)

### How to integrate MiSnap SDK without having access to maven?
The MiSnap SDK provides `Library_Modules/maven-local-manager.sh` script for Linux/MacOS and `Library_Modules/maven-local-manager.bat` script for Windows in the release package.  Please follow the below steps:
1. Run the appropriate script to deploy all MiSnap aar files to maven local<br>  
___For Linux/MacOS___:  
a. Open terminal in `Library_Modules` directory  
b. Run `sh maven-local-manager.sh -i` command  <br><br>
___For Windows___:  
a. Open command prompt in `Library_Modules` directory  
b. Run `maven-local-manager.bat /i` command

2. Add the following to **project level** `build.gradle`:
    ```groovy
    allprojects {
      repositories {
        ...
        //Please make sure mavenLocal() is last entry in repositories
        mavenLocal()
      }
    }
    ```
3. Add required dependencies as per the [Integration Guides](#integration-guides)

### Does MiSnap SDK requires a license to function?
Yes, MiSnap SDK now requires a valid license to be provided to function. Please follow appropriate [Integration Guide](#integration-guides) and provided code samples on how to pass a license key to the SDK.

### Where can I get the license?
Please reach out to your Mitek support team to obtain a license.

### Which regions and documents are supported by MiSnap SDK for NFC?
Please see [regions and documents page](documentation/nfc_regions_documents_supported.md) for full list of supported regions and documents.

### How can I reduce the size of the SDK?
The MiSnap SDK uses both JVM and native components, and so requires dedicated library versions for 
different hardware architectures (ABIs).  No single device requires multiple ABIs, and so bundling multiple
ABIs into a single app increases its size without providing any addition functionality.  This can be avoided
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
