@file:JvmName("ReflectionActivityViewBindings")

package com.brave.viewbindingdelegate

import android.app.Activity
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding

/**
 * 创建与[Activity][ComponentActivity]相关联的新[ViewBinding]
 *
 * @param Binding 期望的[ViewBinding]结果类
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的
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
 * 创建与[Activity][ComponentActivity]相关联的新[ViewBinding]
 *
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param viewBindingRootId 根视图的id，它将用作视图绑定的
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
 * 创建与[Activity][ComponentActivity]相关联的新[ViewBinding]
 *
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param rootViewProvider 来自[ViewBinding]的[Activity][this]的根视图的提供者
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
 * 创建与[Activity][ComponentActivity]相关联的新[ViewBinding]。
 * 你需要设置[ViewBinding.getRoot]作为使用[Activity.setContentView]的内容视图。
 *
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