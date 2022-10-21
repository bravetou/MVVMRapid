@file:Suppress("UNCHECKED_CAST")

package com.brave.viewbindingdelegate

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ViewBindingProperty<in R : Any, out Binding : ViewBinding> :
    ReadOnlyProperty<R, Binding> {
    @MainThread
    fun clear()
}

@RestrictTo(LIBRARY_GROUP)
open class LazyViewBindingProperty<in R : Any, out Binding : ViewBinding>(
    private val onViewDestroyed: (Binding) -> Unit,
    protected val viewBinder: (R) -> Binding,
) : ViewBindingProperty<R, Binding> {
    constructor(viewBinder: (R) -> Binding) : this({}, viewBinder)

    protected var viewBinding: Any? = null

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): Binding =
        viewBinding as? Binding ?: viewBinder(thisRef).also { viewBinding ->
            this.viewBinding = viewBinding
        }

    @MainThread
    @CallSuper
    override fun clear() {
        val viewBinding = this.viewBinding as Binding?
        if (viewBinding != null) {
            onViewDestroyed(viewBinding)
        }
        this.viewBinding = null
    }
}

@RestrictTo(LIBRARY_GROUP)
open class EagerViewBindingProperty<in R : Any, out Binding : ViewBinding>(
    private val viewBinding: Binding
) : ViewBindingProperty<R, Binding> {
    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): Binding =
        viewBinding

    @MainThread
    override fun clear() {
        // Do nothing
    }
}

@RestrictTo(LIBRARY_GROUP)
abstract class LifecycleViewBindingProperty<in R : Any, out Binding : ViewBinding>(
    private val viewBinder: (R) -> Binding,
    private val onViewDestroyed: (Binding) -> Unit,
) : ViewBindingProperty<R, Binding> {
    private var viewBinding: Binding? = null

    protected abstract fun getLifecycleOwner(thisRef: R): LifecycleOwner

    @MainThread
    override fun getValue(thisRef: R, property: KProperty<*>): Binding {
        viewBinding?.let { return it }
        if (!isViewInitialized(thisRef)) {
            error(ERROR_ACCESS_BEFORE_VIEW_READY)
        }
        if (ViewBindingPropertyDelegate.strictMode) {
            runStrictModeChecks(thisRef)
        }
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        return if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            this.viewBinding = null
            Log.w(TAG, ERROR_ACCESS_AFTER_DESTROY)
            // 可以在[Fragment.onDestroyView]被调用后访问[ViewBinding]，但是不保存，以防止内存泄漏
            viewBinder(thisRef)
        } else {
            val viewBinding = viewBinder(thisRef)
            lifecycle.addObserver(ClearOnDestroyLifecycleObserver(this))
            this.viewBinding = viewBinding
            viewBinding
        }
    }

    private fun runStrictModeChecks(thisRef: R) {
        val lifecycle = getLifecycleOwner(thisRef).lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            error(ERROR_ACCESS_AFTER_DESTROY)
        }
    }

    /**
     * 视图初始化结果
     */
    protected open fun isViewInitialized(thisRef: R): Boolean =
        true

    @MainThread
    @CallSuper
    override fun clear() {
        checkMainThread()
        val viewBinding = viewBinding
        this.viewBinding = null
        if (viewBinding != null) {
            onViewDestroyed(viewBinding)
        }
    }

    internal fun postClear() {
        if (!mainHandler.post { clear() }) {
            clear()
        }
    }

    private class ClearOnDestroyLifecycleObserver(
        private val property: LifecycleViewBindingProperty<*, *>
    ) : DefaultLifecycleObserver {
        /**
         * 一些版本存在BUG：
         *
         * 解决方案1 - 覆盖所有[DefaultLifecycleObserver]的方法
         *
         * 解决方案2 - 用[LifecycleObserver][androidx.lifecycle.LifecycleObserver]替换[DefaultLifecycleObserver]
         */
        override fun onCreate(owner: LifecycleOwner) {
            // Do nothing
        }

        override fun onStart(owner: LifecycleOwner) {
            // Do nothing
        }

        override fun onResume(owner: LifecycleOwner) {
            // Do nothing
        }

        override fun onPause(owner: LifecycleOwner) {
            // Do nothing
        }

        override fun onStop(owner: LifecycleOwner) {
            // Do nothing
        }

        @MainThread
        override fun onDestroy(owner: LifecycleOwner) {
            property.postClear()
        }
    }

    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
    }
}

/**
 * TAG
 */
private const val TAG =
    "ViewBindingProperty"

/**
 * 视图未创建
 */
private const val ERROR_ACCESS_BEFORE_VIEW_READY =
    "Host view isn't ready to create a ViewBinding instance"

/**
 * 视图已销毁
 */
private const val ERROR_ACCESS_AFTER_DESTROY =
    "Accessing viewBinding after Lifecycle is destroyed or hasn't been created yet. The instance of viewBinding isn't cached."