# MiSnap SDK v5.2 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.36        | 5.57      | 6.58         | 5.67  | 5.67   | 6.98         | 9.21     | 
| Document and Barcode             | 6.57        | 6.83      | 9.04         | 6.96  | 7.03   | 9.62         | 14.31    | 
| Document and Biometric           | 12.52       | 13.31     | 17.03        | 14.0  | 13.94  | 19.15        | 27.4     | 
| Document, Barcode, and Biometric | 13.73       | 14.56     | 19.49        | 15.29 | 15.31  | 21.79        | 32.48    | 
| Document, Biometric, and NFC     | 15.77       | 16.85     | 22.16        | 17.48 | 17.33  | 24.35        | 36.05    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86  | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ---: | -----: | -----------: | -------: |
| Document      | 3.33        | 3.53      | 4.54         | 3.63 | 3.63   | 4.94         | 7.17     | 
| Barcode       | 3.86        | 3.97      | 5.53         | 4.06 | 4.14   | 5.89         | 9.12     | 
| Face          | 8.9         | 9.39      | 12.14        | 10.0 | 9.94   | 13.79        | 19.78    | 
| Voice         | 3.14        | 3.34      | 4.03         | 3.45 | 3.47   | 4.46         | 6.04     | 
| NFC           | 4.13        | 4.21      | 4.7          | 4.29 | 4.31   | 4.95         | 6.01     | 
| Combined NFC  | 6.61        | 7.12      | 9.71         | 7.15 | 7.06   | 10.18        | 15.87    | 
| Workflow (UI) | 4.25        | 4.31      | 4.67         | 4.37 | 4.38   | 4.85         | 5.63     | 
<!-- SCIENCE_SIZE_TABLE_END -->
