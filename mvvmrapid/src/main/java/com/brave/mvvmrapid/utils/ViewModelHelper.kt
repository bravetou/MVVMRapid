package com.brave.mvvmrapid.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.brave.mvvmrapid.core.common.CommonViewModel

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/23 16:47
 *
 * ***desc***       ：[CommonViewModel]专用帮助
 */
object ViewModelHelper {
    /**
     * 通过[ViewModelProvider]初始化当前泛型类[VM]的viewModel对象
     *
     * @param cls out相当于Java中的（? extends），是设置泛型上限，表示泛型类是类或者类的子类
     */
    @JvmStatic
    @JvmOverloads
    fun <VM : CommonViewModel> getViewModelByProvider(
        owner: ViewModelStoreOwner? = null,
        key: String? = null,
        cls: Class<out VM>? = null
    ): VM {
        if (null == owner)
            throw RuntimeException("The [owner] argument cannot be empty")
        if (null == cls)
            throw RuntimeException("The [cls] argument cannot be empty")
        return if (key.isNullOrEmpty()) {
            ViewModelProvider(owner)[cls]
        } else {
            // 带key创建会让使用相同Activity或者Fragment，创建的ViewModel数据独立
            ViewModelProvider(owner)[key, cls]
        }
    }
}