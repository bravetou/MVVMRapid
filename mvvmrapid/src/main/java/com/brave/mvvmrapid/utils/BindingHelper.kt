package com.brave.mvvmrapid.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 16:47
 *
 * ***desc***       ：[ViewDataBinding]专用帮助
 */
object BindingHelper {
    /**
     * 通过反射初始化当前泛型类[V]的binding对象
     *
     * @param cls out相当于Java中的（? extends），是设置泛型上限，表示泛型类是类或者类的子类
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <V : ViewDataBinding> getBindingByClass(
        layoutInflater: LayoutInflater? = null,
        cls: Class<out V>? = null
    ): V {
        if (null == layoutInflater)
            throw RuntimeException("The [layoutInflater] argument cannot be empty")
        if (null == cls)
            throw RuntimeException("The [cls] argument cannot be empty")
        val method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as V
    }

    /**
     * 通过[layoutId]当前泛型类[V]的binding对象
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <V : ViewDataBinding> getBindingByLayoutId(
        layoutInflater: LayoutInflater? = null,
        layoutId: Int? = null,
        container: ViewGroup? = null,
        attachToParent: Boolean = false
    ): V {
        if (null == layoutInflater)
            throw RuntimeException("The [layoutInflater] argument cannot be empty")
        if (null == layoutId)
            throw RuntimeException("The [layoutId] argument cannot be empty")
        return DataBindingUtil.inflate(layoutInflater, layoutId, container, attachToParent)
    }
}