# MiSnap SDK v5.5.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.65        | 5.82      | 6.77         | 5.93  | 5.94   | 7.17         | 9.24     | 
| Document and Barcode             | 6.85        | 7.07      | 9.21         | 7.2   | 7.3    | 9.79         | 14.3     | 
| Document and Biometric           | 12.81       | 13.55     | 17.22        | 14.26 | 14.22  | 19.34        | 27.42    | 
| Document, Barcode, and Biometric | 14.01       | 14.81     | 19.67        | 15.54 | 15.57  | 21.96        | 32.48    | 
| Document, Biometric, and NFC     | 16.41       | 17.52     | 23.13        | 18.17 | 18.07  | 25.43        | 37.74    | 
| Document Classification          | 12.3        | 13.56     | 19.37        | 13.92 | 13.77  | 21.19        | 34.06    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.46        | 3.63      | 4.58         | 3.74  | 3.75   | 4.99         | 7.05     | 
| Barcode       | 4.09        | 4.2       | 5.79         | 4.3   | 4.37   | 6.17         | 9.46     | 
| Face          | 9.14        | 9.64      | 12.42        | 10.26 | 10.18  | 14.09        | 20.16    | 
| Voice         | 3.22        | 3.42      | 4.13         | 3.55  | 3.54   | 4.57         | 6.2      | 
| Classifier    | 10.06       | 11.32     | 17.13        | 11.68 | 11.53  | 18.95        | 31.81    | 
| NFC           | 4.21        | 4.29      | 4.8          | 4.39  | 4.38   | 5.07         | 6.17     | 
| Combined NFC  | 7.12        | 7.65      | 10.54        | 7.7   | 7.65   | 11.12        | 17.42    | 
| Workflow (UI) | 4.52        | 4.58      | 4.96         | 4.65  | 4.64   | 5.15         | 5.98     | 
<!-- SCIENCE_SIZE_TABLE_END -->
