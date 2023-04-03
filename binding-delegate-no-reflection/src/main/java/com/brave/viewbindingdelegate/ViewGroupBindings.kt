package com.brave.viewbindingdelegate

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

@PublishedApi
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ViewGroupViewBindingProperty<in V : ViewGroup, out Binding : ViewBinding>(
    onViewDestroyed: (Binding) -> Unit, viewBinder: (V) -> Binding
) : LifecycleViewBindingProperty<V, Binding>(viewBinder, onViewDestroyed) {
    override fun getLifecycleOwner(thisRef: V): LifecycleOwner =
        checkNotNull(thisRef.findViewTreeLifecycleOwner()) {
            "Fragment doesn't have a view associated with it or the view has been destroyed"
        }
}

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    crossinline vbFactory: (ViewGroup) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    lifecycleAware = false, vbFactory
)

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param lifecycleAware 从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    lifecycleAware, vbFactory, emptyVbCallback()
)

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param lifecycleAware 从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 * @param onViewDestroyed 视图销毁回调函数
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> = when {
    isInEditMode -> EagerViewBindingProperty(
        vbFactory(this)
    )
    lifecycleAware -> ViewGroupViewBindingProperty(
        onViewDestroyed
    ) { viewGroup ->
        vbFactory(viewGroup)
    }
    else -> LazyViewBindingProperty(
        onViewDestroyed
    ) { viewGroup ->
        vbFactory(viewGroup)
    }
}

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param onViewDestroyed 视图销毁回调函数
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes
    viewBindingRootId: Int,
    crossinline vbFactory: (View) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    viewBindingRootId, lifecycleAware = false, vbFactory, onViewDestroyed
)

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param lifecycleAware 从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 * @param viewBindingRootId [View] ID
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes
    viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    viewBindingRootId, lifecycleAware, vbFactory, emptyVbCallback()
)

/**
 * 创建一个与[ViewGroup]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param lifecycleAware 从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 * @param viewBindingRootId [View] ID
 * @param onViewDestroyed 视图销毁回调函数
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes
    viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> = when {
    isInEditMode -> EagerViewBindingProperty(
        vbFactory(this)
    )
    lifecycleAware -> ViewGroupViewBindingProperty(
        onViewDestroyed
    ) { viewGroup ->
        vbFactory(viewGroup)
    }
    else -> LazyViewBindingProperty(
        onViewDestroyed
    ) { viewGroup: ViewGroup ->
        vbFactory(viewGroup.requireViewByIdCompat(viewBindingRootId))
    }
}