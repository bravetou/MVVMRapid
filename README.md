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
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

在主项目app的build.gradle中依赖
```groovy
	dependencies {
        // Tag uses the distribution you need    
        implementation 'com.gitee.bravetou:mvvm-rapid:Tag'
	}
```

---
3. 配置Application 
- 继承[CommonApp](mvvmrapid/src/main/java/com/brave/mvvmrapid/core/CommonApp.kt)
- 自定义Application可以参照[CommonApp](mvvmrapid/src/main/java/com/brave/mvvmrapid/core/CommonApp.kt)中的方法添加到自定义Application中
