package com.brave.mvvm.example.ui.fragment

import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.brave.mvvm.example.App
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.databinding.FragmentHomeBinding
import com.brave.mvvmrapid.core.common.ext.CommonViewBindingFragment

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/29 16:59
 *
 * ***desc***       ：Home Fragment
 */
class HomeFragment : CommonViewBindingFragment<FragmentHomeBinding>() {
    companion object {
        const val TAG = "HomeFragment"
    }

    override val isImplUsingClass: Boolean
        get() = false

    override val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override val variableId = BR.viewModel

    override val viewModel = HomeViewModel(App.instance)

    private var count = 0

    override fun initView(savedInstanceState: Bundle?) {
        binding.tvHello.setOnClickListener {
            val text = when (++count % 3) {
                0 -> "hello world! => 0"
                1 -> "hello world! => 1"
                2 -> "hello world! => 2"
                else -> "hello world! => 意外"
            }
            viewModel.helloWorld.value = text
            ToastUtils.showLong(text)
        }
    }
}