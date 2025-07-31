# MiSnap SDK v5.8.2 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.25        | 6.43      | 7.48         | 6.52  | 6.52   | 7.85         | 10.13    | 
| Document and Barcode             | 7.45        | 7.68      | 9.92         | 7.79  | 7.87   | 10.47        | 15.19    | 
| Document and Biometric           | 13.65       | 14.43     | 18.49        | 14.94 | 14.9   | 20.25        | 29.16    | 
| Document, Barcode, and Biometric | 14.85       | 15.68     | 20.94        | 16.22 | 16.26  | 22.88        | 34.22    | 
| Document, Biometric, and NFC     | 18.9        | 20.04     | 26.05        | 20.49 | 20.39  | 27.99        | 41.15    | 
| Document Classification          | 13.14       | 14.51     | 20.72        | 14.68 | 14.49  | 22.25        | 36.03    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.05        | 4.23      | 5.29         | 4.33  | 4.32   | 5.65         | 7.94     | 
| Barcode       | 4.66        | 4.79      | 6.46         | 4.88  | 4.94   | 6.84         | 10.32    | 
| Face          | 10.14       | 10.75     | 13.92        | 11.2  | 11.16  | 15.4         | 22.34    | 
| Voice         | 3.2         | 3.35      | 4.11         | 3.45  | 3.41   | 4.41         | 6.07     | 
| Classifier    | 11.06       | 12.43     | 18.64        | 12.61 | 12.42  | 20.17        | 33.95    | 
| NFC           | 5.81        | 5.9       | 6.48         | 5.99  | 5.97   | 6.73         | 8.0      | 
| Combined NFC  | 9.27        | 9.81      | 12.81        | 9.84  | 9.77   | 13.35        | 19.9     | 
| Workflow (UI) | 4.49        | 4.56      | 5.0          | 4.63  | 4.6    | 5.18         | 6.13     | 
<!-- SCIENCE_SIZE_TABLE_END -->
