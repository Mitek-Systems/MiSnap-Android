# MiSnap SDK v5.11.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.46        | 6.67      | 7.91         | 6.77  | 6.75   | 8.3          | 10.99    | 
| Document and Barcode             | 9.1         | 9.83      | 13.7         | 9.45  | 9.86   | 14.08        | 22.54    | 
| Document and Biometric           | 13.86       | 14.67     | 18.92        | 15.18 | 15.14  | 20.71        | 30.01    | 
| Document, Barcode, and Biometric | 16.5        | 17.83     | 24.7         | 17.86 | 18.24  | 26.48        | 41.56    | 
| Document, Biometric, and NFC     | 19.09       | 20.27     | 26.47        | 20.72 | 20.61  | 28.43        | 42.0     | 
| Document Classification          | 13.35       | 14.75     | 21.14        | 14.93 | 14.73  | 22.69        | 36.88    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.25        | 4.47      | 5.71         | 4.56  | 4.55   | 6.09         | 8.79     | 
| Barcode       | 6.3         | 6.93      | 10.23        | 6.53  | 6.92   | 10.44        | 17.67    | 
| Face          | 10.35       | 10.99     | 14.34        | 11.45 | 11.39  | 15.85        | 23.2     | 
| Voice         | 3.22        | 3.36      | 4.14         | 3.45  | 3.42   | 4.43         | 6.13     | 
| Classifier    | 11.26       | 12.67     | 19.06        | 12.84 | 12.64  | 20.61        | 34.79    | 
| NFC           | 5.83        | 5.92      | 6.52         | 6.0   | 5.97   | 6.75         | 8.05     | 
| Combined NFC  | 9.46        | 10.04     | 13.23        | 10.07 | 9.99   | 13.79        | 20.74    | 
| Workflow (UI) | 4.52        | 4.59      | 5.05         | 4.65  | 4.62   | 5.21         | 6.2      | 
<!-- SCIENCE_SIZE_TABLE_END -->