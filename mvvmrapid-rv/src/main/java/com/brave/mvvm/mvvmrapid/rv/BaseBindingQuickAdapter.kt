package com.brave.mvvm.mvvmrapid.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.brave.mvvmrapid.utils.GenericsHelper
import com.brave.mvvmrapid.utils.inflate
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/10/18 10:33
 *
 * ***desc***       ：支持[ViewBinding]的[BaseQuickAdapter]基类
 */
@Suppress("UNCHECKED_CAST", "unused")
abstract class BaseBindingQuickAdapter<T, Binding : ViewBinding> @JvmOverloads constructor(
    @LayoutRes
    private val layoutResId: Int = -1,
    data: MutableList<T>? = null,
) : BaseQuickAdapter<T, BaseViewHolder>(layoutResId, data) {
    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        GenericsHelper(javaClass).find<ViewBinding, Binding>()
            ?.inflate<Binding>(LayoutInflater.from(parent.context), parent, false)
            ?.also { return BaseBindingViewHolder(it) }
        return super.onCreateDefViewHolder(parent, viewType)
    }

    @Deprecated(message = "Please use the [convert(binding, item)]")
    override fun convert(holder: BaseViewHolder, item: T) {
        if (holder is BaseBindingViewHolder<*>) {
            convert(holder.binding as Binding, item)
            if (holder.binding is ViewDataBinding) {
                holder.binding.executePendingBindings()
            }
        }
    }

    @Deprecated(message = "Please use the [convert(binding, item, payloads)]")
    override fun convert(holder: BaseViewHolder, item: T, payloads: List<Any>) {
        if (holder is BaseBindingViewHolder<*>) {
            convert(holder.binding as Binding, item, payloads)
            if (holder.binding is ViewDataBinding) {
                holder.binding.executePendingBindings()
            }
        }
    }

    abstract fun convert(binding: Binding, item: T)

    protected open fun convert(binding: Binding, item: T, payloads: List<Any>) {}
}