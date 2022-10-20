@file:JvmName("ReflectionViewGroupBindings")

package com.brave.viewbindingdelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param Binding 期望的[ViewBinding]结果类
 * @param createMethod 创建[ViewBinding]的方法
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
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
 * 创建与[ViewGroup]关联的新[ViewBinding]
 *
 * @param viewBindingClass 期望的[ViewBinding]结果类
 * @param createMethod 创建[ViewBinding]的方法
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
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
 * 用[ViewGroup][this]作为父元素膨胀新的[ViewBinding]
 *
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
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
 * 用[ViewGroup][this]作为父元素膨胀新的[ViewBinding]
 *
 * @param lifecycleAware 使用[ViewTreeLifecycleOwner]从[ViewGroup][this]中获取[LifecycleOwner]
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