@file:Suppress("unused")

package com.brave.mvvmrapid.utils

import android.util.Log
import com.brave.mvvmrapid.core.CommonConfig
import kotlinx.coroutines.*

private const val TAG = "ScopeHelper"

/**
 * 异常处理
 */
private fun handlerException(onError: (Throwable) -> Unit) =
    CoroutineExceptionHandler { coroutineContext, throwable ->
        if (CommonConfig.DEBUG) {
            val name = coroutineContext[CoroutineName]?.name ?: TAG
            Log.d("CoroutineException", "$name 处理异常 => $throwable")
        }
        // 拦截含任务取消异常在内的所有错误信息
        onError(throwable)
    }

/**
 * 启动指定的调度器协程
 *
 * @see Dispatchers.Default CPU 密集型
 * @see Dispatchers.IO 阻塞线程
 * @see Dispatchers.Unconfined 不局限于任何特定线程
 * @see Dispatchers.Main 访问主线程，使用***immediate***可立即执行
 * @see newSingleThreadContext 单个线程
 * @see newFixedThreadPoolContext 固定大小的线程池
 *
 * @param dispatcher 调度器
 * @param block 协程作用域
 * @param onError 加载失败的回调
 * @param onStart 开始加载的回调，加载数据前执行
 * @param onComplete 终止事件的回调
 */
@JvmOverloads
fun launchScope(
    dispatcher: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> Unit,
    onError: (Throwable) -> Unit = { },
    onStart: () -> Unit = { },
    onComplete: () -> Unit = { }
) = CoroutineScope(
    SupervisorJob() +
            CoroutineName(TAG) +
            dispatcher +
            handlerException(onError)
).launch {
    onStart()
    block()
    onComplete()
}