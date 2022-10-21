@file:JvmName("ReflectionActivityViewBindings")

package com.brave.viewbindingdelegate

import android.app.Activity
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingRootId [View] ID
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingActivity")
inline fun <reified Binding : ViewBinding> ComponentActivity.viewBinding(
    @IdRes viewBindingRootId: Int,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, Binding> = viewBinding(
    Binding::class.java,
    viewBindingRootId,
    onViewDestroyed
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingRootId [View] ID
 * @param viewBindingClass 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingActivity")
fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<Binding>,
    @IdRes viewBindingRootId: Int,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, Binding> = viewBinding(
    onViewDestroyed
) { activity ->
    val rootView = ActivityCompat.requireViewById<View>(
        activity,
        viewBindingRootId
    )
    ViewBindingCache.getBind(
        viewBindingClass
    ).bind(rootView)
}

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param rootViewProvider [View]提供者
 */
@JvmName("viewBindingActivity")
fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<Binding>,
    rootViewProvider: (ComponentActivity) -> View,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, Binding> = viewBinding(
    onViewDestroyed
) { activity ->
    ViewBindingCache.getBind(
        viewBindingClass
    ).bind(rootViewProvider(activity))
}

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]，
 * 需要设置[root][ViewBinding.getRoot]作为[setContentView][Activity.setContentView]的内容视图。
 * @param onViewDestroyed 视图销毁回调函数
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("inflateViewBindingActivity")
inline fun <reified Binding : ViewBinding> ComponentActivity.viewBinding(
    createMethod: CreateMethod = CreateMethod.BIND,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
) = viewBinding(
    Binding::class.java,
    createMethod,
    onViewDestroyed
)

/**
 * 创建一个与[活动][ComponentActivity]关联的[ViewBinding]，
 * 需要设置[root][ViewBinding.getRoot]作为[setContentView][Activity.setContentView]的内容视图。
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("inflateViewBindingActivity")
fun <Binding : ViewBinding> ComponentActivity.viewBinding(
    viewBindingClass: Class<Binding>,
    createMethod: CreateMethod = CreateMethod.BIND,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ComponentActivity, Binding> = when (createMethod) {
    CreateMethod.BIND -> viewBinding(
        viewBindingClass,
        ::findRootView,
        onViewDestroyed
    )
    CreateMethod.INFLATE -> {
        activityViewBinding(
            onViewDestroyed,
            viewNeedsInitialization = false
        ) {
            ViewBindingCache.getInflateWithLayoutInflater(
                viewBindingClass
            ).inflate(
                layoutInflater,
                null,
                false
            )
        }
    }
}