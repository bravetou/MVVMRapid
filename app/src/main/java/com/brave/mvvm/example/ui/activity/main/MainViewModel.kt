package com.brave.mvvm.example.ui.activity.main

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
 * ***time***       ：2022/9/23 15:43
 *
 * ***desc***       ：Main ViewModel
 */
class MainViewModel(application: Application) : CommonViewModel(application) {
    fun testScope() {
        launch(true, {
            supervisorScope {

                launch(CoroutineName("异常子协程")) {
                    Log.d("${Thread.currentThread().name}", "我要开始抛异常了")
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
                                            Log.d("${Thread.currentThread().name}正常执行", "$position")
                                        }
                                    }
                                }
                            }
                        }
                        if (index % 3 == 0) {
                            throw NullPointerException("子协程${index}空指针异常")
                        } else {
                            Log.d("${Thread.currentThread().name}正常执行", "$index")
                        }
                    }
                }

                launch(it) {
                    async { throw NullPointerException("测试async异常拦截") }
                }
            }
        })
    }

    fun testDispatcher() {
        val dispatcher = newFixedThreadPoolContext(4, "CPool")
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