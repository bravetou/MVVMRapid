package com.brave.mvvm.example.ui.activity

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.brave.mvvmrapid.core.common.CommonViewModel
import kotlinx.coroutines.*

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
    val helloWorld = MutableLiveData("hello world!")
    val helloVisibility = MutableLiveData(View.VISIBLE)

    fun testScope() {

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.d(
                "exceptionHandler2",
                "${coroutineContext[CoroutineName]?.name ?: ""} 处理异常 ：$throwable"
            )
        }

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

            }
        })
    }
}