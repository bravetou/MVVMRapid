package com.brave.mvvm.example

import android.os.Bundle
import com.brave.mvvm.example.databinding.ActivityMainBinding
import com.brave.mvvm.example.databinding.ActivityTestBinding
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

    var count = 0

    override fun initView(savedInstanceState: Bundle?) {
        binding.tvHello.setOnClickListener {
            viewModel.helloWorld.value = when (++count % 3) {
                0 -> "hello world! => 0"
                1 -> "hello world! => 1"
                2 -> "hello world! => 2"
                else -> "hello world! => 意外"
            }
        }
    }
}