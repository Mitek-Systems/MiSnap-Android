# MiSnap SDK v5.6.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.82        | 6.0       | 6.99         | 6.11  | 6.12   | 7.41         | 9.58     | 
| Document and Barcode             | 7.03        | 7.25      | 9.44         | 7.39  | 7.48   | 10.03        | 14.64    | 
| Document and Biometric           | 12.98       | 13.73     | 17.45        | 14.44 | 14.4   | 19.58        | 27.77    | 
| Document, Barcode, and Biometric | 14.18       | 14.99     | 19.9         | 15.72 | 15.75  | 22.2         | 32.84    | 
| Document, Biometric, and NFC     | 16.6        | 17.72     | 23.39        | 18.37 | 18.25  | 25.69        | 38.14    | 
| Document Classification          | 12.48       | 13.75     | 19.6         | 14.1  | 13.96  | 21.43        | 34.39    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.63        | 3.8       | 4.8          | 3.92  | 3.93   | 5.22         | 7.39     | 
| Barcode       | 4.26        | 4.38      | 6.01         | 4.48  | 4.54   | 6.4          | 9.79     | 
| Face          | 9.32        | 9.82      | 12.66        | 10.44 | 10.37  | 14.33        | 20.5     | 
| Voice         | 3.25        | 3.45      | 4.2          | 3.58  | 3.57   | 4.64         | 6.34     | 
| Classifier    | 10.25       | 11.51     | 17.36        | 11.86 | 11.72  | 19.19        | 32.16    | 
| NFC           | 4.25        | 4.32      | 4.88         | 4.43  | 4.41   | 5.15         | 6.33     | 
| Combined NFC  | 7.3         | 7.82      | 10.77        | 7.88  | 7.83   | 11.36        | 17.79    | 
| Workflow (UI) | 4.55        | 4.62      | 5.03         | 4.69  | 4.67   | 5.22         | 6.11     | 
<!-- SCIENCE_SIZE_TABLE_END -->
