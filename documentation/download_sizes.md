# MiSnap SDK v5.12.0 Download Sizes

The following tables provide the APK download size for various SDK configurations. These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower. To avoid bundling redundant architectures, see [the FAQ](../README.md#how-can-i-reduce-the-size-of-my-application).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an "out of the box" integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                         | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                         | 6.61        | 6.83      | 8.08         | 6.92  | 6.91   | 8.46         | 11.17    | 
| Document and Barcode             | 9.27        | 10.01     | 13.88        | 9.62  | 10.03  | 14.25        | 22.74    | 
| Document and Biometric           | 14.03       | 14.85     | 19.1         | 15.36 | 15.31  | 20.89        | 30.2     | 
| Document, Barcode, and Biometric | 16.69       | 18.02     | 24.9         | 18.06 | 18.43  | 26.68        | 41.77    | 
| Document, Biometric, and NFC     | 19.35       | 20.52     | 26.72        | 20.97 | 20.85  | 28.68        | 42.26    | 
| Document Classification          | 13.53       | 14.93     | 21.32        | 15.1  | 14.9   | 22.87        | 37.07    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document      | 4.39        | 4.61      | 5.85         | 4.7   | 4.69   | 6.24         | 8.94     | 
| Barcode       | 6.46        | 7.09      | 10.39        | 6.69  | 7.08   | 10.6         | 17.83    | 
| Face          | 10.49       | 11.13     | 14.49        | 11.59 | 11.53  | 15.99        | 23.35    | 
| Voice         | 3.22        | 3.37      | 4.15         | 3.46  | 3.42   | 4.44         | 6.14     | 
| Classifier    | 11.4        | 12.8      | 19.2         | 12.98 | 12.78  | 20.76        | 34.95    | 
| NFC           | 5.88        | 5.97      | 6.57         | 6.05  | 6.02   | 6.8          | 8.1      | 
| Combined NFC  | 9.64        | 10.22     | 13.41        | 10.25 | 10.16  | 13.97        | 20.94    | 
| Workflow (UI) | 4.55        | 4.62      | 5.08         | 4.68  | 4.65   | 5.24         | 6.23     | 
<!-- SCIENCE_SIZE_TABLE_END -->