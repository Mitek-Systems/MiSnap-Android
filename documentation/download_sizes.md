# MiSnap SDK v5.6.2 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.87        | 6.06      | 7.09         | 6.16  | 6.17   | 7.49         | 9.73     | 
| Document and Barcode             | 8.49        | 9.19      | 12.83        | 8.83  | 9.26   | 13.24        | 21.22    | 
| Document and Biometric           | 13.36       | 14.15     | 18.18        | 14.67 | 14.64  | 19.98        | 28.83    | 
| Document, Barcode, and Biometric | 15.98       | 17.29     | 23.93        | 17.34 | 17.74  | 25.74        | 40.34    | 
| Document, Biometric, and NFC     | 18.59       | 19.74     | 25.73        | 20.2  | 20.1   | 27.7         | 40.82    | 
| Document Classification          | 12.85       | 14.23     | 20.41        | 14.41 | 14.23  | 21.97        | 35.71    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.7         | 3.89      | 4.91         | 3.99  | 4.0    | 5.31         | 7.55     | 
| Barcode       | 5.75        | 6.34      | 9.43         | 5.94  | 6.35   | 9.64         | 16.4     | 
| Face          | 9.82        | 10.42     | 13.58        | 10.88 | 10.84  | 15.06        | 21.99    | 
| Voice         | 3.23        | 3.38      | 4.13         | 3.47  | 3.44   | 4.44         | 6.1      | 
| Classifier    | 10.72       | 12.09     | 18.28        | 12.28 | 12.1   | 19.84        | 33.57    | 
| NFC           | 5.84        | 5.94      | 6.52         | 6.03  | 6.0    | 6.77         | 8.03     | 
| Combined NFC  | 8.96        | 9.5       | 12.49        | 9.55  | 9.48   | 13.05        | 19.56    | 
| Workflow (UI) | 4.57        | 4.65      | 5.09         | 4.71  | 4.69   | 5.27         | 6.22     | 
<!-- SCIENCE_SIZE_TABLE_END -->
