@file:JvmName("ActivityViewBindings")

package com.brave.viewbindingdelegate

import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

@RestrictTo(LIBRARY)
private class ActivityViewBindingProperty<in A : ComponentActivity, out Binding : ViewBinding>(
    onViewDestroyed: (Binding) -> Unit,
    private val viewNeedsInitialization: Boolean = true,
    viewBinder: (A) -> Binding
) : LifecycleViewBindingProperty<A, Binding>(viewBinder, onViewDestroyed) {
    override fun getLifecycleOwner(thisRef: A): LifecycleOwner {
        return thisRef
    }

    override fun isViewInitialized(thisRef: A): Boolean {
        return viewNeedsInitialization && thisRef.window != null
    }
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 */
@JvmName("viewBindingActivity")
fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> {
    return viewBinding(emptyVbCallback(), viewBinder)
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 */
@JvmName("viewBindingActivityWithCallbacks")
fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    onViewDestroyed: (Binding) -> Unit = {},
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> {
    return ActivityViewBindingProperty(onViewDestroyed, viewBinder = viewBinder)
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 */
@JvmName("viewBindingActivity")
inline fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, Binding> {
    return viewBinding(emptyVbCallback(), vbFactory, viewProvider)
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 */
@JvmName("viewBindingActivityWithCallbacks")
inline fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    noinline onViewDestroyed: (Binding) -> Unit = {},
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, Binding> {
    return viewBinding(onViewDestroyed) { activity -> vbFactory(viewProvider(activity)) }
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 */
@JvmName("viewBindingActivity")
inline fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, Binding> {
    return viewBinding(emptyVbCallback(), vbFactory, viewBindingRootId)
}

/**
 * 创建新的与[Activity][ComponentActivity]相关联的[ViewBinding]，
 * 并允许自定义[View]绑定到视图绑定的方式。
 *
 * @param vbFactory 创建[ViewBinding]的新实例的函数。可以使用***MyViewBinding::bind***
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的根
 */
@JvmName("viewBindingActivity")
inline fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    noinline onViewDestroyed: (Binding) -> Unit = {},
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, Binding> {
    return viewBinding(onViewDestroyed) { activity ->
        vbFactory(activity.requireViewByIdCompat(viewBindingRootId))
    }
}

@RestrictTo(LIBRARY_GROUP)
fun <A : ComponentActivity, Binding : ViewBinding> activityViewBinding(
    onViewDestroyed: (Binding) -> Unit,
    viewNeedsInitialization: Boolean = true,
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> {
    return ActivityViewBindingProperty(onViewDestroyed, viewNeedsInitialization, viewBinder)
}