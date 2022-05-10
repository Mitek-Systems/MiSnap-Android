# Download Sizes

The following tables provide the APK download size for various SDK configurations.  These values represent
the maximum size increase, however due to potential shared dependencies the actual increase in size may
be lower.  To avoid bundling redundant architectures, see [the faq](../README.md#how-can-i-reduce-the-size-of-the-sdk).
All sizes are in MiB.

### **Common Integration Sizes**
These sizes include the UI and represent an 'out of the box' integration.
<!-- USECASE_SIZE_TABLE_START -->
| Use Case                    | armeabi_v7a | arm64_v8a | All Arm ABIs | x86   | x86_64 | All x86 ABIs | All ABIs | 
| :-------------------------- | ----------: | --------: | -----------: | ----: | -----: | -----------: | -------: |
| Document                    | 5.0         | 5.14      | 5.89         | 5.19  | 5.26   | 6.2          | 7.85     | 
| Document and Barcode        | 6.18        | 6.38      | 8.31         | 6.44  | 6.59   | 8.79         | 12.85    | 
| Document and Face           | 11.33       | 11.9      | 15.05        | 12.5  | 12.51  | 16.83        | 23.69    | 
| Document, Barcode, and Face | 12.51       | 13.14     | 17.47        | 13.76 | 13.84  | 19.41        | 28.68    | 
| Document, Face, and NFC     | 14.32       | 15.09     | 19.63        | 15.69 | 15.77  | 21.68        | 31.53    | 
<!-- USECASE_SIZE_TABLE_END -->

### **Feature Sizes**
UI size listed separately from other features.
<!-- SCIENCE_SIZE_TABLE_START -->
| Feature       | armeabi_v7a | arm64_v8a | All Arm ABIs | x86  | x86_64 | All x86 ABIs | All ABIs | 
| :------------ | ----------: | --------: | -----------: | ---: | -----: | -----------: | -------: |
| Document      | 3.05        | 3.19      | 3.95         | 3.24 | 3.31   | 4.26         | 5.91     | 
| Barcode       | 3.79        | 3.86      | 5.37         | 3.92 | 4.02   | 5.65         | 8.73     | 
| Face          | 8.85        | 9.31      | 12.02        | 9.89 | 9.85   | 13.6         | 19.49    | 
| NFC           | 4.03        | 4.06      | 4.52         | 4.12 | 4.15   | 4.71         | 5.66     | 
| Combined NFC  | 6.09        | 6.43      | 8.58         | 6.48 | 6.62   | 9.16         | 13.79    | 
| Workflow (UI) | 5.21        | 5.21      | 5.21         | 5.21 | 5.21   | 5.21         | 5.21     | 
<!-- SCIENCE_SIZE_TABLE_END -->

