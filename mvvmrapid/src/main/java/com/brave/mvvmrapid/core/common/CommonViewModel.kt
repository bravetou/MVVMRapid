package com.brave.mvvmrapid.core.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.brave.mvvmrapid.core.livedata.SingleLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CancellationException

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/8 18:04
 *
 * ***desc***       ：ViewModel常用类
 */
@Suppress(
    "RedundantOverride"
)
open class CommonViewModel(
    application: Application
) : AndroidViewModel(application), ICommonViewModel {
    protected open val app: Application by lazy { getApplication() }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
    }

    /**
     * 网络请求UI事件
     */
    open val defUI: UIChange by lazy { UIChange() }

    /**
     * 网络请求UI事件
     */
    inner class UIChange {
        /**
         * 开始加载数据
         */
        val onStart by lazy { SingleLiveData<String>() }

        /**
         * 数据加载完成
         */
        val onComplete by lazy { SingleLiveData<Void>() }

        /**
         * 数据加载出错
         */
        val onError by lazy { SingleLiveData<Throwable>() }
    }

    /**
     * 启动具有生命周期的协程
     * @param isShowLoading 显示加载中的弹窗（默认不显示）
     * @param block 协程作用域
     * @param onError 加载失败的回调，[onError]返回true则消费掉[UIChange.onError]事件
     * @param onStart 开始加载的回调，加载数据前执行
     * @param onFinally 终止事件的回调，任何类型的结束都会执行
     * @param loadingText 加载文本
     */
    fun launch(
        isShowLoading: Boolean = false,
        block: suspend CoroutineScope.() -> Unit,
        onError: ((Throwable) -> Boolean)? = null,
        onStart: (() -> Unit)? = null,
        onFinally: (() -> Unit)? = null,
        loadingText: String? = null
    ): Job {
        return viewModelScope.launch {
            try {
                if (isShowLoading) {
                    defUI.onStart.value = loadingText
                }
                onStart?.invoke()
                block()
                defUI.onComplete.call()
            } catch (e: Throwable) {
                if (e !is CancellationException) {
                    if (null != onError && isActive) {
                        if (!onError(handleError(e))) {
                            defUI.onError.value = e
                        }
                    } else {
                        e.printStackTrace()
                        defUI.onError.value = e
                    }
                }
            } finally {
                onFinally?.invoke()
            }
        }
    }

    /**
     * 错误处理
     */
    open fun handleError(e: Throwable): Throwable = e
}