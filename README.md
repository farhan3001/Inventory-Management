# Inventory-Management

1. Requirements:
   - Minimal SDK 29 (Android 10)
   - Storage capacity: 14Mb
   - Using external lib:
     A. Retrofit2:
      - com.squareup.retrofit2:retrofit:2.8.1
      - com.squareup.retrofit2:converter-gson:2.8.1
     B. Okhttp3:
      - com.squareup.okhttp3:logging-interceptor:4.8.1
     C. Chart:
      - com.github.PhilJay:MPAndroidChart:v3.1.0

2. App architecture:
     1. Using MVVM android architecture pattern
     2. Directories:
      - Domain: handle retrofit build interceptor as well service interfaces
      - Data: hande request and response data format
      - Activities: handle all UI related functionalities
      - Helper class: contains general function that can be use in all directories and classes

3. How to install:
     1. Go to this link: https://drive.google.com/file/d/1w_H4_hm0ZDDdim5azxpOVS6Vz9BKVHOn/view?usp=sharing
     2. Install on your device
     3. Recommended using light mode 

4. Test result: https://docs.google.com/presentation/d/1nbDu9joQKZxaKTSkfHZUL09kPL-2U5Iw2bEv3jDBtmQ/edit?usp=sharing
