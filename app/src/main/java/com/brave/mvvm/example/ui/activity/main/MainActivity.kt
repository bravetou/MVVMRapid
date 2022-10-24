package com.brave.mvvm.example.ui.activity.main

import android.os.Bundle
import android.text.InputFilter
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.SizeUtils
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.R
import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.ActivityMainBinding
import com.brave.mvvm.example.ui.activity.delegate.DelegateActivity
import com.brave.mvvm.example.ui.activity.mvvm.MvvmRapidActivity
import com.brave.mvvm.example.ui.adapter.FunctionAdapter
import com.brave.mvvmrapid.core.common.CommonActivity
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
class MainActivity : TransferCopyActivity() {
    override val variableId = BR.viewModel

    private val adapter by lazy {
        FunctionAdapter()
    }

    private val data by lazy {
        mutableListOf(
            FunctionBean(0, R.mipmap.icon_dot, "delegate"),
            FunctionBean(1, R.mipmap.icon_dot, "mvvmrapid")
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = adapter
        adapter.setNewInstance(data)

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            when (item.id) {
                0 -> {
                    // delegate
                    startActivity<DelegateActivity>(
                        bundle = bundleOf(
                            "from" to javaClass.simpleName
                        )
                    )
                }
                1 -> {
                    // mvvmrapid
                    startActivity<MvvmRapidActivity>(
                        bundle = bundleOf(
                            "from" to javaClass.simpleName
                        )
                    )
                }
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
            MoneyInputFilter.newInstance(2),
            InputFilter.LengthFilter(11)
        )
    }
}

abstract class TransferActivity<Binding : ViewBinding> :
    CommonActivity<Binding, MainViewModel>()

abstract class TransferCopyActivity :
    TransferActivity<ActivityMainBinding>()