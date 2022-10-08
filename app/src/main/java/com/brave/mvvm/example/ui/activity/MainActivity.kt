package com.brave.mvvm.example.ui.activity

import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.databinding.ActivityMainBinding
import com.brave.mvvm.example.ui.fragment.HomeFragment
import com.brave.mvvmrapid.core.common.ext.CommonDataBindingActivity
import com.brave.mvvmrapid.core.filter.MoneyInputFilter
import com.brave.mvvmrapid.utils.drawBackground

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 15:43
 *
 * ***desc***       ：Main View
 */
class MainActivity : CommonDataBindingActivity<ActivityMainBinding, MainViewModel>() {
    override val variableId = BR.viewModel

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
        binding.btnTest.setOnClickListener {
            viewModel.testScope()
        }
        binding.etMoney.drawBackground()
            .isDrawBackground(true)
            .setBackgroundColorRes(android.R.color.transparent)
            .setBorderColorStr("#9450F0")
            .setBorderWidth(SizeUtils.dp2px(1f))
            .setRoundedCorners(SizeUtils.dp2px(24f))
            .build()
        binding.etMoney.filters = arrayOf(
            MoneyInputFilter.newInstance(2)
        )

        viewModel.defUI.onStart.observe(this) {
            Log.d("MainActivity", "onStart: ================== ======>")
        }
        viewModel.defUI.onError.observe(this) {
            Log.d("MainActivity", "onError: ================== ======>")
        }
        viewModel.defUI.onComplete.observe(this) {
            Log.d("MainActivity", "onComplete: ================== ======>")
        }

        supportFragmentManager.beginTransaction()
            .add(binding.fragment.id, HomeFragment(), HomeFragment.TAG)
            .commitNow()
    }
}