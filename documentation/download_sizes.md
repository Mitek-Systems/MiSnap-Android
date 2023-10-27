# MiSnap SDK v5.4.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.39        | 5.55      | 6.5          | 5.66  | 5.68   | 6.9          | 8.97     | 
| Document and Barcode             | 6.59        | 6.81      | 8.95         | 6.94  | 7.03   | 9.53         | 14.03    | 
| Document and Biometric           | 12.55       | 13.29     | 16.96        | 14.0  | 13.96  | 19.08        | 27.15    | 
| Document, Barcode, and Biometric | 13.75       | 14.54     | 19.4         | 15.28 | 15.31  | 21.69        | 32.21    | 
| Document, Biometric, and NFC     | 16.16       | 17.26     | 22.87        | 17.91 | 17.81  | 25.16        | 37.48    | 
| Document Classification          | 12.05       | 13.31     | 19.11        | 13.67 | 13.52  | 20.94        | 33.8     | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.35        | 3.51      | 4.46         | 3.63  | 3.64   | 4.86         | 6.93     | 
| Barcode       | 3.97        | 4.09      | 5.67         | 4.18  | 4.25   | 6.05         | 9.34     | 
| Face          | 9.02        | 9.51      | 12.3         | 10.14 | 10.06  | 13.96        | 20.03    | 
| Voice         | 3.2         | 3.4       | 4.11         | 3.53  | 3.52   | 4.55         | 6.17     | 
| Classifier    | 9.94        | 11.2      | 17.0         | 11.55 | 11.4   | 18.82        | 31.68    | 
| NFC           | 4.19        | 4.27      | 4.78         | 4.36  | 4.36   | 5.05         | 6.15     | 
| Combined NFC  | 7.0         | 7.53      | 10.41        | 7.58  | 7.53   | 10.99        | 17.29    | 
| Workflow (UI) | 4.32        | 4.37      | 4.76         | 4.45  | 4.43   | 4.95         | 5.77     | 
<!-- SCIENCE_SIZE_TABLE_END -->
