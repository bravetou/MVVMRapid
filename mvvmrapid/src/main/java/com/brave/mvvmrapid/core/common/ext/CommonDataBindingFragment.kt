package com.brave.mvvmrapid.core.common.ext

import androidx.databinding.ViewDataBinding
import com.brave.mvvmrapid.core.common.CommonFragment
import com.brave.mvvmrapid.core.common.CommonViewModel

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/29 10:56
 *
 * ***desc***       ：[ViewDataBinding]专用
 */
abstract class CommonDataBindingFragment<Binding : ViewDataBinding, VM : CommonViewModel> :
    CommonFragment<Binding, VM>()