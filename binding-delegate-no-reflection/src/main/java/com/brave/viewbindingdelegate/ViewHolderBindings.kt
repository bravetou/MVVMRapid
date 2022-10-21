@file:JvmName("ViewHolderBindings")
@file:Suppress("unused")

package com.brave.viewbindingdelegate

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * 创建一个与[ViewHolder]关联的[ViewBinding]
 * @param viewBinder 视图绑定函数
 */
fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(
    viewBinder: (VH) -> Binding
): ViewBindingProperty<VH, Binding> = LazyViewBindingProperty(
    viewBinder
)

/**
 * 创建一个与[ViewHolder]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewProvider [View]提供者。此处默认使用[ViewHolder][ViewHolder]中的根[View][ViewHolder.itemView]
 */
inline fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (VH) -> View = ViewHolder::itemView,
): ViewBindingProperty<VH, Binding> = LazyViewBindingProperty { viewHolder: VH ->
    viewProvider(viewHolder).let(vbFactory)
}

/**
 * 创建一个与[ViewHolder]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewBindingRootId [View] ID
 */
inline fun <VH : ViewHolder, Binding : ViewBinding> VH.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int,
): ViewBindingProperty<VH, Binding> = LazyViewBindingProperty { viewHolder: VH ->
    vbFactory(viewHolder.itemView.requireViewByIdCompat(viewBindingRootId))
}