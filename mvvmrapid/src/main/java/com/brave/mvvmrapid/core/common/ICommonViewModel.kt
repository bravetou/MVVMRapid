package com.brave.mvvmrapid.core.common

import androidx.lifecycle.DefaultLifecycleObserver

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 18:01
 *
 * ***desc***       ：ICommonViewModel
 */
interface ICommonViewModel : DefaultLifecycleObserver {
    // /**
    //  * 生命周期视图树[LifecycleOwner]任意方法都会执行
    //  */
    // fun onAny(owner: LifecycleOwner, event: Lifecycle.Event)
}