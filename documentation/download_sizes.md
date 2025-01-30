# MiSnap SDK v5.7.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.23        | 6.42      | 7.45         | 6.52  | 6.53   | 7.86         | 10.11    | 
| Document and Barcode             | 7.43        | 7.67      | 9.9          | 7.8   | 7.88   | 10.47        | 15.17    | 
| Document and Biometric           | 13.63       | 14.42     | 18.46        | 14.95 | 14.92  | 20.27        | 29.14    | 
| Document, Barcode, and Biometric | 14.83       | 15.67     | 20.9         | 16.22 | 16.27  | 22.89        | 34.19    | 
| Document, Biometric, and NFC     | 17.28       | 18.43     | 24.43        | 18.89 | 18.79  | 26.4         | 39.54    | 
| Document Classification          | 13.13       | 14.5      | 20.69        | 14.69 | 14.5   | 22.26        | 36.01    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.03        | 4.22      | 5.26         | 4.33  | 4.33   | 5.66         | 7.92     | 
| Barcode       | 4.66        | 4.79      | 6.46         | 4.88  | 4.94   | 6.84         | 10.32    | 
| Face          | 10.14       | 10.75     | 13.92        | 11.2  | 11.16  | 15.4         | 22.34    | 
| Voice         | 3.2         | 3.35      | 4.1          | 3.45  | 3.41   | 4.41         | 6.07     | 
| Classifier    | 11.04       | 12.42     | 18.6         | 12.61 | 12.42  | 20.17        | 33.92    | 
| NFC           | 4.22        | 4.31      | 4.89         | 4.4   | 4.38   | 5.14         | 6.4      | 
| Combined NFC  | 7.67        | 8.21      | 11.2         | 8.26  | 8.19   | 11.77        | 18.3     | 
| Workflow (UI) | 4.45        | 4.52      | 4.96         | 4.58  | 4.56   | 5.14         | 6.09     | 
<!-- SCIENCE_SIZE_TABLE_END -->
