@file:JvmName("ViewHolderBindings")

package com.brave.viewbindingdelegate

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * 创建与[ViewHolder]相关联的新[ViewBinding]
 */
fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(viewBinder: (VH) -> Binding): ViewBindingProperty<VH, Binding> {
    return LazyViewBindingProperty(viewBinder)
}

/**
 * 创建与[ViewHolder]相关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewProvider 从[ViewHolder]中提供一个视图。默认情况下调用[ViewHolder.itemView]
 */
inline fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (VH) -> View = ViewHolder::itemView,
): ViewBindingProperty<VH, Binding> {
    return LazyViewBindingProperty { viewHolder: VH -> viewProvider(viewHolder).let(vbFactory) }
}

/**
 * 创建与[ViewHolder]相关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 */
inline fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int,
): ViewBindingProperty<VH, Binding> {
    return LazyViewBindingProperty { viewHolder: VH ->
        vbFactory(viewHolder.itemView.requireViewByIdCompat(viewBindingRootId))
    }
}