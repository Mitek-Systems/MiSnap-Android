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
| Document                    | 5.04        | 5.18      | 5.94         | 5.23  | 5.3    | 6.24         | 7.9      | 
| Document and Barcode        | 6.23        | 6.42      | 8.35         | 6.49  | 6.64   | 8.83         | 12.89    | 
| Document and Face           | 11.36       | 11.95     | 15.09        | 12.54 | 12.54  | 16.86        | 23.73    | 
| Document, Barcode, and Face | 12.55       | 13.18     | 17.5         | 13.79 | 13.88  | 19.45        | 28.72    | 
| Document, Face, and NFC     | 14.37       | 15.13     | 19.68        | 15.73 | 15.82  | 21.72        | 31.57    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86  | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ---: | -----: | -----------: | -------: |
| Document      | 3.07        | 3.21      | 3.96         | 3.26 | 3.32   | 4.27         | 5.93     | 
| Barcode       | 3.8         | 3.87      | 5.37         | 3.92 | 4.03   | 5.65         | 8.73     | 
| Face          | 8.86        | 9.32      | 12.03        | 9.9  | 9.86   | 13.61        | 19.5     | 
| NFC           | 4.03        | 4.07      | 4.53         | 4.13 | 4.16   | 4.71         | 5.67     | 
| Combined NFC  | 6.11        | 6.44      | 8.59         | 6.49 | 6.64   | 9.17         | 13.81    | 
| Workflow (UI) | 4.12        | 4.14      | 4.46         | 4.17 | 4.19   | 4.58         | 5.25     | 
<!-- SCIENCE_SIZE_TABLE_END -->

