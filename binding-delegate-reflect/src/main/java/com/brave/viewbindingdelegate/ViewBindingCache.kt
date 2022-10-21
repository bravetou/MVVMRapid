@file:Suppress("FunctionName", "UNCHECKED_CAST", "unused")

package com.brave.viewbindingdelegate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

object ViewBindingCache {
    private val inflateCache =
        mutableMapOf<Class<out ViewBinding>, InflateViewBinding<ViewBinding>>()
    private val bindCache =
        mutableMapOf<Class<out ViewBinding>, BindViewBinding<ViewBinding>>()

    @RestrictTo(LIBRARY)
    internal fun <Binding : ViewBinding> getInflateWithLayoutInflater(
        viewBindingClass: Class<Binding>
    ): InflateViewBinding<Binding> = inflateCache.getOrPut(
        viewBindingClass
    ) {
        InflateViewBinding(viewBindingClass)
    } as InflateViewBinding<Binding>

    @RestrictTo(LIBRARY)
    internal fun <Binding : ViewBinding> getBind(
        viewBindingClass: Class<Binding>
    ): BindViewBinding<Binding> = bindCache.getOrPut(
        viewBindingClass
    ) {
        BindViewBinding(viewBindingClass)
    } as BindViewBinding<Binding>

    /**
     * 重置所有缓存数据
     */
    fun clear() {
        inflateCache.clear()
        bindCache.clear()
    }
}

/**
 * `ViewBinding.inflate(LayoutInflater, ViewGroup, Boolean)`的包装类
 */
@RestrictTo(LIBRARY)
internal abstract class InflateViewBinding<out Binding : ViewBinding>(
    private val inflateViewBinding: Method
) {
    abstract fun inflate(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): Binding
}

@RestrictTo(LIBRARY)
internal fun <Binding : ViewBinding> InflateViewBinding(
    viewBindingClass: Class<Binding>
): InflateViewBinding<Binding> = try {
    val method = viewBindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )
    FullInflateViewBinding(method)
} catch (e: NoSuchMethodException) {
    val method = viewBindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java
    )
    MergeInflateViewBinding(method)
}

@RestrictTo(LIBRARY)
internal class FullInflateViewBinding<out Binding : ViewBinding>(
    private val inflateViewBinding: Method
) : InflateViewBinding<Binding>(inflateViewBinding) {
    override fun inflate(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): Binding = inflateViewBinding(
        null,
        layoutInflater,
        parent,
        attachToParent
    ) as Binding
}

@RestrictTo(LIBRARY)
internal class MergeInflateViewBinding<out Binding : ViewBinding>(
    private val inflateViewBinding: Method
) : InflateViewBinding<Binding>(inflateViewBinding) {
    override fun inflate(
        layoutInflater: LayoutInflater,
        parent: ViewGroup?,
        attachToParent: Boolean
    ): Binding {
        require(attachToParent) {
            "${InflateViewBinding::class.java.simpleName} supports inflate only with attachToParent=true"
        }
        return inflateViewBinding(
            null,
            layoutInflater,
            parent
        ) as Binding
    }
}

/**
 * `ViewBinding.bind(View)`的包装类
 */
@RestrictTo(LIBRARY)
internal class BindViewBinding<out VB : ViewBinding>(
    viewBindingClass: Class<VB>
) {
    private val bindViewBinding = viewBindingClass.getMethod(
        "bind",
        View::class.java
    )

    fun bind(view: View): VB = bindViewBinding(
        null,
        view
    ) as VB
}