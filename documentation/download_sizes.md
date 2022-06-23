# Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an 'out of the box' integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                    | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :-------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                    | 5.26        | 5.47      | 6.44         | 5.56  | 5.57   | 6.84         | 9.0      | 
| Document and Barcode        | 6.47        | 6.73      | 8.91         | 6.86  | 6.93   | 9.49         | 14.1     | 
| Document and Face           | 11.59       | 12.23     | 15.59        | 12.86 | 12.8   | 17.45        | 24.82    | 
| Document, Barcode, and Face | 12.79       | 13.49     | 18.05        | 14.16 | 14.17  | 20.1         | 29.93    | 
| Document, Face, and NFC     | 14.83       | 15.77     | 20.71        | 16.34 | 16.19  | 22.65        | 33.47    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86  | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ---: | -----: | -----------: | -------: |
| Document      | 3.28        | 3.49      | 4.46         | 3.57 | 3.58   | 4.86         | 7.01     | 
| Barcode       | 3.85        | 3.96      | 5.52         | 4.05 | 4.13   | 5.89         | 9.12     | 
| Face          | 8.89        | 9.38      | 12.13        | 9.99 | 9.93   | 13.78        | 19.77    | 
| NFC           | 4.12        | 4.2       | 4.69         | 4.28 | 4.3    | 4.94         | 6.0      | 
| Combined NFC  | 6.57        | 7.07      | 9.62         | 7.09 | 7.01   | 10.09        | 15.71    | 
| Workflow (UI) | 4.15        | 4.21      | 4.57         | 4.27 | 4.28   | 4.75         | 5.53     | 
<!-- SCIENCE_SIZE_TABLE_END -->