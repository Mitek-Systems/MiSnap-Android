# MiSnap SDK v5.6.1 Download Sizes

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
| Document and Barcode             | 7.07        | 7.31      | 9.53         | 7.43  | 7.52   | 10.1         | 14.78    | 
| Document and Biometric           | 13.36       | 14.15     | 18.18        | 14.67 | 14.64  | 19.98        | 28.83    | 
| Document, Barcode, and Biometric | 14.56       | 15.4      | 20.62        | 15.94 | 16.0   | 22.6         | 33.9     | 
| Document, Biometric, and NFC     | 16.99       | 18.14     | 24.13        | 18.6  | 18.5   | 26.1         | 39.22    | 
| Document Classification          | 12.85       | 14.23     | 20.41        | 14.41 | 14.23  | 21.97        | 35.71    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.7         | 3.89      | 4.91         | 3.99  | 4.0    | 5.31         | 7.55     | 
| Barcode       | 4.33        | 4.46      | 6.13         | 4.55  | 4.61   | 6.5          | 9.96     | 
| Face          | 9.82        | 10.42     | 13.58        | 10.88 | 10.84  | 15.06        | 21.99    | 
| Voice         | 3.23        | 3.38      | 4.13         | 3.47  | 3.44   | 4.44         | 6.1      | 
| Classifier    | 10.72       | 12.09     | 18.28        | 12.28 | 12.1   | 19.84        | 33.57    | 
| NFC           | 4.25        | 4.34      | 4.93         | 4.43  | 4.41   | 5.17         | 6.43     | 
| Combined NFC  | 7.37        | 7.91      | 10.89        | 7.96  | 7.89   | 11.46        | 17.97    | 
| Workflow (UI) | 4.54        | 4.61      | 5.05         | 4.67  | 4.65   | 5.23         | 6.18     | 
<!-- SCIENCE_SIZE_TABLE_END -->
