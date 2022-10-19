package com.brave.mvvmrapid.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ReflectUtils

/**
 * [ViewBinding]接口的实现类的`inflate`方法名
 */
private const val INFLATE = "inflate"

/**
 * 反射[ViewBinding]的`inflate`方法
 *
 * @param layoutInflater 布局服务
 * @param parent 父布局
 * @param attachToRoot 附加到父布局
 */
@JvmOverloads
fun <Binding : ViewBinding> Class<*>?.inflate(
    layoutInflater: LayoutInflater,
    parent: ViewGroup? = null,
    attachToRoot: Boolean = false,
): Binding? {
    if (null == this) return null
    val cls = this
    return when {
        ViewDataBinding::class.java.isAssignableFrom(cls) && cls != ViewDataBinding::class.java -> {
            // 如果[cls]继承至[ViewDataBinding]，并且不是[ViewDataBinding]
            ReflectUtils.reflect(this)
                .method(INFLATE, layoutInflater, parent, attachToRoot)
                .get()
        }
        ViewBinding::class.java.isAssignableFrom(cls) && cls != ViewBinding::class.java -> {
            // 如果[cls]继承至[ViewBinding]，并且不是[ViewBinding]
            ReflectUtils.reflect(this)
                .method(INFLATE, layoutInflater, parent, attachToRoot)
                .get()
        }
        else -> null // 其他情况
    }
}