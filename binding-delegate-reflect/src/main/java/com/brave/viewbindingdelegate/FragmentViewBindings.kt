@file:JvmName("ReflectionFragmentViewBindings")

package com.brave.viewbindingdelegate

import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

@JvmName("viewBindingFragment")
inline fun <reified Binding : ViewBinding> Fragment.viewBinding(
    @IdRes viewBindingRootId: Int,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<Fragment, Binding> = viewBinding(
    Binding::class.java,
    viewBindingRootId,
    onViewDestroyed
)

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
 * 创建与[Fragment][Fragment]相关联的新[ViewBinding]
 *
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
 * 创建与[Fragment][Fragment]相关联的新[ViewBinding]
 *
 * @param viewBindingClass 期望的[ViewBinding]结果类
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