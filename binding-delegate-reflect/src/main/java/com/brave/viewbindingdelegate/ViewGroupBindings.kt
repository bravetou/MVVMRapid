@file:JvmName("ReflectionViewGroupBindings")

package com.brave.viewbindingdelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * 创建一个与[ViewGroup][ViewGroup]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param Binding 期望的[ViewBinding]结果类
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 */
@JvmName("viewBindingFragment")
inline fun <reified Binding : ViewBinding> ViewGroup.viewBinding(
    createMethod: CreateMethod = CreateMethod.BIND,
    lifecycleAware: Boolean = false,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    Binding::class.java,
    createMethod,
    lifecycleAware,
    onViewDestroyed
)

/**
 * 创建一个与[ViewGroup][ViewGroup]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param Binding 期望的[ViewBinding]结果类
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
fun <Binding : ViewBinding> ViewGroup.viewBinding(
    viewBindingClass: Class<Binding>,
    createMethod: CreateMethod = CreateMethod.BIND,
    lifecycleAware: Boolean = false,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, Binding> = when (createMethod) {
    CreateMethod.BIND -> viewBinding(
        lifecycleAware,
        { viewGroup ->
            ViewBindingCache.getBind(
                viewBindingClass
            ).bind(viewGroup)
        },
        onViewDestroyed
    )
    CreateMethod.INFLATE -> viewBinding(
        viewBindingClass,
        attachToRoot = true,
        onViewDestroyed = onViewDestroyed
    )
}

/**
 * 创建一个与[ViewGroup][ViewGroup]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param Binding 期望的[ViewBinding]结果类
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
inline fun <reified Binding : ViewBinding> ViewGroup.viewBinding(
    attachToRoot: Boolean,
    lifecycleAware: Boolean = false,
    noinline onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    Binding::class.java,
    attachToRoot,
    lifecycleAware,
    onViewDestroyed
)

/**
 * 创建一个与[ViewGroup][ViewGroup]关联的[ViewBinding]
 * @param onViewDestroyed 视图销毁回调函数
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param Binding 期望的[ViewBinding]结果类
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][ViewGroup]中获取[LifecycleOwner]
 */
@JvmName("viewBindingFragment")
@JvmOverloads
fun <Binding : ViewBinding> ViewGroup.viewBinding(
    viewBindingClass: Class<Binding>,
    attachToRoot: Boolean,
    lifecycleAware: Boolean = false,
    onViewDestroyed: (Binding) -> Unit = emptyVbCallback(),
): ViewBindingProperty<ViewGroup, Binding> = viewBinding(
    lifecycleAware,
    { viewGroup ->
        ViewBindingCache.getInflateWithLayoutInflater(
            viewBindingClass
        ).inflate(
            LayoutInflater.from(context),
            viewGroup,
            attachToRoot
        )
    },
    onViewDestroyed
)