package com.brave.mvvmrapid.core

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.multidex.MultiDex
import com.blankj.utilcode.util.AppUtils
import kotlin.system.exitProcess

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 17:34
 *
 * ***desc***       ：开放的常用App
 */
open class CommonApp : Application() {
    companion object {
        @JvmStatic
        @Volatile
        lateinit var instance: CommonApp
            private set
    }


    /////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////我是一条分割线哟//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        // 防64K
        MultiDex.install(base)
    }

    /////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////我是一条分割线哟//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    override fun onCreate() {
        super.onCreate()
        initApp()
        initUtilCodeX()
    }

    /**
     * 初始化UtilCodeX
     */
    private fun initUtilCodeX() {
        // 已无需单独调用初始化方法
        // 工具已单独配置内容提供者 [com.blankj.utilcode.util.UtilsFileProvider]
        // Utils.init(this)
    }

    /**
     * 初始化app
     */
    private fun initApp() {
        instance = this
    }

    /////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////我是一条分割线哟//////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * 退出程序
     */
    open fun exitApp() {
        AppUtils.exitApp()
        Process.killProcess(Process.myPid())
        System.gc()
        exitProcess(0)
    }
}