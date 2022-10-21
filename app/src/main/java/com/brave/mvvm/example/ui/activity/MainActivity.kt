package com.brave.mvvm.example.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.R
import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.ActivityMainBinding
import com.brave.mvvm.example.databinding.ActivityMainBindingImpl
import com.brave.mvvm.example.ui.adapter.FunctionAdapter
import com.brave.mvvm.example.ui.fragment.HomeFragment
import com.brave.mvvmrapid.core.common.CommonActivity
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
class MainActivity : TransferActivity<ActivityMainBinding>() {
    override val variableId = BR.viewModel

    private var count = 0

    private val adapter by lazy {
        FunctionAdapter()
    }

    private val data by lazy {
        mutableListOf(
            FunctionBean(0, R.mipmap.ic_launcher, "CommonActivity"),
            FunctionBean(1, R.mipmap.ic_launcher, "CommonViewBindingActivity"),
            FunctionBean(2, R.mipmap.ic_launcher, "CommonDataBindingActivity"),
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setNewInstance(data)

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

        binding.btnTestDispatcher.setOnClickListener {
            // viewModel.testDispatcher()
            supportFragmentManager.findFragmentByTag(HomeFragment.TAG)?.let { fragment ->
                supportFragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNow()
            }
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

        supportFragmentManager.beginTransaction()
            .add(binding.fragment.id, HomeFragment(), HomeFragment.TAG)
            .commitNow()
    }

    override fun onStart(text: String) {
        Log.d("MainActivity", "onStart: ================== ======> $text")
    }

    override fun onComplete() {
        Log.d("MainActivity", "onComplete: ================== ======>")
    }

    override fun onError(e: Throwable) {
        Log.d("MainActivity", "onError: ================== ======> $e")
    }
}

abstract class TransferActivity<Binding : ViewBinding> : CommonActivity<Binding, MainViewModel>()