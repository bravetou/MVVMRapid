@file:JvmName("ReflectionFragmentViewBindings")

package com.brave.viewbindingdelegate

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * 创建一个与[Fragment][Fragment]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingRootId [View] ID
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingFragment")
inline fun <reified Binding : ViewBinding> Fragment.viewBinding(
    @IdRes viewBindingRootId: Int,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<Fragment, Binding> = viewBinding(
    Binding::class.java,
    viewBindingRootId,
    onViewDestroyed
)

/**
 * 创建一个与[Fragment][Fragment]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingRootId [View] ID
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingFragment")
fun <Binding : ViewBinding> Fragment.viewBinding(
    viewBindingClass: Class<Binding>,
    @IdRes viewBindingRootId: Int,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<Fragment, Binding> = when (this) {
    is DialogFragment -> viewBinding(
        { dialogFragment ->
            require(dialogFragment is DialogFragment)
            ViewBindingCache.getBind(
                viewBindingClass
            ).bind(dialogFragment.getRootView(viewBindingRootId))
        },
        onViewDestroyed
    )
    else -> viewBinding(
        {
            ViewBindingCache.getBind(
                viewBindingClass
            ).bind(requireView().requireViewByIdCompat(viewBindingRootId))
        },
        onViewDestroyed
    )
}

/**
 * 创建一个与[Fragment][Fragment]关联的[ViewBinding]，
 * 需要设置[root][ViewBinding.getRoot]作为[onCreateView][Fragment.onCreateView]的内容视图。
 * @param onViewDestroyed 视图销毁回调函数
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingFragment")
inline fun <reified Binding : ViewBinding> Fragment.viewBinding(
    createMethod: CreateMethod = CreateMethod.BIND,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<Fragment, Binding> = viewBinding(
    Binding::class.java,
    createMethod,
    onViewDestroyed
)

/**
 * 创建一个与[Fragment][Fragment]关联的[ViewBinding]，
 * 需要设置[root][ViewBinding.getRoot]作为[onCreateView][Fragment.onCreateView]的内容视图。
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param Binding 期望的[ViewBinding]结果类
 */
@JvmName("viewBindingFragment")
fun <Binding : ViewBinding> Fragment.viewBinding(
    viewBindingClass: Class<Binding>,
    createMethod: CreateMethod = CreateMethod.BIND,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<Fragment, Binding> = when (createMethod) {
    CreateMethod.BIND -> viewBinding(
        {
            ViewBindingCache.getBind(
                viewBindingClass
            ).bind(requireView())
        },
        onViewDestroyed
    )
    CreateMethod.INFLATE -> when (this) {
        is DialogFragment -> dialogFragmentViewBinding(
            onViewDestroyed,
            {
                ViewBindingCache.getInflateWithLayoutInflater(
                    viewBindingClass
                ).inflate(
                    layoutInflater,
                    null,
                    false
                )
            },
            viewNeedsInitialization = false
        )
        else -> fragmentViewBinding(
            onViewDestroyed,
            {
                ViewBindingCache.getInflateWithLayoutInflater(
                    viewBindingClass
                ).inflate(
                    layoutInflater,
                    null,
                    false
                )
            },
            viewNeedsInitialization = false
        )
    }
}