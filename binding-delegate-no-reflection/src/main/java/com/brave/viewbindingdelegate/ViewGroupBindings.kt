package com.brave.viewbindingdelegate

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

@PublishedApi
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ViewGroupViewBindingProperty<in V : ViewGroup, out Binding : ViewBinding>(
    onViewDestroyed: (Binding) -> Unit,
    viewBinder: (V) -> Binding
) : LifecycleViewBindingProperty<V, Binding>(viewBinder, onViewDestroyed) {
    override fun getLifecycleOwner(thisRef: V): LifecycleOwner {
        return checkNotNull(ViewTreeLifecycleOwner.get(thisRef)) {
            "Fragment doesn't have a view associated with it or the view has been destroyed"
        }
    }
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    crossinline vbFactory: (ViewGroup) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> {
    return viewBinding(lifecycleAware = false, vbFactory)
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> {
    return viewBinding(lifecycleAware, vbFactory, emptyVbCallback())
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    lifecycleAware: Boolean,
    crossinline vbFactory: (ViewGroup) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> {
    return when {
        isInEditMode -> EagerViewBindingProperty(vbFactory(this))
        lifecycleAware -> ViewGroupViewBindingProperty(onViewDestroyed) { viewGroup ->
            vbFactory(
                viewGroup
            )
        }
        else -> LazyViewBindingProperty(onViewDestroyed) { viewGroup -> vbFactory(viewGroup) }
    }
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    crossinline vbFactory: (View) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> {
    return viewBinding(viewBindingRootId, lifecycleAware = false, vbFactory, onViewDestroyed)
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> Binding,
): ViewBindingProperty<ViewGroup, Binding> {
    return viewBinding(viewBindingRootId, lifecycleAware, vbFactory, emptyVbCallback())
}

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
 */
inline fun <Binding : ViewBinding> ViewGroup.viewBinding(
    @IdRes viewBindingRootId: Int,
    lifecycleAware: Boolean,
    crossinline vbFactory: (View) -> Binding,
    noinline onViewDestroyed: (Binding) -> Unit,
): ViewBindingProperty<ViewGroup, Binding> {
    return when {
        isInEditMode -> EagerViewBindingProperty(vbFactory(this))
        lifecycleAware -> ViewGroupViewBindingProperty(onViewDestroyed) { viewGroup ->
            vbFactory(
                viewGroup
            )
        }
        else -> LazyViewBindingProperty(onViewDestroyed) { viewGroup: ViewGroup ->
            vbFactory(viewGroup.requireViewByIdCompat(viewBindingRootId))
        }
    }
}