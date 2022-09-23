package com.brave.mvvmrapid.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 16:47
 *
 * ***desc***       ：[ViewBinding]专用帮助
 */
object BindingHelper {
    /**
     * 通过反射初始化当前泛型类[Binding]对象
     * @param cls out相当于Java中的（? extends），是设置泛型上限，表示泛型类是类或者类的子类
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <Binding : ViewBinding> getBindingByClass(
        layoutInflater: LayoutInflater? = null,
        cls: Class<out Binding>? = null,
    ): Binding {
        if (null == layoutInflater)
            throw RuntimeException("The [layoutInflater] argument cannot be empty")
        if (null == cls)
            throw RuntimeException("The [cls] argument cannot be empty")
        return when {
            ViewDataBinding::class.java.isAssignableFrom(cls) && cls != ViewDataBinding::class.java -> {
                // 如果[cls]继承至[ViewDataBinding]，并且不是[ViewDataBinding]
                val method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
                method.invoke(null, layoutInflater) as Binding
            }
            ViewBinding::class.java.isAssignableFrom(cls) && cls != ViewBinding::class.java -> {
                // 如果[cls]继承至[ViewBinding]，并且不是[ViewBinding]
                val method = cls.getDeclaredMethod("inflate", LayoutInflater::class.java)
                method.invoke(null, layoutInflater) as Binding
            }
            else -> {
                // 其他情况
                throw RuntimeException("[cls] does not belong to [ViewDataBinding] and does not belong to [ViewBinding].")
            }
        }
    }

    /**
     * 通过[layoutId]初始化当前泛型类[Binding]对象
     */
    @JvmStatic
    @JvmOverloads
    @Suppress("UNCHECKED_CAST")
    fun <Binding : ViewDataBinding> getBindingByLayoutId(
        layoutInflater: LayoutInflater? = null,
        layoutId: Int? = null,
        container: ViewGroup? = null,
        attachToParent: Boolean = false
    ): Binding {
        if (null == layoutInflater)
            throw RuntimeException("The [layoutInflater] argument cannot be empty")
        if (null == layoutId)
            throw RuntimeException("The [layoutId] argument cannot be empty")
        return DataBindingUtil.inflate(layoutInflater, layoutId, container, attachToParent)
    }
}