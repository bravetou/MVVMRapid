package com.brave.mvvmrapid.core.common.ext

import androidx.databinding.ViewDataBinding
import com.brave.mvvmrapid.core.common.CommonActivity
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
abstract class CommonDataBindingActivity<Binding : ViewDataBinding, VM : CommonViewModel> :
    CommonActivity<Binding, VM>()