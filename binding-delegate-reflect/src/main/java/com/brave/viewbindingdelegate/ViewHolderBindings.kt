@file:JvmName("ReflectionViewHolderBindings")

package com.brave.viewbindingdelegate

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

/**
 * 创建与[ViewHolder]相关联的新[ViewBinding]
 *
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingViewHolder")
inline fun <reified Binding : ViewBinding> ViewHolder.viewBinding() = viewBinding(
    Binding::class.java
)

/**
 * 创建与[ViewHolder]相关联的新[ViewBinding]
 *
 * @param viewBindingClass 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingViewHolder")
fun <Binding : ViewBinding> ViewHolder.viewBinding(
    viewBindingClass: Class<Binding>,
): ViewBindingProperty<ViewHolder, Binding> = viewBinding {
    ViewBindingCache.getBind(viewBindingClass).bind(itemView)
}