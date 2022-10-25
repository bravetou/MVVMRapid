package com.brave.mvvm.example.ui.fragment.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.R
import com.brave.mvvm.example.bean.FunctionBean
import com.brave.mvvm.example.databinding.FragmentHomeBinding
import com.brave.mvvm.example.ui.adapter.FunctionAdapter
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
class HomeFragment : TransferFragment() {
    companion object {
        const val TAG = "HomeFragment"
    }

    override val variableId = BR.viewModel

    override val viewModel by lazy {
        ViewModelProvider(this)[viewModelKey, HomeViewModel::class.java]
    }

    private val adapter by lazy {
        FunctionAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        val data = mutableListOf(
            FunctionBean(0, R.mipmap.icon_dot, "协程测试"),
            FunctionBean(1, R.mipmap.icon_dot, "调度器和线程测试"),
            FunctionBean(2, R.mipmap.icon_dot, "（Unconfined、confined）调度器测试"),
            FunctionBean(3, R.mipmap.icon_dot, "在线程间切换测试"),
            FunctionBean(4, R.mipmap.icon_dot, "线程池调度器测试"),
        )
        adapter.setNewInstance(data)

        adapter.setOnItemClickListener { _, _, position ->
            val item = adapter.getItem(position)
            when (item.id) {
                0 -> viewModel.testScope()
                1 -> viewModel.testSchedulersAndThreads()
                2 -> viewModel.testSchedulersUnconfinedAndConfined()
                3 -> viewModel.testSwitchBetweenThreads()
                4 -> viewModel.testThreadPoolScheduler()
            }
        }
    }

    override fun onStart(text: String) {
        Log.d(TAG, "onStart:=== === === === === >>> $text")
    }

    override fun onError(e: Throwable) {
        Log.d(TAG, "onError:=== === === === === >>> ${e.message}")
    }

    override fun onComplete() {
        Log.d(TAG, "onComplete:=== === === === === >>>")
    }
}

abstract class TransferFragment :
    CommonViewBindingFragment<FragmentHomeBinding>()