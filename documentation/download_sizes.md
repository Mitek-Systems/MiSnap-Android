# MiSnap SDK v5.1.1 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.33        | 5.53      | 6.51         | 5.63  | 5.63   | 6.91         | 9.07     | 
| Document and Barcode             | 6.53        | 6.79      | 8.97         | 6.92  | 6.99   | 9.56         | 14.17    | 
| Document and Biometric           | 12.48       | 13.27     | 16.96        | 13.96 | 13.91  | 19.07        | 27.25    | 
| Document, Barcode, and Biometric | 13.69       | 14.53     | 19.43        | 15.25 | 15.27  | 21.72        | 32.35    | 
| Document, Biometric, and NFC     | 15.73       | 16.81     | 22.08        | 17.43 | 17.29  | 24.27        | 35.9     | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86  | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ---: | -----: | -----------: | -------: |
| Document      | 3.29        | 3.49      | 4.47         | 3.59 | 3.59   | 4.87         | 7.02     | 
| Barcode       | 3.86        | 3.97      | 5.53         | 4.06 | 4.14   | 5.9          | 9.13     | 
| Face          | 8.9         | 9.39      | 12.14        | 9.99 | 9.94   | 13.78        | 19.78    | 
| Voice         | 3.14        | 3.34      | 4.03         | 3.45 | 3.47   | 4.46         | 6.03     | 
| NFC           | 4.13        | 4.21      | 4.7          | 4.29 | 4.31   | 4.95         | 6.01     | 
| Combined NFC  | 6.57        | 7.07      | 9.63         | 7.1  | 7.01   | 10.1         | 15.71    | 
| Workflow (UI) | 4.25        | 4.3       | 4.67         | 4.36 | 4.37   | 4.85         | 5.62     | 
<!-- SCIENCE_SIZE_TABLE_END -->
