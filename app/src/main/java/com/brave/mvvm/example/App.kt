package com.brave.mvvm.example

import com.brave.mvvmrapid.core.CommonApp
import com.brave.mvvmrapid.core.CommonConfig

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/27 14:05
 *
 * ***desc***       ：App
 */
class App : CommonApp() {
    override fun onCreate() {
        super.onCreate()
        CommonConfig.DEBUG = true
    }
}