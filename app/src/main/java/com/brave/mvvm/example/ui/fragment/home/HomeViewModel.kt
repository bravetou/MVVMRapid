package com.brave.mvvm.example.ui.fragment.home

import android.app.Application
import android.util.Log
import com.brave.mvvmrapid.core.common.CommonViewModel
import com.brave.mvvmrapid.utils.launchScope
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * ***author***     ：brave tou
 *
 * ***blog***       ：https://blog.csdn.net/bravetou
 *
 * ***time***       ：2022/9/29 17:00
 *
 * ***desc***       ：Home ViewModel
 */
@OptIn(DelicateCoroutinesApi::class)
class HomeViewModel(application: Application) : CommonViewModel(application) {
    companion object {
        private const val TAG = "HomeViewModel"
    }

    private fun log(msg: String) =
        println("${Thread.currentThread().name} => " + msg)

    /**
     * 协程
     */
    fun testScope() {
        launch(true, {
            supervisorScope {
                launch(CoroutineName("异常子协程")) {
                    Log.d(TAG, "${Thread.currentThread().name} => 我要开始抛异常了")
                    throw NullPointerException("空指针异常")
                }

                for (index in 0..10) {
                    launch(CoroutineName("子协程")) {
                        delay(1000L * index)
                        if (index == 9) {
                            supervisorScope {
                                for (position in 0..10) {
                                    launch(CoroutineName("孙协程")) {
                                        delay(1000L * position)
                                        if (position % 2 == 0) {
                                            throw NullPointerException("孙协程${position}空指针异常")
                                        } else {
                                            Log.d(
                                                TAG,
                                                "${Thread.currentThread().name} => 正常执行 + $position"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (index % 3 == 0) {
                            throw NullPointerException("子协程${index}空指针异常")
                        } else {
                            Log.d(TAG, "${Thread.currentThread().name} => 正常执行 + $index")
                        }
                    }
                }

                launch(it) {
                    async {
                        throw NullPointerException("测试async异常拦截")
                    }
                }
            }
        })
    }

    /**
     * 调度器和线程
     */
    fun testSchedulersAndThreads() {
        launch(false, {
            launch {
                println("main runBlocking in ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Default) {
                println("Dispatchers.Default in ${Thread.currentThread().name}")
            }
            launch(Dispatchers.IO) {
                println("Dispatchers.IO in ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Unconfined) {
                println("Dispatchers.Unconfined in ${Thread.currentThread().name}")
            }
            launch(newSingleThreadContext("NewThread")) {
                println("newSingleThreadContext in ${Thread.currentThread().name}")
            }
        })
    }

    /**
     * Unconfined调度器与confined调度器
     */
    fun testSchedulersUnconfinedAndConfined() {
        launch(false, {
            launch {
                println("main runBlocking in ${Thread.currentThread().name}")
                delay(1200)
                println("main after runBlocking in ${Thread.currentThread().name}")
            }
            launch(Dispatchers.Unconfined) {
                println("Dispatchers.Unconfined in ${Thread.currentThread().name}")

                delay(1200)   //恢复线程中的协程
                println("Dispatchers after.Unconfined in ${Thread.currentThread().name}")
            }
        })
    }

    /**
     * 在线程间切换
     */
    fun testSwitchBetweenThreads() {
        launch(false, {
            newSingleThreadContext("Ctx1").use { ctx1 ->
                newSingleThreadContext("Ctx2").use { ctx2 ->
                    // 在Ctx1线程执行协程
                    withContext(ctx1) {
                        log("this is ctx1")
                        // 切换线程到Ctx2继续执行协程，依然保持在另外一个协程中
                        withContext(ctx2) {
                            log("this is ctx2")
                        }
                    }
                }
            }
        })
    }

    /**
     * 线程池调度器
     */
    fun testThreadPoolScheduler() {
        val dispatcher = newFixedThreadPoolContext(24, "CPool")
        launch(false, {
            coroutineScope {
                repeat(1000) {
                    launchScope(dispatcher, block = {
                        // 不断计算
                        List(1000) { Random.nextLong() }.maxOrNull()
                        val threadName = Thread.currentThread().name
                        println("Running on thread: $threadName")
                    })
                }
            }
        })
    }
}