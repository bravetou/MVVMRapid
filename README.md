# MVVMRapid

---
#### 介绍
MVVM rapid development.

---
#### 使用说明

---
1. 启用databinding
```groovy
buildFeatures {
    viewBinding true
    dataBinding true
}
```

---
2. 依赖Library

在根目录的build.gradle中加入
```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

在主项目app的build.gradle中依赖
```groovy
dependencies {
    // mvvm core
    implementation 'com.github.bravetou.MVVMRapid:mvvmrapid:$version'
    // mvvm recyclerView
    implementation 'com.github.bravetou.MVVMRapid:mvvmrapid-rv:$version'
    // no reflection viewBinding delegate
    implementation 'com.github.bravetou.MVVMRapid:binding-delegate-no-reflection:$version'
    // reflect viewBinding delegate
    implementation 'com.github.bravetou.MVVMRapid:binding-delegate-reflect:$version'
}
```
```groovy
dependencies {
    // mvvm core => eg（v1.1.3 => 1.1.3）
    implementation 'com.github.bravetou.MVVMRapid:mvvmrapid:1.1.3'
}
```

---
3. 配置Application 
- 继承[CommonApp](mvvmrapid/src/main/java/com/brave/mvvmrapid/core/CommonApp.kt)
- 自定义Application可以参照[CommonApp](mvvmrapid/src/main/java/com/brave/mvvmrapid/core/CommonApp.kt)中的方法添加到自定义Application中
