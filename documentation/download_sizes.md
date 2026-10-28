# MiSnap SDK v5.13.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.62        | 6.84      | 8.09         | 6.93  | 6.92   | 8.47         | 11.17    |
| Document and Barcode             | 7.72        | 8.08      | 10.4         | 8.28  | 8.23   | 11.11        | 16.1     |
| Document and Biometric           | 14.04       | 14.85     | 19.1         | 15.36 | 15.32  | 20.89        | 30.22    |
| Document, Barcode, and Biometric | 15.14       | 16.1      | 21.43        | 16.71 | 16.64  | 23.53        | 35.14    |
| Document, Biometric, and NFC     | 18.71       | 19.89     | 26.1         | 20.34 | 20.22  | 28.05        | 41.64    |
| Document Classification          | 13.53       | 14.94     | 21.34        | 15.11 | 14.91  | 22.89        | 37.09    |
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs |
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.4         | 4.61      | 5.86         | 4.71  | 4.69   | 6.24         | 8.95     |
| Barcode       | 4.9         | 5.16      | 6.9          | 5.33  | 5.27   | 7.44         | 11.19    |
| Face          | 10.5        | 11.14     | 14.5         | 11.59 | 11.54  | 16.0         | 23.36    |
| Voice         | 3.22        | 3.37      | 4.15         | 3.46  | 3.42   | 4.44         | 6.14     |
| Classifier    | 11.4        | 12.81     | 19.21        | 12.98 | 12.78  | 20.76        | 34.96    |
| NFC           | 5.23        | 5.32      | 5.93         | 5.41  | 5.38   | 6.16         | 7.46     |
| Combined NFC  | 8.99        | 9.57      | 12.76        | 9.59  | 9.51   | 13.31        | 20.29    |
| Workflow (UI) | 4.56        | 4.63      | 5.09         | 4.69  | 4.66   | 5.25         | 6.24     |
<!-- SCIENCE_SIZE_TABLE_END -->