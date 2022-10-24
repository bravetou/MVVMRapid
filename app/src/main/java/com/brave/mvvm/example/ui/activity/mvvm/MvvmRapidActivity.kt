package com.brave.mvvm.example.ui.activity.mvvm

import android.os.Bundle
import com.brave.mvvm.example.BR
import com.brave.mvvm.example.databinding.ActivityMvvmRapidBinding
import com.brave.mvvm.example.ui.fragment.home.HomeFragment
import com.brave.mvvmrapid.core.common.CommonActivity

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/10/24 14:52
 *
 * ***desc***       ：MvvmRapid View
 */
class MvvmRapidActivity : CommonActivity<ActivityMvvmRapidBinding, MvvmRapidViewModel>() {
    override val variableId: Int
        get() = BR.viewModel

    override fun initView(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .add(binding.fragment.id, HomeFragment(), HomeFragment.TAG)
            .commitNow()
    }
}