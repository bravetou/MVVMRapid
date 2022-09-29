package com.brave.mvvmrapid.core.common.ext

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.BR
import com.brave.mvvmrapid.core.common.CommonFragment
import com.brave.mvvmrapid.core.common.CommonViewModel

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/29 10:56
 *
 * ***desc***       ：[ViewBinding]专用
 *
 * 使用此类时，不要重写[variableId]方法，否则实现`setVariable`方法后会类型转换异常。
 *
 * 如有特殊需求可同时实现[variableId]与[viewModel]方法，但需与您的布局文件对应.
 */
abstract class CommonViewBindingFragment<Binding : ViewBinding> :
    CommonFragment<Binding, CommonViewModel>() {
    override val variableId: Int
        get() = BR._all

    override val viewModel by lazy {
        ViewModelProvider(this)[viewModelKey, CommonViewModel::class.java]
    }
}