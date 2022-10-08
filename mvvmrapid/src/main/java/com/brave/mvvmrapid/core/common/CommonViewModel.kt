package com.brave.mvvmrapid.core.common

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.brave.mvvmrapid.core.CommonConfig
import com.brave.mvvmrapid.core.livedata.SingleLiveData
import kotlinx.coroutines.*
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

    companion object {
        private val TAG = CommonViewModel::class.java.simpleName
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onCreate: <$this>")
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onStart: <$this>")
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onResume: <$this>")
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onPause: <$this>")
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onStop: <$this>")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (CommonConfig.DEBUG) {
            LogUtils.dTag(TAG, "onDestroy: <$this>")
        }
    }

    /**
     * 网络请求UI事件
     */
    open val defUI: UIChange by lazy { UIChange() }

    /**
     * 网络请求UI事件
     */
    open inner class UIChange {
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
     * 启动具有[CommonViewModel]生命周期的协程。
     * 因为异常会导致父协程被取消执行，
     * 同时导致后续的所有子协程都没有执行完成(可能偶尔有个别会执行完)。
     * 若需执行完后续的协程需使用[supervisorScope]包裹所有子协程，
     * 同理子协程中的子协程也需使用[supervisorScope]来包裹。
     * @param isShowLoading 显示加载中的弹窗（默认不显示）
     * @param block 协程作用域
     * @param onError 加载失败的回调，[onError]返回true则消费掉[UIChange.onError]事件，
     * 可在[handleError]无法满足需求时使用
     * @param onStart 开始加载的回调，加载数据前执行
     * @param onComplete 终止事件的回调
     * @param loadingText 加载文本
     */
    @JvmOverloads
    fun launch(
        isShowLoading: Boolean = false,
        block: suspend CoroutineScope.() -> Unit,
        onError: (Throwable) -> Boolean = { false },
        onStart: () -> Unit = { },
        onComplete: () -> Unit = { },
        loadingText: String? = null
    ): Job {
        // 创建处理协程抛出的异常的函数（可以处理本作用域及以下所有作用域的异常）
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            if (CommonConfig.DEBUG) {
                val name = coroutineContext[CoroutineName]?.name ?: TAG
                Log.d("CoroutineException", "$name 处理异常 => $throwable")
            }
            // 拦截非任务取消异常的错误信息
            if (throwable !is CancellationException) {
                if (!onError(handleError(throwable))) {
                    defUI.onError.value = throwable
                }
            }
        }
        // 启动拥有当前*CommonViewModel*生命周期的协程
        return viewModelScope.launch(exceptionHandler) {
            if (isShowLoading) {
                defUI.onStart.value = loadingText
            }
            onStart()
            block()
            defUI.onComplete.call()
            onComplete()
        }
    }

    /**
     * 启动无生命周期的协程，请不要在成功、失败的回调中处理ui！
     * 因为异常会导致父协程被取消执行，
     * 同时导致后续的所有子协程都没有执行完成(可能偶尔有个别会执行完)。
     * 若需执行完后续的协程需使用[supervisorScope]包裹所有子协程，
     * 同理子协程中的子协程也需使用[supervisorScope]来包裹。
     * @param block 协程作用域
     * @param onError 加载失败的回调，[onError]返回true则消费掉[UIChange.onError]事件，
     * 可在[handleError]无法满足需求时使用
     * @param onStart 开始加载的回调，加载数据前执行
     * @param onComplete 终止事件的回调
     */
    @OptIn(DelicateCoroutinesApi::class)
    @JvmOverloads
    fun launchNoScope(
        block: suspend CoroutineScope.() -> Unit,
        onError: (Throwable) -> Boolean = { false },
        onStart: () -> Unit = { },
        onComplete: () -> Unit = { }
    ): Job {
        // 创建处理协程抛出的异常的函数（可以处理本作用域及以下所有作用域的异常）
        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            if (CommonConfig.DEBUG) {
                val name = coroutineContext[CoroutineName]?.name ?: TAG
                Log.d("CoroutineException", "$name 处理异常 => $throwable")
            }
            // 拦截含任务取消异常在内的所有错误信息
            onError(handleError(throwable))
        }
        // 启动无生命周期的协程
        val context = Dispatchers.Main.immediate + SupervisorJob() + exceptionHandler
        return GlobalScope.launch(context) {
            onStart()
            block()
            onComplete()
        }
    }

    /**
     * 统一错误处理
     * @param e 异常
     * @return 异常
     */
    open fun handleError(e: Throwable): Throwable = e
}