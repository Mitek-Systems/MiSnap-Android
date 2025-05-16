# MiSnap SDK v5.8.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.25        | 6.44      | 7.49         | 6.53  | 6.52   | 7.85         | 10.14    | 
| Document and Barcode             | 7.46        | 7.69      | 9.93         | 7.8   | 7.88   | 10.47        | 15.2     | 
| Document and Biometric           | 13.66       | 14.44     | 18.5         | 14.95 | 14.92  | 20.27        | 29.17    | 
| Document, Barcode, and Biometric | 14.86       | 15.69     | 20.95        | 16.23 | 16.27  | 22.89        | 34.23    | 
| Document, Biometric, and NFC     | 17.31       | 18.45     | 24.46        | 18.9  | 18.8   | 26.4         | 39.56    | 
| Document Classification          | 13.15       | 14.52     | 20.73        | 14.7  | 14.51  | 22.26        | 36.04    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.05        | 4.23      | 5.29         | 4.33  | 4.32   | 5.65         | 7.94     | 
| Barcode       | 4.66        | 4.79      | 6.46         | 4.88  | 4.94   | 6.84         | 10.32    | 
| Face          | 10.14       | 10.75     | 13.92        | 11.2  | 11.16  | 15.4         | 22.34    | 
| Voice         | 3.2         | 3.35      | 4.1          | 3.45  | 3.41   | 4.41         | 6.07     | 
| Classifier    | 11.06       | 12.43     | 18.64        | 12.61 | 12.42  | 20.17        | 33.95    | 
| NFC           | 4.22        | 4.31      | 4.89         | 4.4   | 4.37   | 5.14         | 6.4      | 
| Combined NFC  | 7.68        | 8.23      | 11.23        | 8.26  | 8.18   | 11.77        | 18.32    | 
| Workflow (UI) | 4.45        | 4.53      | 4.97         | 4.59  | 4.57   | 5.15         | 6.1      | 
<!-- SCIENCE_SIZE_TABLE_END -->
