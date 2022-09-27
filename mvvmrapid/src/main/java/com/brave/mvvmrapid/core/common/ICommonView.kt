package com.brave.mvvmrapid.core.common

import android.os.Bundle

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 17:57
 *
 * ***desc***       ：ICommonView
 */
interface ICommonView {
    /**
     * 初始化开始，此处可进行一些参数处理
     */
    fun initStart()

    /**
     * 初始化系统栏（状态栏 与 导航栏）
     */
    fun initSystemBar()

    /**
     * 初始化全局通知（如：EventBus、RxBus等）
     */
    fun initGlobalBus()

    /**
     * 初始化View
     * @param savedInstanceState 如果非 null，则此（活动/片段）将从此处给出的先前保存状态重新构建
     */
    fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 初始化观察者
     */
    fun initObserver()

    /**
     * 初始化结束
     */
    fun initEnd()
}