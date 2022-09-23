package com.brave.mvvm.example

import android.os.Bundle
import com.brave.mvvm.example.databinding.ActivityMainBinding
import com.brave.mvvmrapid.core.common.CommonActivity
import com.brave.mvvmrapid.core.common.CommonViewModel

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 15:43
 *
 * ***desc***       ：Main View
 */
class MainActivity : CommonActivity<ActivityMainBinding, MainViewModel>() {
    override val variableId: Int
        get() = BR.viewModel

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initObserver() {

    }
}