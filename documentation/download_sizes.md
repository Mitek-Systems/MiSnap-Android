# MiSnap SDK v5.3.3 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 5.44        | 5.65      | 6.67         | 5.75  | 5.75   | 7.08         | 9.34     | 
| Document and Barcode             | 6.65        | 6.91      | 9.13         | 7.04  | 7.11   | 9.73         | 14.44    | 
| Document and Biometric           | 12.59       | 13.38     | 17.12        | 14.08 | 14.02  | 19.25        | 27.53    | 
| Document, Barcode, and Biometric | 13.8        | 14.64     | 19.58        | 15.37 | 15.38  | 21.9         | 32.61    | 
| Document, Biometric, and NFC     | 15.85       | 16.93     | 22.26        | 17.57 | 17.43  | 24.46        | 36.19    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 3.4         | 3.61      | 4.63         | 3.71  | 3.71   | 5.04         | 7.3      | 
| Barcode       | 3.93        | 4.04      | 5.62         | 4.14  | 4.22   | 5.99         | 9.25     | 
| Face          | 8.97        | 9.47      | 12.23        | 10.07 | 10.02  | 13.89        | 19.91    | 
| Voice         | 3.16        | 3.36      | 4.04         | 3.46  | 3.48   | 4.47         | 6.05     | 
| NFC           | 4.14        | 4.22      | 4.71         | 4.3   | 4.32   | 4.97         | 6.03     | 
| Combined NFC  | 6.69        | 7.2       | 9.8          | 7.23  | 7.15   | 10.29        | 16.01    | 
| Workflow (UI) | 4.27        | 4.32      | 4.68         | 4.38  | 4.39   | 4.86         | 5.64     | 
<!-- SCIENCE_SIZE_TABLE_END -->
