# MiSnap SDK v5.10.1 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.45        | 6.67      | 7.9          | 6.76  | 6.75   | 8.29         | 10.98    | 
| Document and Barcode             | 9.09        | 9.82      | 13.68        | 9.44  | 9.85   | 14.06        | 22.52    | 
| Document and Biometric           | 13.85       | 14.67     | 18.91        | 15.18 | 15.13  | 20.7         | 30.01    | 
| Document, Barcode, and Biometric | 16.49       | 17.82     | 24.69        | 17.85 | 18.23  | 26.47        | 41.55    | 
| Document, Biometric, and NFC     | 19.09       | 20.26     | 26.46        | 20.72 | 20.6   | 28.43        | 41.99    | 
| Document Classification          | 13.35       | 14.75     | 21.14        | 14.92 | 14.72  | 22.69        | 36.88    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.24        | 4.46      | 5.7          | 4.55  | 4.54   | 6.09         | 8.78     | 
| Barcode       | 6.29        | 6.92      | 10.22        | 6.52  | 6.91   | 10.43        | 17.65    | 
| Face          | 10.34       | 10.98     | 14.34        | 11.44 | 11.39  | 15.84        | 23.19    | 
| Voice         | 3.21        | 3.36      | 4.13         | 3.45  | 3.41   | 4.42         | 6.12     | 
| Classifier    | 11.25       | 12.66     | 19.05        | 12.84 | 12.63  | 20.61        | 34.79    | 
| NFC           | 5.82        | 5.91      | 6.51         | 5.99  | 5.96   | 6.74         | 8.04     | 
| Combined NFC  | 9.45        | 10.03     | 13.22        | 10.06 | 9.98   | 13.78        | 20.74    | 
| Workflow (UI) | 4.51        | 4.58      | 5.04         | 4.64  | 4.61   | 5.2          | 6.19     | 
<!-- SCIENCE_SIZE_TABLE_END -->
