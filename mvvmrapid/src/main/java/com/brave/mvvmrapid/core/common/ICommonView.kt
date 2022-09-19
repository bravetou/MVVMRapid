package com.brave.mvvmrapid.core.common

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
     * 初始化界面传递参数
     */
    fun initParam()

    /**
     * 初始化系统栏（状态栏 与 导航栏）
     */
    fun initSystemBar()

    /**
     * 初始化View
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 初始化观察者
     */
    fun initObserver()
}