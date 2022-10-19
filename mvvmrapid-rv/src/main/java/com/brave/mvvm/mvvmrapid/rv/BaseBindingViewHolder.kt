package com.brave.mvvm.mvvmrapid.rv

import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/10/18 11:49
 *
 * ***desc***       ：方便[ViewBinding]的使用
 */
open class BaseBindingViewHolder<Binding : ViewBinding>(
    val binding: Binding
) : BaseViewHolder(binding.root)