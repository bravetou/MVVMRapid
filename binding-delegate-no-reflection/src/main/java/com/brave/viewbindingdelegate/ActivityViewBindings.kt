@file:JvmName("ActivityViewBindings")
@file:Suppress("unused")

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
    override fun getLifecycleOwner(thisRef: A): LifecycleOwner =
        thisRef

    override fun isViewInitialized(thisRef: A): Boolean = if (!viewNeedsInitialization) {
        true
    } else thisRef.window != null
}

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param viewBinder 视图绑定函数
 */
@JvmName("viewBindingActivity")
fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> = viewBinding(
    emptyVbCallback(),
    viewBinder
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param viewBinder 视图绑定函数
 * @param onViewDestroyed 视图销毁回调函数
 */
@JvmName("viewBindingActivityWithCallbacks")
fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    onViewDestroyed: (Binding) -> Unit = {},
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> = ActivityViewBindingProperty(
    onViewDestroyed,
    viewBinder = viewBinder
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewProvider [View]提供者。此处默认使用[Activity][android.app.Activity]中的根[View]
 */
@JvmName("viewBindingActivity")
inline fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, Binding> = viewBinding(
    emptyVbCallback(),
    vbFactory,
    viewProvider
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewProvider [View]提供者。此处默认使用[Activity][android.app.Activity]中的根[View]
 */
@JvmName("viewBindingActivityWithCallbacks")
inline fun <A : ComponentActivity, Binding : ViewBinding> ComponentActivity.viewBinding(
    noinline onViewDestroyed: (Binding) -> Unit = {},
    crossinline vbFactory: (View) -> Binding,
    crossinline viewProvider: (A) -> View = ::findRootView
): ViewBindingProperty<A, Binding> = viewBinding(
    onViewDestroyed
) { activity ->
    vbFactory(viewProvider(activity))
}

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewBindingRootId [View] ID
 */
@JvmName("viewBindingActivity")
inline fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, Binding> = viewBinding(
    emptyVbCallback(),
    vbFactory,
    viewBindingRootId
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param vbFactory 创建[ViewBinding]的函数。可以直接使用*MyViewBinding::bind*
 * @param viewBindingRootId [View] ID
 */
@JvmName("viewBindingActivity")
inline fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    noinline onViewDestroyed: (Binding) -> Unit = {},
    crossinline vbFactory: (View) -> Binding,
    @IdRes viewBindingRootId: Int
): ViewBindingProperty<ComponentActivity, Binding> = viewBinding(
    onViewDestroyed
) { activity ->
    vbFactory(activity.requireViewByIdCompat(viewBindingRootId))
}

@RestrictTo(LIBRARY_GROUP)
fun <A : ComponentActivity, Binding : ViewBinding> activityViewBinding(
    onViewDestroyed: (Binding) -> Unit,
    viewNeedsInitialization: Boolean = true,
    viewBinder: (A) -> Binding
): ViewBindingProperty<A, Binding> = ActivityViewBindingProperty(
    onViewDestroyed,
    viewNeedsInitialization,
    viewBinder
)